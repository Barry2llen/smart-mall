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
@Document(indexName = "product")
@Schema(description = "es中的商品模型")
public class Product {

    @Id
    @Field(type = FieldType.Keyword)
    private String skuId;

    @Field(type = FieldType.Keyword)
    private String spuId;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String skuTitle;

    @Field(type = FieldType.Scaled_Float)
    private BigDecimal skuPrice;

    @Field(type = FieldType.Keyword, index = false, docValues = false)
    private String skuImg;

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

    @Field(type = FieldType.Keyword, index = false, docValues = false)
    private String brandName;

    @Field(type = FieldType.Keyword)
    private String brandImg;

    @Field(type = FieldType.Keyword, index = false, docValues = false)
    private String catalogName;

    @Field(type = FieldType.Nested)
    private List<Attr> attrs;

    @Data
    @NoArgsConstructor
    public static class Attr {
        @Field(type = FieldType.Keyword)
        private String attrId;

        @Field(type = FieldType.Keyword, index = false, docValues = false)
        private String attrName;

        @Field(type = FieldType.Keyword)
        private String attrValue;
    }
}
