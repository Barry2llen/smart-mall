package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SkuSaleAttrValue;
import edu.nchu.mall.services.product.dao.SkuSaleAttrValueMapper;
import edu.nchu.mall.services.product.service.SkuSaleAttrValueService;
import edu.nchu.mall.services.product.vo.SaleAttrValueVO;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "skuSaleAttrValue")
@Transactional
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueMapper, SkuSaleAttrValue> implements SkuSaleAttrValueService {

    @Autowired
    CacheManager cacheManager;

    private boolean checkSkuAttrValueCache(Long id) {
        Cache cache = cacheManager.getCache("skuSaleAttrValue::values");
        return cache != null && cache.get(id) != null;
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SkuSaleAttrValue entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SkuSaleAttrValue getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Cacheable(key = "#skuId", cacheNames = "skuSaleAttrValue::values")
    public List<String> getSkuAttrValues(Long skuId) {
        return baseMapper.getSaleAttrValues(skuId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, List<String>> getBatchSkuAttrValues(Collection<Long> skuIds) {
        var self = (SkuSaleAttrValueServiceImpl) AopContext.currentProxy();
        Cache cache = cacheManager.getCache("skuSaleAttrValue::values");

        List<Long> nonCachedSkuIds = new LinkedList<>();
        Map<Long, List<String>> res = new HashMap<>();

        skuIds.forEach(id -> {
            if (checkSkuAttrValueCache(id)) {
                if (cache != null) {
                    Object cached = cache.get(id).get();
                    res.put(id, cached == null ? List.of() : (List<String>) cached);
                }
            }else {
                nonCachedSkuIds.add(id);
            }
        });

        if (nonCachedSkuIds.isEmpty()) {
            return res;
        }

        List<SaleAttrValueVO> batchSaleAttrValues = baseMapper.getBatchSaleAttrValues(nonCachedSkuIds);
        batchSaleAttrValues.forEach(each -> {
            Long id = each.getSkuId();
            List<String> name_values = Arrays.stream(each.getNameValues().split(";")).filter(s -> !s.isEmpty()).toList();
            res.put(id, new ArrayList<>(name_values));
            if (cache != null) {
                cache.put(id, name_values);
            }
        });

        return res;
    }
}
