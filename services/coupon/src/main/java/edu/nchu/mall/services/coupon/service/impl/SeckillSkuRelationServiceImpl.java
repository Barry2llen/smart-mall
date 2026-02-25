package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SeckillSkuRelation;
import edu.nchu.mall.services.coupon.dao.SeckillSkuRelationMapper;
import edu.nchu.mall.services.coupon.service.SeckillSkuRelationService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "seckillSkuRelation")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationMapper, SeckillSkuRelation> implements SeckillSkuRelationService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SeckillSkuRelation entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SeckillSkuRelation getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
