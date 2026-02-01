package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "sku信息")
public class SkuInfoVO {

    @Schema(description = "skuId")
    private Long skuId;

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

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "销量")
    private Long saleCount;
}