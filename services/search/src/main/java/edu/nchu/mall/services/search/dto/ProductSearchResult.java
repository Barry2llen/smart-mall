package edu.nchu.mall.services.search.dto;

import edu.nchu.mall.services.search.document.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "搜索响应结果")
@NoArgsConstructor
public class ProductSearchResult {

    @Schema(description = "商品列表")
    private List<ProductItem> products;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "总页数")
    private Integer pages;

    @Schema(description = "当前页码")
    private Integer pageNum;

    @Schema(description = "相关品牌")
    private List<RelatedBrand> brands;

    @Schema(description = "相关属性")
    private List<RelatedAttr> attrs;

    @Schema(description = "相关分类")
    private List<RelatedCatalog> catalogs;

    @Data
    @Schema(description = "相关品牌")
    @NoArgsConstructor
    public static class RelatedBrand {
        @Schema(description = "品牌ID")
        private Long brandId;
        @Schema(description = "品牌名称")
        private String brandName;
        @Schema(description = "品牌logo")
        private String logo;
    }

    @Data
    @Schema(description = "相关属性")
    @NoArgsConstructor
    public static class RelatedAttr {
        @Schema(description = "属性ID")
        private Long attrId;
        @Schema(description = "属性名称")
        private String attrName;
        @Schema(description = "属性的可选值")
        private List<String> attrValue;
    }

    @Data
    @Schema(description = "相关分类")
    @NoArgsConstructor
    public static class RelatedCatalog {
        @Schema(description = "分类ID")
        private Long catalogId;
        @Schema(description = "分类名称")
        private String catalogName;
    }

    @Data
    @Schema(description = "SPU商品项")
    @NoArgsConstructor
    public static class ProductItem {
        @Schema(description = "spuId")
        private Long spuId;

        @Schema(description = "默认跳转的skuId")
        private Long defaultSkuId;

        @Schema(description = "spu名称")
        private String spuName;

        @Schema(description = "默认图片")
        private String defaultImage;

        @Schema(description = "最低价")
        private BigDecimal minPrice;

        @Schema(description = "最高价")
        private BigDecimal maxPrice;

        @Schema(description = "销量")
        private Long saleCount;

        @Schema(description = "是否有货")
        private Boolean hasStock;

        @Schema(description = "热度")
        private Long hotScore;

        @Schema(description = "品牌ID")
        private Long brandId;

        @Schema(description = "品牌名称")
        private String brandName;

        @Schema(description = "品牌logo")
        private String brandImg;

        @Schema(description = "分类ID")
        private Long catalogId;

        @Schema(description = "分类名称")
        private String catalogName;
    }
}
