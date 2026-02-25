package edu.nchu.mall.services.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.OrderSetting;
import edu.nchu.mall.services.order.dao.OrderSettingMapper;
import edu.nchu.mall.services.order.service.OrderSettingService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "orderSetting")
public class OrderSettingServiceImpl extends ServiceImpl<OrderSettingMapper, OrderSetting> implements OrderSettingService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(OrderSetting entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public OrderSetting getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
