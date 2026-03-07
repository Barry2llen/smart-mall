package edu.nchu.mall.services.order.dto;

import edu.nchu.mall.models.enums.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "秒杀订单提交DTO")
public class FlashSaleOrderSubmit {
    @Schema(description = "订单总价")
    private BigDecimal price;

    @Schema(description = "订单编号")
    private String orderSn;

    @Schema(description = "收货地址id")
    private Long addrId;

    @Schema(description = "支付方式")
    private Payment payment;

    @Schema(description = "备注")
    private String notes;

    @Schema(description = "购买数量")
    private Integer num;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SPU ID")
    private Long spuId;

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
