package edu.nchu.mall.services.search.document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@Document(indexName = "product_spu")
@Schema(description = "es中的商品模型")
public class Product {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String spuId;

    @Field(type = FieldType.Keyword)
    private String defaultSkuId;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String spuName;

    @Field(type = FieldType.Scaled_Float)
    private BigDecimal minPrice;

    @Field(type = FieldType.Scaled_Float)
    private BigDecimal maxPrice;

    @Field(type = FieldType.Scaled_Float)
    private List<BigDecimal> skuPrices;

    @Field(type = FieldType.Keyword)
    private List<String> skuIds;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private List<String> skuTitles;

    @Field(type = FieldType.Keyword, index = false, docValues = false)
    private String defaultImage;

    @Field(type = FieldType.Long)
    private Long saleCount;

    @Field(type = FieldType.Boolean)
    private Boolean hasStock;

    @Field(type = FieldType.Long)
    private Long hotScore;

    @Field(type = FieldType.Keyword)
    private String brandId;

    @Field(type = FieldType.Keyword)
    private String catalogId;

    @Field(type = FieldType.Keyword)
    private String brandName;

    @Field(type = FieldType.Keyword)
    private String brandImg;

    @Field(type = FieldType.Keyword)
    private String catalogName;

    @Field(type = FieldType.Nested)
    private List<Attr> attrs;

    @Data
    @NoArgsConstructor
    public static class Attr {
        @Field(type = FieldType.Keyword)
        private String attrId;

        @Field(type = FieldType.Keyword)
        private String attrName;

        @Field(type = FieldType.Keyword)
        private String attrValue;
    }
}
