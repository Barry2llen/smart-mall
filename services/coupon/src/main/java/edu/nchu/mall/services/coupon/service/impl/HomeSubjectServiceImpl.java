package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.HomeSubject;
import edu.nchu.mall.services.coupon.dao.HomeSubjectMapper;
import edu.nchu.mall.services.coupon.service.HomeSubjectService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "homeSubject")
public class HomeSubjectServiceImpl extends ServiceImpl<HomeSubjectMapper, HomeSubject> implements HomeSubjectService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(HomeSubject entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public HomeSubject getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<HomeSubject> list(Integer pageNum, Integer pageSize, String name) {
        IPage<HomeSubject> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HomeSubject> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StringUtils.hasText(name), HomeSubject::getName, name)
                .orderByDesc(HomeSubject::getId);
        return super.page(page, queryWrapper).getRecords();
    }
}
