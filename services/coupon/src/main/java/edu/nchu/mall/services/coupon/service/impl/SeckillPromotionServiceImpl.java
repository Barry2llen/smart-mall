package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SeckillPromotion;
import edu.nchu.mall.services.coupon.dao.SeckillPromotionMapper;
import edu.nchu.mall.services.coupon.service.SeckillPromotionService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;

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

    @Override
    public List<SeckillPromotion> list(Integer pageNum, Integer pageSize, String title, Integer status) {
        IPage<SeckillPromotion> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SeckillPromotion> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StringUtils.hasText(title), SeckillPromotion::getTitle, title)
                .eq(status != null, SeckillPromotion::getStatus, status)
                .orderByDesc(SeckillPromotion::getId);
        return super.page(page, queryWrapper).getRecords();
    }
}
