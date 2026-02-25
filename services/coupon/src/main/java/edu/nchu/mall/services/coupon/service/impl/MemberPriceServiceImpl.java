package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.MemberPrice;
import edu.nchu.mall.services.coupon.dao.MemberPriceMapper;
import edu.nchu.mall.services.coupon.service.MemberPriceService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "memberPrice")
public class MemberPriceServiceImpl extends ServiceImpl<MemberPriceMapper, MemberPrice> implements MemberPriceService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(MemberPrice entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public MemberPrice getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
