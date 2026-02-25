package edu.nchu.mall.services.order.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.components.config.ThreadPoolConfig;
import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.feign.cart.CartFeignClient;
import edu.nchu.mall.components.feign.member.MemberFeignClient;
import edu.nchu.mall.components.feign.product.ProductFeignClient;
import edu.nchu.mall.components.feign.ware.WareFeignClient;
import edu.nchu.mall.components.utils.CallTaskUtils;
import edu.nchu.mall.components.utils.RedisUtils;
import edu.nchu.mall.models.dto.WareSkuLock;
import edu.nchu.mall.models.entity.MemberReceiveAddress;
import edu.nchu.mall.models.entity.Order;
import edu.nchu.mall.models.entity.OrderItem;
import edu.nchu.mall.models.enums.OrderStatus;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.Try;
import edu.nchu.mall.models.vo.CartItemVO;
import edu.nchu.mall.models.vo.SkuLockResult;
import edu.nchu.mall.models.vo.SpuInfoVO;
import edu.nchu.mall.services.order.constants.RedisConstant;
import edu.nchu.mall.services.order.dao.OrderItemMapper;
import edu.nchu.mall.services.order.dao.OrderMapper;
import edu.nchu.mall.services.order.dto.OrderSubmit;
import edu.nchu.mall.services.order.exception.StockNotEnoughException;
import edu.nchu.mall.services.order.service.OrderItemService;
import edu.nchu.mall.services.order.service.OrderService;
import edu.nchu.mall.services.order.vo.OrderConfirm;
import edu.nchu.mall.services.order.dto.OrderCreate;
import edu.nchu.mall.services.order.vo.OrderItemVO;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "order")
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
    WareFeignClient wareFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    OrderItemService orderItemService;;

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

        String order_sn = IdWorker.getTimeId();
        var self = ((OrderServiceImpl) AopContext.currentProxy());

        // 获取收货地址信息
        var addrTask = callTaskUtils.rcall(() -> memberFeignClient.getMemberReceiveAddress(orderSubmit.getAddrId(), memberId));
        // 获取订单项信息
        var itemTask = callTaskUtils.rcall(() -> cartFeignClient.getRefreshedCartItems(memberId));
        // 获取会员信息
        var memberTask = callTaskUtils.rcall(() -> memberFeignClient.getMember(memberId));

        CompletableFuture.allOf(addrTask, itemTask, memberTask).join();
        var addrTry = addrTask.join();
        var itemTry = itemTask.join();
        var memberTry = memberTask.join();

        if (!Try.allSucceeded(addrTry, itemTry, memberTry)) {
            return OrderSubmitStatus.ERROR;
        }

        List<CartItemVO> cartItems = itemTry.getValue().stream()
                .filter(CartItemVO::getSelected)
                .filter(CartItemVO::available)
                .toList();
        List<Long> ids = cartItems.stream()
                .map(CartItemVO::getSkuId).toList();

        // 购物项为空
        if (cartItems.isEmpty()) {
            return OrderSubmitStatus.EMPTY_CART;
        }

        //获取spu信息
        var spuTask = callTaskUtils.rcall(() -> productFeignClient.getSpuInfoBatch(ids));
        var spuTry = spuTask.join();
        var spuInfos = spuTry.getValue();
        if (spuTry.failed() || spuInfos.size() != cartItems.size()) {
            return OrderSubmitStatus.ERROR;
        }

        var spuMap = spuInfos.stream().collect(Collectors.toMap(SpuInfoVO::getSpuId, spu -> spu));

        // spu信息不匹配
        if (cartItems.stream().anyMatch(item -> !spuMap.containsKey(item.getSpuId()))) {
            return OrderSubmitStatus.ERROR;
        }

        MemberReceiveAddress address = addrTry.getValue();
        List<OrderItem> items = cartItems.stream()
                .map(item -> buildOrderItem(item, order_sn, spuMap.get(item.getSpuId()))).toList();

        Order order = createOrder(memberId, memberTry.getValue().getUsername(), order_sn, orderSubmit, address, items);

        // 验价
        if (order.getTotalAmount().compareTo(orderSubmit.getPrice()) != 0) {
            return OrderSubmitStatus.PRICE_CHANGED;
        }

        // 保存订单
        try {
            self.saveOrder(order, address, items);
        } catch (StockNotEnoughException ex) {
            return OrderSubmitStatus.NOT_ENOUGH_STOCK;
        } catch (CustomException e) {
            return OrderSubmitStatus.ERROR;
        }

        return OrderSubmitStatus.OK;

    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public void saveOrder(Order order, MemberReceiveAddress address, List<OrderItem> items) {
        order.setModifyTime(LocalDateTime.now());
        baseMapper.insert(order);
        orderItemService.saveBatch(items);

        // 锁定库存
        WareSkuLock lock = new WareSkuLock();
        lock.setOrderSn(order.getOrderSn());
        lock.setItems(items.stream().map(WareSkuLock::fromOrderItem).toList());
        lock.setAddress(address);
        var lockTry = Try.of(wareFeignClient::lockStock, lock);
        if (lockTry.failed()) {
            throw new CustomException("锁定库存失败");
        } else if (!R.succeeded(lockTry.getValue())) {
            throw new StockNotEnoughException("库存不足");
        }
    }

    private Order createOrder(Long memberId, String username, String order_sn, OrderSubmit orderSubmit, MemberReceiveAddress address, List<OrderItem> items) {
        Order order = new Order();
        order.setMemberId(memberId);
        order.setOrderSn(order_sn);
        order.setCreateTime(LocalDateTime.now());
        order.setMemberUsername(username);
        order.setTotalAmount(items.stream()
                .map(item -> item.getSkuPrice().multiply(new BigDecimal(item.getSkuQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        // 其他金额算上了数量，不用乘以数量
        order.setPayAmount(items.stream().map(OrderItem::getRealAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setFreightAmount(BigDecimal.ZERO);
        order.setPromotionAmount(items.stream().map(OrderItem::getPromotionAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setIntegrationAmount(items.stream().map(OrderItem::getIntegrationAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setCouponAmount(items.stream().map(OrderItem::getCouponAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayType(orderSubmit.getPayment());
        order.setStatus(OrderStatus.UNPAID);
        order.setAutoConfirmDay(7);
        order.setIntegration(items.stream().map(OrderItem::getGiftIntegration).reduce(0, Integer::sum));
        order.setGrowth(items.stream().map(OrderItem::getGiftGrowth).reduce(0, Integer::sum));
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        order.setNote(orderSubmit.getNotes());
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        return order;
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
        return orderItem;
    }


}
