package edu.nchu.mall.services.product;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import edu.nchu.mall.components.feign.ware.WareFeignClient;
import edu.nchu.mall.models.document.EsSpuProduct;
import edu.nchu.mall.models.entity.Brand;
import edu.nchu.mall.models.entity.Category;
import edu.nchu.mall.models.entity.SkuInfo;
import edu.nchu.mall.models.entity.SpuInfo;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.vo.SkuStockVO;
import edu.nchu.mall.services.product.dao.SkuInfoMapper;
import edu.nchu.mall.services.product.dao.SpuInfoMapper;
import edu.nchu.mall.services.product.service.AttrService;
import edu.nchu.mall.services.product.service.BrandService;
import edu.nchu.mall.services.product.service.CategoryService;
import edu.nchu.mall.services.product.service.ProductAttrValueService;
import edu.nchu.mall.services.product.service.SpuImagesService;
import edu.nchu.mall.services.product.service.support.SpuSearchProductBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductApplicationTests {

    @Mock
    private SpuInfoMapper spuInfoMapper;
    @Mock
    private SkuInfoMapper skuInfoMapper;
    @Mock
    private AttrService attrService;
    @Mock
    private ProductAttrValueService productAttrValueService;
    @Mock
    private SpuImagesService spuImagesService;
    @Mock
    private BrandService brandService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private WareFeignClient wareFeignClient;

    private SpuSearchProductBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new SpuSearchProductBuilder(
                spuInfoMapper,
                skuInfoMapper,
                attrService,
                productAttrValueService,
                spuImagesService,
                brandService,
                categoryService,
                wareFeignClient
        );
    }

    @Test
    void buildShouldPreferInStockLowestPriceSkuAndAggregateSpuFields() {
        Long spuId = 1L;
        when(spuInfoMapper.selectById(spuId)).thenReturn(buildSpuInfo(spuId));
        when(skuInfoMapper.selectList(any())).thenReturn(List.of(
                buildSku(11L, spuId, "标准版", "std.jpg", "4999", 3L),
                buildSku(12L, spuId, "Pro", "pro.jpg", "6999", 6L),
                buildSku(13L, spuId, "Pro Max", "pro-max.jpg", "6499", 8L)
        ));
        when(wareFeignClient.getStocksBySkuIds(List.of(11L, 12L, 13L))).thenReturn(R.success(List.of(
                buildStock(11L, 0, 0),
                buildStock(12L, 10, 0),
                buildStock(13L, 5, 0)
        )));
        when(productAttrValueService.list(any(Wrapper.class))).thenReturn(List.of());
        when(brandService.getById(9L)).thenReturn(buildBrand());
        when(categoryService.getById(225L)).thenReturn(buildCategory());

        EsSpuProduct product = builder.build(spuId);

        assertEquals(spuId, product.getSpuId());
        assertEquals(13L, product.getDefaultSkuId());
        assertEquals("pro-max.jpg", product.getDefaultImage());
        assertEquals(new BigDecimal("4999"), product.getMinPrice());
        assertEquals(new BigDecimal("6999"), product.getMaxPrice());
        assertEquals(17L, product.getSaleCount());
        assertTrue(product.getHasStock());
        assertEquals("Apple", product.getBrandName());
        assertEquals("手机", product.getCatalogName());
    }

    @Test
    void buildShouldFallbackToSpuDefaultImageWhenDefaultSkuImageMissing() {
        Long spuId = 2L;
        when(spuInfoMapper.selectById(spuId)).thenReturn(buildSpuInfo(spuId));
        when(skuInfoMapper.selectList(any())).thenReturn(List.of(
                buildSku(21L, spuId, "入门款", "", "2999", 1L),
                buildSku(22L, spuId, "高配款", "", "3999", 2L)
        ));
        when(wareFeignClient.getStocksBySkuIds(List.of(21L, 22L))).thenReturn(R.success(List.of(
                buildStock(21L, 6, 0),
                buildStock(22L, 0, 0)
        )));
        when(productAttrValueService.list(any(Wrapper.class))).thenReturn(List.of());
        when(spuImagesService.getSpuDefaultImagesBatch(List.of(spuId))).thenReturn(Map.of(spuId, "spu-default.jpg"));
        when(brandService.getById(9L)).thenReturn(buildBrand());
        when(categoryService.getById(225L)).thenReturn(buildCategory());

        EsSpuProduct product = builder.build(spuId);

        assertEquals(21L, product.getDefaultSkuId());
        assertEquals("spu-default.jpg", product.getDefaultImage());
        assertFalse(product.getSkuTitles().isEmpty());
    }

    private SpuInfo buildSpuInfo(Long spuId) {
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setId(spuId);
        spuInfo.setSpuName("iPhone");
        spuInfo.setBrandId(9L);
        spuInfo.setCatalogId(225L);
        return spuInfo;
    }

    private SkuInfo buildSku(Long skuId, Long spuId, String title, String image, String price, Long saleCount) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSkuId(skuId);
        skuInfo.setSpuId(spuId);
        skuInfo.setSkuTitle(title);
        skuInfo.setSkuDefaultImg(image);
        skuInfo.setPrice(new BigDecimal(price));
        skuInfo.setSaleCount(saleCount);
        return skuInfo;
    }

    private SkuStockVO buildStock(Long skuId, Integer stock, Integer stockLocked) {
        SkuStockVO stockVO = new SkuStockVO();
        stockVO.setSkuId(skuId);
        stockVO.setStock(stock);
        stockVO.setStockLocked(stockLocked);
        return stockVO;
    }

    private Brand buildBrand() {
        Brand brand = new Brand();
        brand.setBrandId(9L);
        brand.setName("Apple");
        brand.setLogo("apple.png");
        return brand;
    }

    private Category buildCategory() {
        Category category = new Category();
        category.setCatId(225L);
        category.setName("手机");
        return category;
    }
}
