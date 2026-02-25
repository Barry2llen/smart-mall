package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SpuInfoDesc;
import edu.nchu.mall.services.product.dao.SpuInfoDescMapper;
import edu.nchu.mall.services.product.service.SpuInfoDescService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "spuInfoDesc")
public class SpuInfoDescServiceImpl extends ServiceImpl<SpuInfoDescMapper, SpuInfoDesc> implements SpuInfoDescService {

    @Override
    @CacheEvict(key = "#entity.spuId")
    public boolean updateById(SpuInfoDesc entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SpuInfoDesc getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
