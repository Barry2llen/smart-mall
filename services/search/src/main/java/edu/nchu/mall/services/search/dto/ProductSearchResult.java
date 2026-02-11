package edu.nchu.mall.services.search.dto;

import edu.nchu.mall.services.search.document.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(description = "搜索响应结果")
@NoArgsConstructor
public class ProductSearchResult {

    @Schema(description = "商品列表")
    private List<Product> products;

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
}
