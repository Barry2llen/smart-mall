package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SeckillPromotion;
import edu.nchu.mall.services.coupon.dao.SeckillPromotionMapper;
import edu.nchu.mall.services.coupon.service.SeckillPromotionService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "seckillPromotion")
public class SeckillPromotionServiceImpl extends ServiceImpl<SeckillPromotionMapper, SeckillPromotion> implements SeckillPromotionService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SeckillPromotion entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SeckillPromotion getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
