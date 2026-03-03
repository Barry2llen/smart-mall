package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SeckillSkuNotice;
import edu.nchu.mall.services.coupon.dao.SeckillSkuNoticeMapper;
import edu.nchu.mall.services.coupon.service.SeckillSkuNoticeService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "seckillSkuNotice")
public class SeckillSkuNoticeServiceImpl extends ServiceImpl<SeckillSkuNoticeMapper, SeckillSkuNotice> implements SeckillSkuNoticeService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SeckillSkuNotice entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SeckillSkuNotice getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<SeckillSkuNotice> list(Integer pageNum, Integer pageSize) {
        IPage<SeckillSkuNotice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SeckillSkuNotice> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderByDesc(SeckillSkuNotice::getId);
        return super.page(page, queryWrapper).getRecords();
    }
}
