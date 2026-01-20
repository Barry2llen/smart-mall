package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SpuImages;
import edu.nchu.mall.services.product.dao.SpuImagesMapper;
import edu.nchu.mall.services.product.service.SpuImagesService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "spuImages")
@Transactional
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesMapper, SpuImages> implements SpuImagesService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SpuImages entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SpuImages getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
