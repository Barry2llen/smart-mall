package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SpuBounds;
import edu.nchu.mall.services.coupon.dao.SpuBoundsMapper;
import edu.nchu.mall.services.coupon.service.SpuBoundsService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "spuBounds")
public class SpuBoundsServiceImpl extends ServiceImpl<SpuBoundsMapper, SpuBounds> implements SpuBoundsService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SpuBounds entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SpuBounds getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<SpuBounds> list(Integer pageNum, Integer pageSize) {
        IPage<SpuBounds> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SpuBounds> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderByDesc(SpuBounds::getId);
        return super.page(page, queryWrapper).getRecords();
    }
}
