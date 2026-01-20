package edu.nchu.mall.models.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("oms_order_item")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("oi")
    @Schema(description = "订单项信息")
public class OrderItem {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("order_id")
    @Schema(description = "order_id")
    private Long orderId;

    @TableField("order_sn")
    @Schema(description = "order_sn")
    private String orderSn;

    @TableField("spu_id")
    @Schema(description = "spu_id")
    private Long spuId;

    @TableField("spu_name")
    @Schema(description = "spu_name")
    private String spuName;

    @TableField("spu_pic")
    @Schema(description = "spu_pic")
    private String spuPic;

    @TableField("spu_brand")
    @Schema(description = "品牌")
    private String spuBrand;

    @TableField("category_id")
    @Schema(description = "商品分类id")
    private Long categoryId;

    @TableField("sku_id")
    @Schema(description = "商品sku编号")
    private Long skuId;

    @TableField("sku_name")
    @Schema(description = "商品sku名字")
    private String skuName;

    @TableField("sku_pic")
    @Schema(description = "商品sku图片")
    private String skuPic;

    @TableField("sku_price")
    @Schema(description = "商品sku价格")
    private BigDecimal skuPrice;

    @TableField("sku_quantity")
    @Schema(description = "商品购买的数量")
    private Integer skuQuantity;

    @TableField("sku_attrs_vals")
    @Schema(description = "商品销售属性组合（JSON）")
    private String skuAttrsVals;

    @TableField("promotion_amount")
    @Schema(description = "商品促销分解金额")
    private BigDecimal promotionAmount;

    @TableField("coupon_amount")
    @Schema(description = "优惠券优惠分解金额")
    private BigDecimal couponAmount;

    @TableField("integration_amount")
    @Schema(description = "积分优惠分解金额")
    private BigDecimal integrationAmount;

    @TableField("real_amount")
    @Schema(description = "该商品经过优惠后的分解金额")
    private BigDecimal realAmount;

    @TableField("gift_integration")
    @Schema(description = "赠送积分")
    private Integer giftIntegration;

    @TableField("gift_growth")
    @Schema(description = "赠送成长值")
    private Integer giftGrowth;
}
