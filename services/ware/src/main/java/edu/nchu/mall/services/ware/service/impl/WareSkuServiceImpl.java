package edu.nchu.mall.services.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.WareSku;
import edu.nchu.mall.services.ware.dao.WareSkuMapper;
import edu.nchu.mall.services.ware.service.WareSkuService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "wareSku")
@Transactional
public class WareSkuServiceImpl extends ServiceImpl<WareSkuMapper, WareSku> implements WareSkuService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(WareSku entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public WareSku getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
