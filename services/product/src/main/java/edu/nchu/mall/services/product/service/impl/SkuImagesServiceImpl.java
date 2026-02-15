package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SkuImages;
import edu.nchu.mall.services.product.dao.SkuImagesMapper;
import edu.nchu.mall.services.product.service.SkuImagesService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Service
@CacheConfig(cacheNames = "skuImages")
@Transactional
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesMapper, SkuImages> implements SkuImagesService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SkuImages entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SkuImages getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean saveBatch(Collection<SkuImages> entities) {
        return entities.isEmpty() || super.saveBatch(entities);
    }

    @Override
    public List<SkuImages> getImagesBySkuId(Long skuId) {
        return baseMapper.selectList(Wrappers.<SkuImages>lambdaQuery().eq(SkuImages::getSkuId, skuId));
    }
}
