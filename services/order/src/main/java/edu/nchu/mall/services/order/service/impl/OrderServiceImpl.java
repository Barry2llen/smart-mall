package edu.nchu.mall.services.order.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.components.config.ThreadPoolConfig;
import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.feign.cart.CartFeignClient;
import edu.nchu.mall.components.feign.member.MemberFeignClient;
import edu.nchu.mall.components.feign.product.ProductFeignClient;
import edu.nchu.mall.components.utils.CallTaskUtils;
import edu.nchu.mall.components.utils.RedisUtils;
import edu.nchu.mall.models.entity.MemberReceiveAddress;
import edu.nchu.mall.models.entity.Order;
import edu.nchu.mall.models.entity.OrderItem;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.Try;
import edu.nchu.mall.models.vo.CartItemVO;
import edu.nchu.mall.models.vo.SpuInfoVO;
import edu.nchu.mall.services.order.constants.RedisConstant;
import edu.nchu.mall.services.order.dao.OrderMapper;
import edu.nchu.mall.services.order.dto.OrderSubmit;
import edu.nchu.mall.services.order.service.OrderService;
import edu.nchu.mall.services.order.vo.OrderConfirm;
import edu.nchu.mall.services.order.vo.OrderCreateVO;
import edu.nchu.mall.services.order.vo.OrderItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "order")
@Transactional
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    CallTaskUtils callTaskUtils;

    @Autowired
    MemberFeignClient memberFeignClient;

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    @Qualifier(ThreadPoolConfig.VTHREAD_POOL_NAME)
    Executor executor;

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Order entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public Order getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public OrderConfirm confirmOrder(Long memberId) {

        // 1. 获取收货地址列表
        var addressTask = callTaskUtils.rcall(() -> memberFeignClient.getMemberReceiveAddress(memberId));

        // 2. 获取订单项列表
        var itemTask = callTaskUtils.rcall(() -> cartFeignClient.getRefreshedCartItems(memberId));

        CompletableFuture.allOf(addressTask, itemTask).join();
        var addressTry = addressTask.join();
        var itemTry = itemTask.join();
        if (Try.any(Try::failed, addressTry, itemTry)) {
            throw new CustomException("获取收货地址列表或订单项列表失败");
        }

        List<MemberReceiveAddress> addresses = addressTry.getValue();
        List<OrderItemVO> items = itemTry.getValue().stream()
                .filter(CartItemVO::getSelected)
                .filter(item -> item.getStock() >= item.getCount())
                .map(item -> {
                    OrderItemVO vo = new OrderItemVO();
                    BeanUtils.copyProperties(item, vo);
                    return vo;
                }).toList();
        OrderConfirm orderConfirm = new OrderConfirm();
        orderConfirm.setAddresses(addresses);
        orderConfirm.setItems(items);

        String token = UUID.randomUUID().toString();
        orderConfirm.setToken(token);
        try {
            redisTemplate.opsForValue().set(RedisConstant.ORDER_CONFIRM_TOKEN_PREFIX + memberId, token, RedisConstant.ORDER_CONFIRM_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        } catch (Exception e){
            throw new CustomException("生成令牌失败");
        }

        return orderConfirm;
    }

    @Override
    public OrderSubmitStatus submitOrder(Long memberId, OrderSubmit orderSubmit) {
        boolean res = redisUtils.checkAndDelete(RedisConstant.ORDER_CONFIRM_TOKEN_PREFIX + memberId, orderSubmit.getToken());
        if (!res) {
            return OrderSubmitStatus.PAGE_REDIRECT;
        }

        OrderCreateVO orderCreate = new OrderCreateVO();
        String order_sn = IdWorker.getTimeId();

        // 获取收货地址信息
        var addrTask = callTaskUtils.rcall(() -> memberFeignClient.getMemberReceiveAddress(orderSubmit.getAddrId(), memberId));
        // 获取订单项信息
        var itemTask = callTaskUtils.rcall(() -> cartFeignClient.getRefreshedCartItems(memberId));

        CompletableFuture.allOf(addrTask, itemTask).join();
        var addrTry = addrTask.join();
        var itemTry = itemTask.join();

        if (!Try.allSucceeded(addrTry, itemTry)) {
            return OrderSubmitStatus.ERROR;
        }

        List<CartItemVO> cartItems = itemTry.getValue().stream()
                .filter(CartItemVO::getSelected)
                .filter(CartItemVO::available)
                .toList();
        List<Long> ids = cartItems.stream()
                .map(CartItemVO::getSkuId).toList();

        //获取spu信息
        var spuTask = callTaskUtils.rcall(() -> productFeignClient.getSpuInfoBatch(ids));
        var spuTry = spuTask.join();
        var spuInfos = spuTry.getValue();
        if (spuTry.failed() || spuInfos.size() != cartItems.size()) {
            return OrderSubmitStatus.ERROR;
        }

        // 购物项为空
        if (cartItems.isEmpty()) {
            return OrderSubmitStatus.EMPTY_CART;
        }

        var spuMap = spuInfos.stream().collect(Collectors.toMap(SpuInfoVO::getSpuId, spu -> spu));

        // spu信息不匹配
        if (cartItems.stream().anyMatch(item -> !spuMap.containsKey(item.getSpuId()))) {
            return OrderSubmitStatus.ERROR;
        }

        MemberReceiveAddress address = addrTry.getValue();
        List<OrderItem> items = cartItems.stream()
                .map(item -> buildOrderItem(item, order_sn, spuMap.get(item.getSpuId()))).toList();


    }

    private OrderItem buildOrderItem(CartItemVO item, String order_sn, SpuInfoVO spuInfo) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderSn(order_sn);
        orderItem.setSpuBrand(spuInfo.getBrandName());
        orderItem.setCategoryId(spuInfo.getCatalogId());
        orderItem.setSpuName(spuInfo.getSpuName());
        orderItem.setSkuQuantity(item.getCount());
        orderItem.setSkuPrice(item.getPrice());
        orderItem.setSpuId(item.getSpuId());
        orderItem.setSpuName(item.getTitle());
        orderItem.setSkuId(item.getSkuId());
        orderItem.setSkuName(item.getTitle());
        orderItem.setSkuPic(item.getImage());
        orderItem.setSkuAttrsVals(String.join("/", item.getSkuAttr()));
        orderItem.setGiftGrowth(item.getPrice().intValue());
        orderItem.setGiftIntegration(item.getPrice().intValue());
        return orderItem;
    }


}
