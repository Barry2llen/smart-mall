package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.Coupon;
import edu.nchu.mall.services.coupon.dao.CouponMapper;
import edu.nchu.mall.services.coupon.service.CouponService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "coupon")
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Coupon entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public Coupon getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<Coupon> list(Integer pageNum, Integer pageSize, String couponName, Integer couponType, Integer publish) {
        IPage<Coupon> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Coupon> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StringUtils.hasText(couponName), Coupon::getCouponName, couponName)
                .eq(couponType != null, Coupon::getCouponType, couponType)
                .eq(publish != null, Coupon::getPublish, publish)
                .orderByDesc(Coupon::getId);
        return super.page(page, queryWrapper).getRecords();
    }
}
