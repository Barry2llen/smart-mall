package edu.nchu.mall.services.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.OrderReturnApply;
import edu.nchu.mall.services.order.dao.OrderReturnApplyMapper;
import edu.nchu.mall.services.order.service.OrderReturnApplyService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "orderReturnApply")
public class OrderReturnApplyServiceImpl extends ServiceImpl<OrderReturnApplyMapper, OrderReturnApply> implements OrderReturnApplyService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(OrderReturnApply entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public OrderReturnApply getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
