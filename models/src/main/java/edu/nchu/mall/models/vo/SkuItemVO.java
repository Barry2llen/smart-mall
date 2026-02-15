package edu.nchu.mall.models.vo;

import edu.nchu.mall.models.entity.SkuImages;
import edu.nchu.mall.models.entity.SpuInfoDesc;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "商品详情信息")
public class SkuItemVO {
    @Schema(description = "sku信息")
    private SkuInfoVO skuInfo;

    @Schema(description = "sku图片")
    private List<SkuImages> images;

    @Schema(description = "商品销售属性组合")
    private List<SkuItemSaleAttrVO> saleAttr;

    @Schema(description = "spu商品介绍")
    private SpuInfoDesc desp;

    @Schema(description = "spu商品规格参数")
    private List<SpuItemAttrGroupVO> groupAttrs;


    @Data
    @Schema(description = "spu商品属性")
    public static class SpuItemAttrGroupVO {
        @Schema(description = "属性分组名称")
        private String groupName;

        @Schema(description = "属性组内属性及值")
        private List<SpuBaseAttrVO> attrs;
    }

    @Data
    @Schema(description = "规格参数属性")
    public static class SpuBaseAttrVO {
        @Schema(description = "属性id")
        private Long attrId;
        @Schema(description = "属性名")
        private String attrName;
        @Schema(description = "属性值")
        private String attrValue;
    }

}
