package edu.nchu.mall.services.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单列表项（按品牌聚合）中的订单项")
public class OrderListSkuItem {

    @Schema(description = "商品sku编号")
    private Long skuId;

    @Schema(description = "商品分类id")
    private Long categoryId;

    @Schema(description = "商品sku名字")
    private String skuName;

    @Schema(description = "商品sku图片")
    private String skuPic;

    @Schema(description = "商品sku价格")
    private BigDecimal skuPrice;

    @Schema(description = "商品购买的数量")
    private Integer skuQuantity;

    @Schema(description = "商品销售属性组合（'/'分隔）")
    private String skuAttrsVals;

    @Schema(description = "商品促销分解金额")
    private BigDecimal promotionAmount;

    @Schema(description = "优惠券优惠分解金额")
    private BigDecimal couponAmount;

    @Schema(description = "积分优惠分解金额")
    private BigDecimal integrationAmount;

    @Schema(description = "该商品经过优惠后的分解金额")
    private BigDecimal realAmount;

    @Schema(description = "赠送积分")
    private Integer giftIntegration;

    @Schema(description = "赠送成长值")
    private Integer giftGrowth;
}
