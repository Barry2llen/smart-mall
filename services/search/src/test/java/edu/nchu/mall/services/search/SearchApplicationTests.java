package edu.nchu.mall.services.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nchu.mall.services.search.document.Product;
import edu.nchu.mall.services.search.dto.ProductSearchParam;
import edu.nchu.mall.services.search.dto.ProductSearchResult;
import edu.nchu.mall.services.search.service.support.ProductMessageConverter;
import edu.nchu.mall.services.search.service.support.ProductSearchMapper;
import edu.nchu.mall.services.search.utils.QueryUtils;
import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.query.Query;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SearchApplicationTests {

    @Test
    void productMessageConverterShouldConvertLinkedHashMapPayload() {
        ProductMessageConverter converter = new ProductMessageConverter(new ObjectMapper());

        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();
        payload.put("spuId", "1001");
        payload.put("spuName", "iPhone 17");

        List<Product> products = converter.toProducts(List.of(payload));

        assertEquals(1, products.size());
        assertEquals("1001", products.get(0).getSpuId());
        assertEquals("1001", products.get(0).getId());
        assertEquals("iPhone 17", products.get(0).getSpuName());
    }

    @Test
    void productSearchMapperShouldMapSpuCardFields() {
        Product product = new Product();
        product.setSpuId("2001");
        product.setDefaultSkuId("3001");
        product.setSpuName("MateBook X");
        product.setDefaultImage("cover.jpg");
        product.setMinPrice(new BigDecimal("5999"));
        product.setMaxPrice(new BigDecimal("7999"));
        product.setSaleCount(18L);
        product.setHasStock(true);
        product.setHotScore(99L);
        product.setBrandId("12");
        product.setBrandName("Huawei");
        product.setBrandImg("brand.png");
        product.setCatalogId("225");
        product.setCatalogName("笔记本");

        ProductSearchResult.ProductItem item = new ProductSearchMapper().toProductItem(product);

        assertEquals(2001L, item.getSpuId());
        assertEquals(3001L, item.getDefaultSkuId());
        assertEquals("MateBook X", item.getSpuName());
        assertEquals(new BigDecimal("5999"), item.getMinPrice());
        assertEquals("Huawei", item.getBrandName());
        assertEquals("笔记本", item.getCatalogName());
    }

    @Test
    void queryBuilderShouldAcceptLegacyPriceAndSortParams() throws Exception {
        ProductSearchParam param = new ProductSearchParam();
        param.setKeyword("iPhone");
        param.setCatalogId(225L);
        param.setHasStock(1);
        param.setSkuPrice("3000_6000");
        param.setSort(List.of("skuPrice_desc"));
        param.setPageNum(2);
        param.setPageSize(20);

        Query query = new QueryUtils.ProductQuery().buildQuery(param);

        assertNotNull(query);
        assertNotNull(query.getPageable());
        assertEquals(2, query.getPageable().getPageNumber());
        assertEquals(20, query.getPageable().getPageSize());
    }
}
