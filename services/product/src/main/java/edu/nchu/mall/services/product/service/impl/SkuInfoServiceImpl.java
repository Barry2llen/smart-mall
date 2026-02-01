package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.dto.SkuInfoDTO;
import edu.nchu.mall.models.entity.SkuInfo;
import edu.nchu.mall.models.vo.SkuInfoVO;
import edu.nchu.mall.services.product.dao.SkuInfoMapper;
import edu.nchu.mall.services.product.service.SkuInfoService;
import jakarta.annotation.Nullable;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Service
@CacheConfig(cacheNames = "skuInfo")
@Transactional
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {
    private Long parseKey2Long(String key) {
        try {
            return Long.parseLong(key);
        } catch (Exception e) {
            return 0L;
        }
    }

    private SkuInfoVO convert2VO(@Nullable SkuInfo info) {
        SkuInfoVO vo = new SkuInfoVO();
        if (info == null) return vo;
        BeanUtils.copyProperties(info, vo);
        return vo;
    }

    @Override
    @Cacheable(key = "#id")
    public SkuInfoVO getVOById(Serializable id) {
        return this.convert2VO(super.getById(id));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.skuId"),
            @CacheEvict(cacheNames = "skuInfo:list", allEntries = true)
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
                            .eq(SkuInfo::getSkuId, parseKey2Long(key));
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
}
