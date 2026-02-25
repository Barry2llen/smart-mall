package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.CouponSpuRelation;
import edu.nchu.mall.services.coupon.dao.CouponSpuRelationMapper;
import edu.nchu.mall.services.coupon.service.CouponSpuRelationService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "couponSpuRelation")
public class CouponSpuRelationServiceImpl extends ServiceImpl<CouponSpuRelationMapper, CouponSpuRelation> implements CouponSpuRelationService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(CouponSpuRelation entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public CouponSpuRelation getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
