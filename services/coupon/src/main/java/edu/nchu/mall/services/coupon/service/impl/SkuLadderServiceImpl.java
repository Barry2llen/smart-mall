package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SkuLadder;
import edu.nchu.mall.services.coupon.dao.SkuLadderMapper;
import edu.nchu.mall.services.coupon.service.SkuLadderService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "skuLadder")
public class SkuLadderServiceImpl extends ServiceImpl<SkuLadderMapper, SkuLadder> implements SkuLadderService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SkuLadder entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SkuLadder getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<SkuLadder> list(Integer pageNum, Integer pageSize) {
        IPage<SkuLadder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SkuLadder> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderByDesc(SkuLadder::getId);
        return super.page(page, queryWrapper).getRecords();
    }
}
