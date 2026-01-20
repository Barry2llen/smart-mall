package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.HomeSubject;
import edu.nchu.mall.services.coupon.dao.HomeSubjectMapper;
import edu.nchu.mall.services.coupon.service.HomeSubjectService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "homeSubject")
@Transactional
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
}
