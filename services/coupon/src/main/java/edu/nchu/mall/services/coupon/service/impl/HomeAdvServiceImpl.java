package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.HomeAdv;
import edu.nchu.mall.services.coupon.dao.HomeAdvMapper;
import edu.nchu.mall.services.coupon.service.HomeAdvService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

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
}
