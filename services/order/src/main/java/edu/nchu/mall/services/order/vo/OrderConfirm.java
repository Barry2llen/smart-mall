package edu.nchu.mall.services.order.vo;

import edu.nchu.mall.models.entity.MemberReceiveAddress;
import edu.nchu.mall.models.vo.CartItemVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
@Schema(description = "确认订单信息")
public class OrderConfirm {
    @Schema(description = "收货地址列表")
    private List<MemberReceiveAddress> addresses;

    @Schema(description = "订单项列表")
    private List<CartItemVO> items;

    @Schema(description = "获得积分")
    private Integer points;

    public Integer getPoints() {
        return getTotal().multiply(BigDecimal.valueOf(10)).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    //    @Schema(description = "优惠券列表")
//    private List<Coupon> coupons;

    @Schema(description = "订单总金额")
    private BigDecimal total;

    public BigDecimal getTotal() {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemVO item : items) {
            total = total.add(item.getTotalPrice());
        }
        return total;
    }

    @Schema(description = "应付总金额")
    private BigDecimal payTotal;

    public BigDecimal getPayTotal() {
        // TODO 计算优惠
        return getTotal();
    }
}
