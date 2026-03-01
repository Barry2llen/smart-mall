package edu.nchu.mall.services.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.annotation.JsonValue;
import edu.nchu.mall.models.entity.Order;
import edu.nchu.mall.models.vo.PayVo;
import edu.nchu.mall.services.order.dto.OrderSubmit;
import edu.nchu.mall.services.order.vo.OrderConfirm;
import edu.nchu.mall.services.order.vo.OrderWithItems;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

public interface OrderService extends IService<Order> {

    OrderConfirm confirmOrder(Long memberId);

    OrderSubmitStatus submitOrder(Long memberId, OrderSubmit orderSubmit);

    Order getBySn(String sn);

    Order getBySn(Long memberId, String sn);

    Order releaseOrder(Long orderId);

    PayVo getOrderPay(Long userId, String orderSn) throws Exception;

    List<OrderWithItems> listByMemberId(Long memberId);

    enum OrderSubmitStatus{
        OK(0, "成功"),
        PAGE_REDIRECT(1, "请重新提交"),
        PRICE_CHANGED(2, "价格已改变"),
        NOT_SELECT_ITEM(3, "请选择商品"),
        NOT_ENOUGH_STOCK(4, "库存不足"),
        NOT_SELECT_ADDRESS(5, "请选择收货地址"),
        NOT_SELECT_COUPON(6, "请选择优惠券"),
        NOT_SELECT_PAYMENT(7, "请选择支付方式"),
        NOT_SELECT_SHIPPING(8, "请选择配送方式"),
        ERROR(9, "创建订单失败" ),
        EMPTY_CART(10, "购物车为空");

        @Getter
        @JsonValue
        private final int code;
        @Getter
        private final String message;
        OrderSubmitStatus(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
