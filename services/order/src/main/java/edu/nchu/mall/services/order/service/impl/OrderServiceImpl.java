package edu.nchu.mall.services.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import edu.nchu.mall.models.vo.PayVo;
import edu.nchu.mall.models.vo.SpuInfoVO;
import edu.nchu.mall.services.order.constants.RedisConstant;
import edu.nchu.mall.services.order.dao.OrderMapper;
import edu.nchu.mall.services.order.dto.OrderSubmit;
import edu.nchu.mall.services.order.exception.StockNotEnoughException;
import edu.nchu.mall.services.order.service.OrderItemService;
import edu.nchu.mall.services.order.service.OrderService;
import edu.nchu.mall.services.order.utils.OrderContext;
import edu.nchu.mall.services.order.vo.OrderConfirm;
import edu.nchu.mall.services.order.vo.OrderItemVO;
import edu.nchu.mall.services.order.vo.OrderWithItems;
//import org.apache.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
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
    RabbitTemplate rabbitTemplate;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    OrderItemService orderItemService;

    @CacheEvict(key = "'list:' + #memberId")
    public void deleteOrderListCache(Long memberId) {
    }

    @Override
    public Order getBySn(String sn) {
        LambdaQueryWrapper<Order> qw = Wrappers.lambdaQuery();
        qw.eq(Order::getOrderSn, sn);
        return getOne(qw);
    }

    @Override
    @Cacheable(key = "'list:' + #memberId")
    public List<OrderWithItems> listByMemberId(Long memberId) {
        List<Order> orders = this.list(
                Wrappers.<Order>lambdaQuery()
                        .eq(Order::getMemberId, memberId)
                        .orderByDesc(Order::getCreateTime));
        if (orders.isEmpty()) {
            return List.of();
        }

        List<String> orderSns = orders.stream()
                .map(Order::getOrderSn)
                .filter(Objects::nonNull)
                .toList();
        if (orderSns.isEmpty()) {
            return orders.stream()
                    .map(order -> new OrderWithItems(order, List.of()))
                    .toList();
        }

        Map<String, List<OrderItem>> itemsByOrderSn = orderItemService.list(
                        Wrappers.<OrderItem>lambdaQuery()
                                .in(OrderItem::getOrderSn, orderSns)
                                .orderByAsc(OrderItem::getId))
                .stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderSn));

        return orders.stream()
                .map(order -> new OrderWithItems(
                        order,
                        itemsByOrderSn.getOrDefault(order.getOrderSn(), Collections.emptyList())))
                .toList();
    }

    @Override
    public Order getBySn(Long memberId, String sn) {
        LambdaQueryWrapper<Order> qw = Wrappers.lambdaQuery();
        qw.eq(Order::getOrderSn, sn);
        qw.eq(Order::getMemberId, memberId);
        return getOne(qw);
    }

    @Override
    public Order releaseOrder(Long orderId) {
        LambdaQueryWrapper<Order> qw = Wrappers.lambdaQuery();
        qw.eq(Order::getId, orderId);
        Order order = getOne(qw);
        if (order == null) {
            log.error("订单[id={}]不存在", orderId);
            throw new CustomException("订单不存在");
        }

        if (order.getStatus() == OrderStatus.UNPAID) {
            LambdaUpdateWrapper<Order> uw = Wrappers.lambdaUpdate();
            uw.eq(Order::getId, orderId);
            uw.set(Order::getStatus, OrderStatus.CLOSED);
            uw.set(Order::getModifyTime, LocalDateTime.now());
            uw.eq(Order::getStatus, OrderStatus.UNPAID);
            this.update(uw);
        }

        deleteOrderListCache(order.getMemberId());

        return order;
    }

    @Override
    //@CacheEvict
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
    public PayVo getOrderPay(Long memberId, String orderSn) throws Exception {
        Order order = getBySn(memberId, orderSn);
        if (order == null) {
            log.error("订单[orderSn={}]不存在", orderSn);
            throw new Exception("订单不存在");
        }

        OrderItem item = orderItemService.getOne(Wrappers.<OrderItem>lambdaQuery().eq(OrderItem::getOrderSn, orderSn).select(OrderItem::getSpuName));
        if (item == null) {
            log.error("订单项[orderSn={}]不存在", orderSn);
            throw new Exception("订单项不存在");
        }

        BigDecimal amount = order.getPayAmount().setScale(2, RoundingMode.HALF_UP);
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderSn);
        payVo.setTotal_amount(amount.toString());
        payVo.setSubject(item.getSpuName().substring(0, 10) + "...");
        payVo.setBody(order.getNote());

        return payVo;
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
    @CacheEvict(key = "'list:' + #memberId")
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
                .map(CartItemVO::getSpuId).toList();

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

        var spuMap = spuTask.join().getValue();

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
        } catch (CustomException | AmqpException e) {
            return OrderSubmitStatus.ERROR;
        }

        OrderContext.ORDER_SN.set(order_sn);
        return OrderSubmitStatus.OK;

    }

    //@GlobalTransactional
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

        rabbitTemplate.convertAndSend("order.event.exchange", "order.create", order);
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
