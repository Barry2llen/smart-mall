package edu.nchu.mall.services.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.OrderOperateHistory;
import edu.nchu.mall.services.order.dao.OrderOperateHistoryMapper;
import edu.nchu.mall.services.order.service.OrderOperateHistoryService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "orderOperateHistory")
@Transactional
public class OrderOperateHistoryServiceImpl extends ServiceImpl<OrderOperateHistoryMapper, OrderOperateHistory> implements OrderOperateHistoryService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(OrderOperateHistory entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public OrderOperateHistory getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
