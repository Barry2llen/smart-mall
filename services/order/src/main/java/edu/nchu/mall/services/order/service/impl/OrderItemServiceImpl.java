package edu.nchu.mall.services.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.OrderItem;
import edu.nchu.mall.services.order.dao.OrderItemMapper;
import edu.nchu.mall.services.order.service.OrderItemService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "orderItem")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(OrderItem entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public OrderItem getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
