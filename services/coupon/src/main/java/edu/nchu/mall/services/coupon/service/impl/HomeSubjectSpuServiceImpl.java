package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.HomeSubjectSpu;
import edu.nchu.mall.services.coupon.dao.HomeSubjectSpuMapper;
import edu.nchu.mall.services.coupon.service.HomeSubjectSpuService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "homeSubjectSpu")
public class HomeSubjectSpuServiceImpl extends ServiceImpl<HomeSubjectSpuMapper, HomeSubjectSpu> implements HomeSubjectSpuService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(HomeSubjectSpu entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public HomeSubjectSpu getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<HomeSubjectSpu> list(Integer pageNum, Integer pageSize) {
        IPage<HomeSubjectSpu> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HomeSubjectSpu> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderByDesc(HomeSubjectSpu::getId);
        return super.page(page, queryWrapper).getRecords();
    }
}
