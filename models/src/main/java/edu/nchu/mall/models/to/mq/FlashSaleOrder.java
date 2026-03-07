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

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "随机码")
    private String randomCode;

    @Schema(description = "购买数量")
    private Integer num;

    @Schema(description = "秒杀价格")
    private BigDecimal price;

    @Schema(description = "地址ID")
    private Long addressId;

    @Schema(description = "备注信息")
    private String note;

    @Schema(description = "所属分类id")
    private Long catalogId;

    @Schema(description = "sku名称")
    private String skuName;

    @Schema(description = "sku介绍描述")
    private String skuDesc;

    @Schema(description = "默认图片")
    private String skuDefaultImg;

    @Schema(description = "标题")
    private String skuTitle;

    @Schema(description = "副标题")
    private String skuSubtitle;

    @Schema(description = "销量")
    private Long saleCount;
}
