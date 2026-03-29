package edu.nchu.mall.services.product.service.support;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.feign.ware.WareFeignClient;
import edu.nchu.mall.models.document.EsSpuProduct;
import edu.nchu.mall.models.entity.Attr;
import edu.nchu.mall.models.entity.Brand;
import edu.nchu.mall.models.entity.Category;
import edu.nchu.mall.models.entity.ProductAttrValue;
import edu.nchu.mall.models.entity.SkuInfo;
import edu.nchu.mall.models.entity.SpuInfo;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.vo.SkuStockVO;
import edu.nchu.mall.services.product.dao.SkuInfoMapper;
import edu.nchu.mall.services.product.dao.SpuInfoMapper;
import edu.nchu.mall.services.product.service.AttrService;
import edu.nchu.mall.services.product.service.BrandService;
import edu.nchu.mall.services.product.service.CategoryService;
import edu.nchu.mall.services.product.service.ProductAttrValueService;
import edu.nchu.mall.services.product.service.SpuImagesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpuSearchProductBuilder {

    private final SpuInfoMapper spuInfoMapper;
    private final SkuInfoMapper skuInfoMapper;
    private final AttrService attrService;
    private final ProductAttrValueService productAttrValueService;
    private final SpuImagesService spuImagesService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final WareFeignClient wareFeignClient;

    public EsSpuProduct build(Long spuId) {
        SpuInfo spuInfo = getRequiredSpuInfo(spuId);
        List<SkuInfo> skus = getRequiredSkus(spuId);
        Map<Long, Integer> availableStockMap = getAvailableStockMap(skus);
        SkuInfo defaultSku = resolveDefaultSku(skus, availableStockMap);
        List<BigDecimal> skuPrices = resolveSkuPrices(skus);
        PriceRange priceRange = resolvePriceRange(skuPrices);
        Brand brand = brandService.getById(spuInfo.getBrandId());
        Category category = categoryService.getById(spuInfo.getCatalogId());

        EsSpuProduct esProduct = new EsSpuProduct();
        esProduct.setId(String.valueOf(spuId));
        esProduct.setSpuId(spuId);
        esProduct.setDefaultSkuId(defaultSku != null ? defaultSku.getSkuId() : null);
        esProduct.setSpuName(spuInfo.getSpuName());
        esProduct.setDefaultImage(resolveDefaultImage(spuId, defaultSku, skus));
        esProduct.setMinPrice(priceRange.minPrice());
        esProduct.setMaxPrice(priceRange.maxPrice());
        esProduct.setSkuPrices(skuPrices);
        esProduct.setSkuIds(skus.stream().map(SkuInfo::getSkuId).filter(Objects::nonNull).toList());
        esProduct.setSkuTitles(skus.stream().map(SkuInfo::getSkuTitle).filter(StringUtils::isNotBlank).toList());
        esProduct.setSaleCount(resolveSaleCount(skus));
        esProduct.setHasStock(availableStockMap.values().stream().anyMatch(stock -> stock != null && stock > 0));
        esProduct.setHotScore(0L);
        esProduct.setBrandId(spuInfo.getBrandId());
        esProduct.setCatalogId(spuInfo.getCatalogId());
        esProduct.setBrandName(brand != null ? brand.getName() : null);
        esProduct.setBrandImg(brand != null ? brand.getLogo() : null);
        esProduct.setCatalogName(category != null ? category.getName() : null);
        esProduct.setAttrs(buildSearchAttrs(spuId));
        return esProduct;
    }

    private SpuInfo getRequiredSpuInfo(Long spuId) {
        SpuInfo spuInfo = spuInfoMapper.selectById(spuId);
        if (spuInfo == null) {
            throw new CustomException("商品不存在", null, HttpStatus.BAD_REQUEST);
        }
        return spuInfo;
    }

    private List<SkuInfo> getRequiredSkus(Long spuId) {
        List<SkuInfo> skus = skuInfoMapper.selectList(new LambdaQueryWrapper<SkuInfo>().eq(SkuInfo::getSpuId, spuId));
        if (skus == null || skus.isEmpty()) {
            throw new CustomException("无法上架没有任何sku的商品", null, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return skus;
    }

    private List<EsSpuProduct.Attr> buildSearchAttrs(Long spuId) {
        List<ProductAttrValue> attrValues = productAttrValueService.list(
                new LambdaQueryWrapper<ProductAttrValue>().eq(ProductAttrValue::getSpuId, spuId)
        );
        List<Long> attrIds = attrValues.stream()
                .map(ProductAttrValue::getAttrId)
                .filter(Objects::nonNull)
                .toList();
        if (attrIds.isEmpty()) {
            return List.of();
        }

        Set<Long> searchAttrIdSet = new HashSet<>(
                attrService.list(new LambdaQueryWrapper<Attr>()
                                .eq(Attr::getAttrType, 1)
                                .in(Attr::getAttrId, attrIds))
                        .stream()
                        .map(Attr::getAttrId)
                        .filter(Objects::nonNull)
                        .toList()
        );

        return attrValues.stream()
                .filter(v -> searchAttrIdSet.contains(v.getAttrId()))
                .map(attrValue -> {
                    EsSpuProduct.Attr esAttr = new EsSpuProduct.Attr();
                    BeanUtils.copyProperties(attrValue, esAttr);
                    return esAttr;
                })
                .toList();
    }

    private Map<Long, Integer> getAvailableStockMap(List<SkuInfo> skus) {
        R<List<SkuStockVO>> stocksBySkuIds = wareFeignClient.getStocksBySkuIds(skus.stream().map(SkuInfo::getSkuId).toList());
        if (stocksBySkuIds.getCode() != RCT.SUCCESS) {
            log.error("构建商品搜索文档时远程查询库存失败 [skuIds={}, message={}]", skus.stream().map(SkuInfo::getSkuId).toList(), stocksBySkuIds.getMsg());
            throw new CustomException("远程查询库存失败");
        }

        List<SkuStockVO> stocks = stocksBySkuIds.getData() == null ? List.of() : stocksBySkuIds.getData();
        return stocks.stream().collect(Collectors.toMap(
                SkuStockVO::getSkuId,
                stock -> Optional.ofNullable(stock.getAvailableStock()).orElse(0),
                (left, right) -> right
        ));
    }

    private SkuInfo resolveDefaultSku(List<SkuInfo> skus, Map<Long, Integer> availableStockMap) {
        return skus.stream()
                .min(Comparator
                        .comparingInt((SkuInfo sku) -> availableStockMap.getOrDefault(sku.getSkuId(), 0) > 0 ? 0 : 1)
                        .thenComparing(SkuInfo::getPrice, Comparator.nullsLast(BigDecimal::compareTo))
                        .thenComparing(SkuInfo::getSkuId, Comparator.nullsLast(Long::compareTo)))
                .orElse(null);
    }

    private List<BigDecimal> resolveSkuPrices(List<SkuInfo> skus) {
        return skus.stream()
                .map(SkuInfo::getPrice)
                .filter(Objects::nonNull)
                .toList();
    }

    private PriceRange resolvePriceRange(List<BigDecimal> skuPrices) {
        BigDecimal minPrice = skuPrices.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal maxPrice = skuPrices.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        return new PriceRange(minPrice, maxPrice);
    }

    private Long resolveSaleCount(List<SkuInfo> skus) {
        return skus.stream()
                .map(SkuInfo::getSaleCount)
                .filter(Objects::nonNull)
                .reduce(0L, Long::sum);
    }

    private String resolveDefaultImage(Long spuId, SkuInfo defaultSku, List<SkuInfo> skus) {
        if (defaultSku != null && StringUtils.isNotBlank(defaultSku.getSkuDefaultImg())) {
            return defaultSku.getSkuDefaultImg();
        }

        String spuDefaultImage = spuImagesService.getSpuDefaultImagesBatch(List.of(spuId)).get(spuId);
        if (StringUtils.isNotBlank(spuDefaultImage)) {
            return spuDefaultImage;
        }

        return skus.stream()
                .map(SkuInfo::getSkuDefaultImg)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse(null);
    }

    private record PriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
    }
}
