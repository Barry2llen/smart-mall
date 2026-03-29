package edu.nchu.mall.models.document;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class EsSpuProduct {
    private String id;

    private Long spuId;

    private Long defaultSkuId;

    private String spuName;

    private String defaultImage;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private List<BigDecimal> skuPrices;

    private List<Long> skuIds;

    private List<String> skuTitles;

    private Long saleCount;

    private Boolean hasStock;

    private Long hotScore;

    private Long brandId;

    private Long catalogId;

    private String brandName;

    private String brandImg;

    private String catalogName;

    private List<Attr> attrs;

    @Data
    @NoArgsConstructor
    public static class Attr {
        private Long attrId;

        private String attrName;

        private String attrValue;
    }
}
