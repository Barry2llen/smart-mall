package edu.nchu.mall.services.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import edu.nchu.mall.models.entity.PaymentInfo;
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
import edu.nchu.mall.services.order.service.PaymentInfoService;
import edu.nchu.mall.services.order.utils.OrderContext;
import edu.nchu.mall.services.order.vo.*;
//import org.apache.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private static final String ORDER_LIST_CACHE_KEY_PREFIX = "order:list:";
    private static final String ORDER_LIST_CACHE_KEYS_PREFIX = "order:list:keys:";
    private static final long ORDER_LIST_CACHE_EXPIRE_SECONDS = 15 * 60;
    private static final long ORDER_LIST_CACHE_KEYS_EXPIRE_SECONDS = 30 * 60;

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

    @Autowired
    PaymentInfoService paymentInfoService;

    @Autowired
    ObjectMapper objectMapper;

    public void deleteOrderListCache(Long memberId) {
        String keySet = buildOrderListCacheKeysSetKey(memberId);
        try {
            var cacheKeys = redisUtils.smembers(keySet);
            if (cacheKeys != null && !cacheKeys.isEmpty()) {
                redisUtils.unlink(cacheKeys);
            }
            redisUtils.unlink(keySet);
        } catch (Exception e) {
            log.error("删除用户订单列表缓存失败 [memberId={}]", memberId, e);
        }
    }

    @Override
    public Order getBySn(String sn) {
        LambdaQueryWrapper<Order> qw = Wrappers.lambdaQuery();
        qw.eq(Order::getOrderSn, sn);
        return getOne(qw);
    }

    @Override
    public List<OrderWithItems> listByMemberId(Long memberId) {
        String cacheKey = buildOrderListCacheKey(memberId, null, null);
        return getOrderListByCache(cacheKey, memberId, () -> {
            List<Order> orders = list(Wrappers.<Order>lambdaQuery()
                    .eq(Order::getMemberId, memberId)
                    .eq(Order::getDeleteStatus, 0)
                    .orderByDesc(Order::getCreateTime));
            return buildOrderWithItems(orders);
        });
    }

    @Override
    public List<OrderWithItems> listByMemberId(Long memberId, Integer pageNum, Integer pageSize) {
        String cacheKey = buildOrderListCacheKey(memberId, pageNum, pageSize);
        return getOrderListByCache(cacheKey, memberId, () -> {
            List<Order> orders = page(new Page<>(pageNum, pageSize), Wrappers.<Order>lambdaQuery()
                    .eq(Order::getMemberId, memberId)
                    .eq(Order::getDeleteStatus, 0)
                    .orderByDesc(Order::getCreateTime)).getRecords();
            return buildOrderWithItems(orders);
        });
    }

    @Override
    public List<OrderWithItems> listByMemberId(Long memberId, Integer pageNum, Integer pageSize, Integer status, String keyword) {
        boolean hasSearch = status != null || StringUtils.hasText(keyword);
        if (!hasSearch) {
            return listByMemberId(memberId, pageNum, pageSize);
        }

        List<Order> orders = page(new Page<>(pageNum, pageSize), buildOrderSearchQuery(memberId, status, keyword))
                .getRecords();
        return buildOrderWithItems(orders);
    }

    @Override
    public OrderWithItems getOrderBySn(Long memberId, String sn, boolean includeItems) {
        Order order = getBySn(memberId, sn);
        if (order == null) {
            return null;
        }

        if (!includeItems) {
            return new OrderWithItems(order, Collections.emptyList());
        }

        List<OrderItem> orderItems = orderItemService.list(
                Wrappers.<OrderItem>lambdaQuery().eq(OrderItem::getOrderSn, sn));
        List<OrderListSpuItem> spuItems = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getSpuId))
                .values()
                .stream()
                .map(this::toOrderListSpuItem)
                .toList();
        return new OrderWithItems(order, spuItems);
    }

    @Override
    public PayStatus handleAlipayAsync(PayAsyncVo vo) throws Throwable{
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setAlipayTradeNo(vo.getTrade_no());
        paymentInfo.setOrderSn(vo.getOut_trade_no());
        paymentInfo.setPaymentStatus(vo.getTrade_status());
        paymentInfo.setCallbackTime(vo.getNotify_time());
        paymentInfo.setTotalAmount(new BigDecimal(vo.getInvoice_amount()));
        paymentInfo.setSubject(vo.getSubject());
        paymentInfo.setCreateTime(LocalDateTime.now());
        paymentInfo.setConfirmTime(LocalDateTime.now());

        String json = objectMapper.writeValueAsString(vo);
        paymentInfo.setCallbackContent(json);

        boolean res = paymentInfoService.save(paymentInfo);

        if (!res) {
            log.error("保存支付信息失败 [paymentInfo={}]", paymentInfo);
            return PayStatus.ERROR;
        }

        log.info("保存支付信息成功 [orderSn={}]", paymentInfo.getOrderSn());
        log.info("修改订单状态为已支付 [orderSn={}]", paymentInfo.getOrderSn());

        try {
            var self = ((OrderServiceImpl) AopContext.currentProxy());
            boolean updateRes = self.updateOrderStatus(paymentInfo.getOrderSn(), OrderStatus.UNSHIPPED);

            if (!updateRes) {
                log.error("修改订单状态失败 [orderSn={}]", paymentInfo.getOrderSn());
                return PayStatus.ERROR;
            }
        } catch (Exception e) {
            log.error("修改订单状态异常 [orderSn={}]", paymentInfo.getOrderSn(), e);
            throw e;
        }

        return PayStatus.SUCCESS;
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(String orderSn, OrderStatus status) {
        LambdaQueryWrapper<Order> qw = Wrappers.lambdaQuery();
        qw.select(Order::getOrderSn, Order::getStatus, Order::getMemberId);
        qw.eq(Order::getOrderSn, orderSn);
        Order order = this.getOne(qw);

        if (order == null) {
            return false;
        }

        if (order.getStatus() != OrderStatus.UNPAID) {
            return false;
        }

        LambdaUpdateWrapper<Order> uw = Wrappers.lambdaUpdate();
        uw.eq(Order::getOrderSn, orderSn);
        uw.set(Order::getModifyTime, LocalDateTime.now());

        switch (status) {
            case UNSHIPPED -> {
                uw.set(Order::getStatus, OrderStatus.UNSHIPPED);
                uw.eq(Order::getStatus, OrderStatus.UNPAID);
            }
            default -> {
                uw.set(Order::getStatus, status);
            }
        }

        boolean updateRes = this.update(uw);

        if (!updateRes) {
            return false;
        } else {
            deleteOrderListCache(order.getMemberId());
            return true;
        }
    }

    private List<OrderWithItems> buildOrderWithItems(List<Order> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> orderSns = orders.stream().map(Order::getOrderSn).filter(Objects::nonNull).toList();
        if (orderSns.isEmpty()) {
            return orders.stream().map(order -> new OrderWithItems(order, Collections.emptyList())).toList();
        }

        Map<String, List<OrderItem>> itemsByOrderSn = orderItemService.list(
                        Wrappers.<OrderItem>lambdaQuery().in(OrderItem::getOrderSn, orderSns))
                .stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderSn));

        return orders.stream().map(order -> {
            List<OrderItem> orderItems = itemsByOrderSn.getOrDefault(order.getOrderSn(), Collections.emptyList());
            List<OrderListSpuItem> spuItems = orderItems.stream()
                    .collect(Collectors.groupingBy(OrderItem::getSpuId))
                    .values()
                    .stream()
                    .map(this::toOrderListSpuItem)
                    .toList();
            return new OrderWithItems(order, spuItems);
        }).toList();
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

        boolean res = false;
        if (order.getStatus() == OrderStatus.UNPAID) {
            LambdaUpdateWrapper<Order> uw = Wrappers.lambdaUpdate();
            uw.eq(Order::getId, orderId);
            uw.set(Order::getStatus, OrderStatus.CLOSED);
            uw.set(Order::getModifyTime, LocalDateTime.now());
            uw.eq(Order::getStatus, OrderStatus.UNPAID);
            res = this.update(uw);
        }

        if (res) {
            deleteOrderListCache(order.getMemberId());
            return order;
        }

        return null;
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
        payVo.setTime_expire(LocalDateTime.now().plusSeconds(120L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

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
        var imgTask = callTaskUtils.rcall(() -> productFeignClient.getSpuDefaultImagesBatch(ids));

        CompletableFuture.allOf(spuTask, imgTask).join();

        var spuTry = spuTask.join();
        var imgTry = imgTask.join();

        var spuInfos = spuTry.getValue();
        var spuImages = imgTry.getValue();

        if (!Try.allSucceeded(spuTry, imgTry) || spuInfos.size() != cartItems.size()) {
            return OrderSubmitStatus.ERROR;
        }

        // spu信息不匹配
        if (cartItems.stream().anyMatch(item -> !spuInfos.containsKey(item.getSpuId()))) {
            return OrderSubmitStatus.ERROR;
        }

        MemberReceiveAddress address = addrTry.getValue();
        List<OrderItem> items = cartItems.stream()
                .map(item -> buildOrderItem(item, order_sn, spuInfos.get(item.getSpuId()), spuImages.get(item.getSpuId()))).toList();

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

        deleteOrderListCache(order.getMemberId());
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

    private OrderItem buildOrderItem(CartItemVO item, String order_sn, SpuInfoVO spuInfo, String spuDefaultImg) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderSn(order_sn);
        orderItem.setSpuBrand(spuInfo.getBrandName());
        orderItem.setCategoryId(spuInfo.getCatalogId());
        orderItem.setSpuName(spuInfo.getSpuName());
        orderItem.setSpuPic(spuDefaultImg);
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

    private OrderListSpuItem toOrderListSpuItem(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return new OrderListSpuItem();
        }
        OrderItem first = orderItems.get(0);
        OrderListSpuItem spuItem = new OrderListSpuItem();
        spuItem.setSpuId(first.getSpuId());
        spuItem.setSpuName(first.getSpuName());
        spuItem.setSpuPic(first.getSpuPic());
        spuItem.setSpuBrand(first.getSpuBrand());
        spuItem.setSpuItems(orderItems.stream().map(this::toOrderListSkuItem).toList());
        return spuItem;
    }

    private OrderListSkuItem toOrderListSkuItem(OrderItem orderItem) {
        OrderListSkuItem skuItem = new OrderListSkuItem();
        skuItem.setSkuId(orderItem.getSkuId());
        skuItem.setCategoryId(orderItem.getCategoryId());
        skuItem.setSkuName(orderItem.getSkuName());
        skuItem.setSkuPic(orderItem.getSkuPic());
        skuItem.setSkuPrice(orderItem.getSkuPrice());
        skuItem.setSkuQuantity(orderItem.getSkuQuantity());
        skuItem.setSkuAttrsVals(orderItem.getSkuAttrsVals());
        skuItem.setPromotionAmount(orderItem.getPromotionAmount());
        skuItem.setCouponAmount(orderItem.getCouponAmount());
        skuItem.setIntegrationAmount(orderItem.getIntegrationAmount());
        skuItem.setRealAmount(orderItem.getRealAmount());
        skuItem.setGiftIntegration(orderItem.getGiftIntegration());
        skuItem.setGiftGrowth(orderItem.getGiftGrowth());
        return skuItem;
    }

    private List<OrderWithItems> getOrderListByCache(String cacheKey, Long memberId, java.util.function.Supplier<List<OrderWithItems>> dbLoader) {
        try {
            String cacheValue = redisTemplate.opsForValue().get(cacheKey);
            if (StringUtils.hasText(cacheValue)) {
                JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, OrderWithItems.class);
                return objectMapper.readValue(cacheValue, type);
            }
        } catch (Exception e) {
            log.warn("读取订单列表缓存失败 [key={}]", cacheKey, e);
        }

        List<OrderWithItems> data = dbLoader.get();

        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(data), ORDER_LIST_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            String keySet = buildOrderListCacheKeysSetKey(memberId);
            redisUtils.sadd(keySet, cacheKey);
            redisTemplate.expire(keySet, ORDER_LIST_CACHE_KEYS_EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            log.warn("序列化订单列表缓存失败 [key={}]", cacheKey, e);
        } catch (Exception e) {
            log.warn("写入订单列表缓存失败 [key={}]", cacheKey, e);
        }

        return data;
    }

    private LambdaQueryWrapper<Order> buildOrderSearchQuery(Long memberId, Integer status, String keyword) {
        LambdaQueryWrapper<Order> qw = Wrappers.lambdaQuery();
        qw.eq(Order::getMemberId, memberId);
        qw.eq(Order::getDeleteStatus, 0);
        OrderStatus orderStatus = parseOrderStatus(status);
        if (orderStatus != null) {
            qw.eq(Order::getStatus, orderStatus);
        }
        if (StringUtils.hasText(keyword)) {
            qw.likeRight(Order::getOrderSn, keyword.trim());
        }
        qw.orderByDesc(Order::getCreateTime);
        return qw;
    }

    private OrderStatus parseOrderStatus(Integer status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case 0 -> OrderStatus.UNPAID;
            case 1 -> OrderStatus.UNSHIPPED;
            case 2 -> OrderStatus.SHIPPED;
            case 3 -> OrderStatus.COMPLETED;
            case 4 -> OrderStatus.CLOSED;
            case 5 -> OrderStatus.INVALID;
            default -> null;
        };
    }

    private String buildOrderListCacheKey(Long memberId, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return ORDER_LIST_CACHE_KEY_PREFIX + memberId + ":all";
        }
        return ORDER_LIST_CACHE_KEY_PREFIX + memberId + ":" + pageNum + ":" + pageSize;
    }

    private String buildOrderListCacheKeysSetKey(Long memberId) {
        return ORDER_LIST_CACHE_KEYS_PREFIX + memberId;
    }


}
