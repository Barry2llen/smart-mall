package edu.nchu.mall.services.flash_sale.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "秒杀活动场次关联的商品信息")
public class SessionRelatedSkuInfoVO {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "促销活动id")
    private Long promotionId;

    @Schema(description = "商品spu id")
    private Long spuId;

    @Schema(description = "商品sku id")
    private Long skuId;

//    @Schema(description = "秒杀随机码，唯一且不变，用于秒杀请求验证")
//    private String randomCode;

    @Schema(description = "秒杀价格")
    private BigDecimal seckillPrice;

    @Schema(description = "秒杀总量")
    private Integer seckillCount;

    @Schema(description = "每人限购数量")
    private Integer seckillLimit;

    @Schema(description = "排序")
    private Integer seckillSort;

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
