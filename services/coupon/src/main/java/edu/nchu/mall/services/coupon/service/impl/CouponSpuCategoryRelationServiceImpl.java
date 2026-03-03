package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.CouponSpuCategoryRelation;
import edu.nchu.mall.services.coupon.dao.CouponSpuCategoryRelationMapper;
import edu.nchu.mall.services.coupon.service.CouponSpuCategoryRelationService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "couponSpuCategoryRelation")
public class CouponSpuCategoryRelationServiceImpl extends ServiceImpl<CouponSpuCategoryRelationMapper, CouponSpuCategoryRelation> implements CouponSpuCategoryRelationService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(CouponSpuCategoryRelation entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public CouponSpuCategoryRelation getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<CouponSpuCategoryRelation> list(Integer pageNum, Integer pageSize) {
        IPage<CouponSpuCategoryRelation> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CouponSpuCategoryRelation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderByDesc(CouponSpuCategoryRelation::getId);
        return super.page(page, queryWrapper).getRecords();
    }
}
