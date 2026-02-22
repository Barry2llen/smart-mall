package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.utils.KeyUtils;
import edu.nchu.mall.models.dto.SkuInfoDTO;
import edu.nchu.mall.models.entity.ProductAttrValue;
import edu.nchu.mall.models.entity.SkuImages;
import edu.nchu.mall.models.entity.SkuInfo;
import edu.nchu.mall.models.entity.SpuInfoDesc;
import edu.nchu.mall.models.vo.SkuInfoVO;
import edu.nchu.mall.models.vo.SkuItemSaleAttrVO;
import edu.nchu.mall.models.vo.SkuItemVO;
import edu.nchu.mall.services.product.dao.SkuInfoMapper;
import edu.nchu.mall.services.product.dao.SkuSaleAttrValueMapper;
import edu.nchu.mall.services.product.service.*;
import jakarta.annotation.Nullable;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@CacheConfig(cacheNames = "skuInfo")
@Transactional
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    CacheManager cacheManager;

    private SkuInfoVO convert2VO(@Nullable SkuInfo info) {
        SkuInfoVO vo = new SkuInfoVO();
        if (info == null) return vo;
        BeanUtils.copyProperties(info, vo);
        return vo;
    }

    private boolean checkSkuInfoCache(Serializable id) {
        Cache cache = cacheManager.getCache("skuInfo");
        return cache != null && cache.get(id) != null;
    }

    @Override
    public Map<Long, SkuInfoVO> getBatchByIds(Iterable<Long> ids) {
        var self = (SkuInfoServiceImpl) AopContext.currentProxy();
        Map<Long, SkuInfoVO> res = new HashMap<>();
        List<Long> nonCached = new LinkedList<>();
        ids.forEach(id -> {
            if (checkSkuInfoCache(id)) {
                res.put(id, self.getVOById(id));
            }else {
                nonCached.add(id);
            }
        });

        if (nonCached.isEmpty()) {
            return res;
        }

        List<SkuInfoVO> selected = baseMapper.selectBatchIds(nonCached).stream().map(this::convert2VO).toList();
        Cache cache = cacheManager.getCache("skuInfo");
        selected.forEach(vo -> {
            res.put(vo.getSkuId(), vo);
            if (cache != null) {
                cache.put(vo.getSkuId(), vo);
            }
        });

        return res;
    }

    @Override
    public boolean existsById(Long skuId) {
        return baseMapper.exists(Wrappers.<SkuInfo>lambdaQuery().eq(SkuInfo::getSkuId, skuId));
    }

    @Override
    @Cacheable(key = "#id")
    public SkuInfoVO getVOById(Serializable id) {
        return this.convert2VO(super.getById(id));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.skuId"),
            @CacheEvict(cacheNames = "skuInfo:list", allEntries = true),
            @CacheEvict(cacheNames = "skuItem", key = "#dto.skuId")
    })
    public boolean updateById(SkuInfoDTO dto) {
        SkuInfo info = new SkuInfo();
        BeanUtils.copyProperties(dto, info);
        return super.updateById(info);
    }

    @Override
    public List<SkuInfoVO> list(Integer pageNum, Integer pageSize, Long catalogId, Long brandId, String key,
                                BigDecimal minPrice, BigDecimal maxPrice) {
        var self = ((SkuInfoServiceImpl) AopContext.currentProxy());
        if (catalogId == null && brandId == null
                && (key == null || key.isEmpty())
                && minPrice == null && maxPrice == null) {
            return self.list(pageNum, pageSize);
        }

        IPage<SkuInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SkuInfo> qw = Wrappers.lambdaQuery();
        qw.eq(catalogId != null, SkuInfo::getCatalogId, catalogId)
                .eq(brandId != null, SkuInfo::getBrandId, brandId)
                .and(key != null && !key.isEmpty(), w -> {
                    w.like(SkuInfo::getSkuName, key)
                            .or()
                            .eq(SkuInfo::getSkuId, KeyUtils.parseKey2Long(key).orElse(0L));
                })
                .and(minPrice != null, w -> {
                    w.ge(SkuInfo::getPrice, minPrice);
                })
                .and(maxPrice != null, w -> {
                    w.le(SkuInfo::getPrice, maxPrice);
                });

        return super.list(page, qw).stream().map(this::convert2VO).toList();
    }

    @Cacheable(cacheNames = "skuInfo:list", key = "#pageNum + ':' + #pageSize")
    public List<SkuInfoVO> list(Integer pageNum, Integer pageSize) {
        IPage<SkuInfo> page = new Page<>(pageNum, pageSize);
        return super.list(page).stream().map(this::convert2VO).toList();
    }

    @Override
    @Cacheable(cacheNames = "skuItem", key = "#id", sync = true)
    public SkuItemVO getSkuItem(long id) {
        var self = ((SkuInfoServiceImpl) AopContext.currentProxy());
        SkuItemVO item = new SkuItemVO();

        CompletableFuture<SkuInfoVO> skuInfoVO = CompletableFuture.supplyAsync(() -> self.getVOById(id));

        CompletableFuture<List<SkuImages>> images = CompletableFuture.supplyAsync(() -> skuImagesService.getImagesBySkuId(id));

        CompletableFuture<SpuInfoDesc> spuInfoDesc = skuInfoVO.thenApply(v -> spuInfoDescService.getById(v.getSpuId()));

        CompletableFuture<List<SkuItemSaleAttrVO>> saleAttrs = skuInfoVO.thenApplyAsync(v -> skuSaleAttrValueMapper.getSaleAttrsBySpuId(v.getSpuId()));

        CompletableFuture<List<SkuItemVO.SpuItemAttrGroupVO>> groupAttrs = skuInfoVO.thenApplyAsync(info -> attrGroupService.listAttrInGroupByCatalogId(info.getCatalogId())
                .stream().map(v -> {
                    SkuItemVO.SpuItemAttrGroupVO vo = new SkuItemVO.SpuItemAttrGroupVO();
                    List<SkuItemVO.SpuBaseAttrVO> baseAttrs = v.getAttrs()
                            .stream().map(attr -> {
                                SkuItemVO.SpuBaseAttrVO baseAttrVO = new SkuItemVO.SpuBaseAttrVO();
                                baseAttrVO.setAttrName(attr.getAttrName());
                                baseAttrVO.setAttrId(attr.getAttrId());
                                return baseAttrVO;
                            }).toList();
                    vo.setGroupName(v.getAttrGroupName());
                    vo.setAttrs(baseAttrs);
                    return vo;
                }).toList());


        CompletableFuture<Map<Long, String>> attrValues = skuInfoVO.thenApplyAsync(info -> {
            LambdaQueryWrapper<ProductAttrValue> qw = Wrappers.lambdaQuery();
            qw.eq(ProductAttrValue::getSpuId, info.getSpuId());
            return productAttrValueService.list(qw)
                    .stream().collect(Collectors.toMap(ProductAttrValue::getAttrId, ProductAttrValue::getAttrValue));
        });

        CompletableFuture<Void> voidCompletableFuture = groupAttrs.thenAcceptBoth(attrValues, (_groupAttrs, _attrValues) -> {
            _groupAttrs.forEach(v -> {
                v.getAttrs().forEach(attr -> {
                    attr.setAttrValue(_attrValues.get(attr.getAttrId()));
                });
            });
        });

        CompletableFuture.allOf(skuInfoVO, images, spuInfoDesc, saleAttrs, groupAttrs, voidCompletableFuture).join();

        try{
            item.setSkuInfo(skuInfoVO.get());
            item.setImages(images.get());
            item.setDesp(spuInfoDesc.get());
            item.setGroupAttrs(groupAttrs.get());
            item.setSaleAttr(saleAttrs.get());
        }catch (ExecutionException | InterruptedException e) {
            throw new CustomException("查询失败", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return item;
    }

}
