package edu.nchu.mall.services.product.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.feign.coupon.CouponFeignClient;
import edu.nchu.mall.components.feign.search.SearchFeignClient;
import edu.nchu.mall.components.feign.ware.WareFeignClient;
import edu.nchu.mall.models.constants.SpuStatus;
import edu.nchu.mall.models.document.EsProduct;
import edu.nchu.mall.models.dto.BoundsDTO;
import edu.nchu.mall.models.dto.SkuReductionDTO;
import edu.nchu.mall.models.dto.SpuInfoDTO;
import edu.nchu.mall.models.dto.SpuSaveDTO;
import edu.nchu.mall.models.entity.*;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.vo.SkuStockVO;
import edu.nchu.mall.services.product.dao.*;
import edu.nchu.mall.services.product.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@CacheConfig(cacheNames = "spuInfo")
@Transactional
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo> implements SpuInfoService {

    @Autowired
    SpuInfoDescMapper spuInfoDescMapper;

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    CouponFeignClient couponFeignClient;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignClient wareFeignClient;

    @Autowired
    SearchFeignClient searchFeignClient;

    private void checkResult(boolean res, String msg) {
        if (!res) {
            log.error("保存失败:" + msg);
            throw new RuntimeException("保存失败:" + msg);
        }
    }

    private void checkResult(R<?> r, String msg) {
        if (r.getCode() != RCT.SUCCESS) {
            log.error("保存失败:" + msg);
            throw new RuntimeException("保存失败:" + msg);
        }
    }

    private List<SkuInfo> getSkusBySpuId(Long spuId) {
        if (spuId == null) return List.of();
        return skuInfoMapper.selectList(new LambdaQueryWrapper<SkuInfo>().eq(SkuInfo::getSpuId, spuId));
    }

    /**
     * 上架商品
     * @param spuId spuId
     * @return boolean
     */
    private boolean putOnSale(Long spuId) {

        // 检查spu是否有对应sku
        List<SkuInfo> skus = getSkusBySpuId(spuId);
        if (skus.isEmpty()) {
            throw new CustomException("无法上架没有任何sku的商品", null, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // TODO(接口幂等) 检查商品是否下架或新建

        // 拿到需要检索的属性
        List<ProductAttrValue> attrValues = productAttrValueService.list(new LambdaQueryWrapper<ProductAttrValue>().eq(ProductAttrValue::getSpuId, spuId));
        List<Long> attrIds = attrValues.stream().map(ProductAttrValue::getAttrId).toList();
        if (attrIds.isEmpty()) attrIds = List.of(0L);
        List<Long> searchAttrIds = attrService.list(new LambdaQueryWrapper<Attr>().eq(Attr::getAttrType, 1).in(Attr::getAttrId, attrIds)).stream().map(Attr::getAttrId).toList();
        Set<Long> searchAttrIdSet = new HashSet<>(searchAttrIds);
        List<EsProduct.Attr> esAttrs = attrValues.stream()
                .filter(v -> searchAttrIdSet.contains(v.getAttrId()))
                .map(attrValue -> {
                    EsProduct.Attr esAttr = new EsProduct.Attr();
                    BeanUtils.copyProperties(attrValue, esAttr);
                    return esAttr;
                }).toList();

        List<EsProduct> esProducts = skus.stream().map(sku -> {
            EsProduct esProduct = new EsProduct();
            BeanUtils.copyProperties(sku, esProduct);
            esProduct.setSkuPrice(sku.getPrice());
            esProduct.setSkuImg(sku.getSkuDefaultImg());
            esProduct.setHotScore(0L);
            esProduct.setAttrs(esAttrs);
            return esProduct;
        }).toList();

        List<Brand> brands = brandService.seqByIds(esProducts.stream().map(EsProduct::getBrandId).toList());
        List<String> brandNames = brands.stream().map(brand -> brand != null ? brand.getName() : null).toList();
        List<String> brandImgs = brands.stream().map(brand -> brand != null ? brand.getLogo() : null).toList();
        List<String> catalogNames = categoryService.seqByIds(esProducts.stream().map(EsProduct::getCatalogId).toList())
                .stream().map(category -> category != null ? category.getName() : null).toList();

        // 远程查询是否有库存
        R<List<SkuStockVO>> stocksBySkuIds = wareFeignClient.getStocksBySkuIds(skus.stream().map(SkuInfo::getSkuId).toList());
        checkResult(stocksBySkuIds, "远程查询库存失败");
        Map<Long, Integer> collect = stocksBySkuIds.getData().stream().collect(Collectors.toMap(SkuStockVO::getSkuId, stock -> stock.getStock() - stock.getStockLocked()));

        for (int i = 0; i < esProducts.size(); i++) {
            esProducts.get(i).setBrandName(brandNames.get(i));
            esProducts.get(i).setBrandImg(brandImgs.get(i));
            esProducts.get(i).setCatalogName(catalogNames.get(i));
            esProducts.get(i).setHasStock(collect.getOrDefault(esProducts.get(i).getSkuId(), 0) > 0);
        }

        // 保存到 Elasticsearch
        R<?> r = searchFeignClient.saveProductAll(esProducts);
        checkResult(r, "保存商品到es出错");
        return true;
    }

    private boolean takenOffSale(Long spuId) {
        List<SkuInfo> skus = getSkusBySpuId(spuId);
        if (skus.isEmpty()) {
            throw new CustomException("无法下架没有任何sku的商品", new IllegalArgumentException(), HttpStatus.BAD_REQUEST);
        }

        // TODO(接口幂等) 检查商品是否上架

        return false;

    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.id"),
            @CacheEvict(cacheNames = "spuInfo:list", allEntries = true)
    })
    public boolean updateById(SpuInfoDTO dto) {
        SpuInfo spuInfo = new SpuInfo();
        BeanUtils.copyProperties(dto, spuInfo);

        boolean res = super.updateById(spuInfo);

        if (res && spuInfo.getPublishStatus() != null) {
            if (spuInfo.getPublishStatus().equals(SpuStatus.UP.getCode())) {
                res = putOnSale(spuInfo.getId());
            } else if (spuInfo.getPublishStatus().equals(SpuStatus.DOWN.getCode())) {
                // TODO 下架商品
                res = takenOffSale(spuInfo.getId());
            }
        }

        return res;
    }

    @Override
    @Cacheable(key = "#id")
    public SpuInfo getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(cacheNames = "spuInfo:list", allEntries = true)
    })
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean save(SpuInfo entity){
        return super.save(entity);
    }

    @Override
    @CacheEvict(cacheNames = "spuInfo:list", allEntries = true)
    public boolean save(SpuSaveDTO dto) {

        // 1. 保存spu基本信息
        SpuInfo spuInfo = new SpuInfo();
        BeanUtils.copyProperties(dto, spuInfo);
        spuInfo.setUpdateTime(new Date());
        spuInfo.setCreateTime(new Date());
        boolean res = this.save(spuInfo);

        if (!res) {
            throw new RuntimeException("保存spu失败");
        }

        // 2. 保存spu的描述图片
        List<String> decript = dto.getDecript() != null ? dto.getDecript() : List.of();
        SpuInfoDesc spuInfoDesc = new SpuInfoDesc();
        spuInfoDesc.setSpuId(spuInfo.getId());
        spuInfoDesc.setDecript(String.join(",", decript));

        res = spuInfoDescMapper.insert(spuInfoDesc) > 0;
        checkResult(res, "保存spu描述图片失败");

        // 3. 保存spu图片集
        List<String> images = dto.getImages() != null ? dto.getImages() : List.of();
        List<SpuImages> spuImages = images.stream().map(image -> {
            SpuImages spuImage = new SpuImages();
            spuImage.setSpuId(spuInfo.getId());
            spuImage.setImgUrl(image);
            return spuImage;
        }).toList();
        res = spuImagesService.saveBatch(spuImages);
        checkResult(res, "保存spu图片集失败");

        // 4. 保存spu的规格参数
        List<SpuSaveDTO.BaseAttr> baseAttrs = dto.getBaseAttrs() != null ? dto.getBaseAttrs() : List.of();
        List<ProductAttrValue> attrValues = baseAttrs.stream().map(baseAttr -> {
            ProductAttrValue attrValue = new ProductAttrValue();
            attrValue.setSpuId(spuInfo.getId());
            attrValue.setAttrId(baseAttr.getAttrId());
            if (baseAttr.getAttrId() != null){
                Attr attr = attrService.getById(baseAttr.getAttrId());
                if (attr != null) {
                    attrValue.setAttrName(attr.getAttrName());
                    attrValue.setAttrValue(baseAttr.getAttrValues());
                }
            }
            attrValue.setQuickShow(baseAttr.getShowDesc());
            return attrValue;
        }).toList();
        res = productAttrValueService.saveBatch(attrValues);
        checkResult(res, "保存spu规格参数失败");

        // 5. 保存spu的积分信息
        SpuSaveDTO.Bounds bounds = dto.getBounds();
        BoundsDTO dtoBounds = new BoundsDTO();
        BeanUtils.copyProperties(bounds, dtoBounds);
        dtoBounds.setSpuId(spuInfo.getId());
        R<?> r = couponFeignClient.createSpuBounds(dtoBounds);
        checkResult(r, "保存spu积分信息失败");

        // 6. 批量保存sku信息
        List<SpuSaveDTO.Skus> skus = dto.getSkus() != null ? dto.getSkus() : List.of();
        skus.forEach(each -> {
            String defaultimage = "";
            List<SpuSaveDTO.Skus.Image> imgs = each.getImages() != null ? each.getImages() : List.of();
            for (SpuSaveDTO.Skus.Image skuImage : imgs) {
                if (skuImage.getDefaultImg() == 1){
                    defaultimage = skuImage.getImgUrl();
                    break;
                }
            }
            SkuInfo skuInfo = new SkuInfo();
            BeanUtils.copyProperties(each, skuInfo);
            skuInfo.setSpuId(spuInfo.getId());
            skuInfo.setCatalogId(spuInfo.getCatalogId());
            skuInfo.setBrandId(spuInfo.getBrandId());
            skuInfo.setSaleCount(0L);
            skuInfo.setSkuDefaultImg(defaultimage);
            boolean skuRes = skuInfoMapper.insert(skuInfo) > 0;
            checkResult(skuRes, "保存sku信息失败");

            Long skuId = skuInfo.getSkuId();
            List<SkuImages> skuImages = each.getImages().stream().map(img -> {
                SkuImages skuImage = new SkuImages();
                skuImage.setSkuId(skuId);
                skuImage.setImgUrl(img.getImgUrl());
                skuImage.setDefaultImg(img.getDefaultImg());
                return skuImage;
            }).filter(img -> StringUtils.isNotBlank(img.getImgUrl())).toList();
            boolean skuImagesRes = skuImagesService.saveBatch(skuImages);
            checkResult(skuImagesRes, "保存sku图片信息失败");

            List<SkuSaleAttrValue> skuSaleAttrValues = each.getAttr().stream().map(saleAttr -> {
                SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
                BeanUtils.copyProperties(saleAttr, skuSaleAttrValue);
                skuSaleAttrValue.setSkuId(skuId);
                return skuSaleAttrValue;
            }).toList();
            boolean skuSaleAttrValuesRes = skuSaleAttrValueService.saveBatch(skuSaleAttrValues);
            checkResult(skuSaleAttrValuesRes, "保存sku销售属性信息失败");

            // 6.4 保存sku的优惠、满减等信息
            SkuReductionDTO skuReduction = new SkuReductionDTO();
            Integer fullCount = each.getFullCount();
            BigDecimal fullPrice = each.getFullPrice();
            if (fullCount == null || fullCount <= 0 || fullPrice == null || fullPrice.compareTo(BigDecimal.ZERO) <= 0) return;
            BeanUtils.copyProperties(each, skuReduction);
            skuReduction.setSkuId(skuId);
            R<?> skuReductionRes = couponFeignClient.saveInfo(skuReduction);
            checkResult(skuReductionRes, "保存sku优惠、满减等信息失败");
        });

        return true;
    }

    private Long parseKey2Long(String key) {
        try {
            return Long.parseLong(key);
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public List<SpuInfo> list(Integer pageNum, Integer pageSize, Long catalogId, Long brandId, String key, Integer status) {
        var self = ((SpuInfoServiceImpl)AopContext.currentProxy());
        if (catalogId == null && (key == null || key.isEmpty()) && (status == null || status <= 0) && brandId == null)
            return self.list(pageNum, pageSize);

        IPage<SpuInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SpuInfo> qw = Wrappers.lambdaQuery();
        qw.eq(catalogId != null, SpuInfo::getCatalogId, catalogId)
                .eq(brandId != null, SpuInfo::getBrandId, brandId)
                .eq(status != null && status >= 0, SpuInfo::getPublishStatus, status)
                .and(key != null && !key.isEmpty(), w -> {
                    w.like(SpuInfo::getSpuName, key)
                            .or()
                            .eq(SpuInfo::getId, parseKey2Long(key));
                });

        return super.list(page, qw);
    }

    @Cacheable(cacheNames = "spuInfo:list", key = "#pageNum + ':' + #pageSize")
    public List<SpuInfo> list(Integer pageNum, Integer pageSize) {
        IPage<SpuInfo> page = new Page<>(pageNum, pageSize);
        return super.list(page);
    }
}
