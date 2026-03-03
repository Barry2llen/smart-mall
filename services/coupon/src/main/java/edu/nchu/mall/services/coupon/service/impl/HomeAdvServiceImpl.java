package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.HomeAdv;
import edu.nchu.mall.services.coupon.dao.HomeAdvMapper;
import edu.nchu.mall.services.coupon.service.HomeAdvService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "homeAdv")
public class HomeAdvServiceImpl extends ServiceImpl<HomeAdvMapper, HomeAdv> implements HomeAdvService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(HomeAdv entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public HomeAdv getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<HomeAdv> list(Integer pageNum, Integer pageSize, String name, Integer status) {
        IPage<HomeAdv> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HomeAdv> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StringUtils.hasText(name), HomeAdv::getName, name)
                .eq(status != null, HomeAdv::getStatus, status)
                .orderByDesc(HomeAdv::getId);
        return super.page(page, queryWrapper).getRecords();
    }
}
