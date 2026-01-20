package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.CouponHistory;
import edu.nchu.mall.services.coupon.dao.CouponHistoryMapper;
import edu.nchu.mall.services.coupon.service.CouponHistoryService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "couponHistory")
@Transactional
public class CouponHistoryServiceImpl extends ServiceImpl<CouponHistoryMapper, CouponHistory> implements CouponHistoryService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(CouponHistory entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public CouponHistory getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
