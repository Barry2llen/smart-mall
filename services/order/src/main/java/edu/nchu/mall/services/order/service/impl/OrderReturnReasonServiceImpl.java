package edu.nchu.mall.services.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.OrderReturnReason;
import edu.nchu.mall.services.order.dao.OrderReturnReasonMapper;
import edu.nchu.mall.services.order.service.OrderReturnReasonService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "orderReturnReason")
public class OrderReturnReasonServiceImpl extends ServiceImpl<OrderReturnReasonMapper, OrderReturnReason> implements OrderReturnReasonService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(OrderReturnReason entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public OrderReturnReason getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
