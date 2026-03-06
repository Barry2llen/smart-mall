package edu.nchu.mall.models.to.mq;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "秒杀订单消息")
public class FlashSaleOrder {
    @Schema(description = "订单号")
    private String orderSn;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "秒杀活动场次ID")
    private Long sessionId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "随机码")
    private String randomCode;

    @Schema(description = "购买数量")
    private Integer num;

    @Schema(description = "秒杀价格")
    private BigDecimal price;
}
