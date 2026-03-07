package edu.nchu.mall.services.flash_sale.vo;

import edu.nchu.mall.models.entity.MemberReceiveAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
@Schema(description = "秒杀订单确认信息")
public class OrderConfirm {
    @Schema(description = "收货地址列表")
    private List<MemberReceiveAddress> addresses;

    @Schema(description = "订单项")
    private OrderItem item;

    @Schema(description = "获得积分")
    private Integer points;

    @Schema(description = "订单总金额")
    private BigDecimal total;

    @Schema(description = "应付总金额")
    private BigDecimal payTotal;

    public BigDecimal getTotal() {
        return this.item.getTotalPrice();
    }

    public BigDecimal getPayTotal() {
        // TODO 计算优惠
        return this.getTotal();
    }

    public Integer getPoints() {
        return getTotal().multiply(BigDecimal.valueOf(10)).setScale(0, RoundingMode.HALF_UP).intValue();
    }
}
