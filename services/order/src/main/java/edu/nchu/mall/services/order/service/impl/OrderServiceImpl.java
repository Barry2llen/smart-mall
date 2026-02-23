package edu.nchu.mall.services.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.components.config.ThreadPoolConfig;
import edu.nchu.mall.components.feign.cart.CartFeignClient;
import edu.nchu.mall.components.feign.member.MemberFeignClient;
import edu.nchu.mall.components.utils.CallTaskUtils;
import edu.nchu.mall.models.entity.MemberReceiveAddress;
import edu.nchu.mall.models.entity.Order;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.Try;
import edu.nchu.mall.services.order.dao.OrderMapper;
import edu.nchu.mall.services.order.service.OrderService;
import edu.nchu.mall.services.order.vo.OrderConfirm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@CacheConfig(cacheNames = "order")
@Transactional
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    CallTaskUtils callTaskUtils;

    @Autowired
    MemberFeignClient memberFeignClient;

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    @Qualifier(ThreadPoolConfig.VTHREAD_POOL_NAME)
    Executor executor;

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Order entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public Order getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public OrderConfirm confirmOrder(Long memberId) {

        // 1. 获取收货地址列表
        var addressTask = callTaskUtils.call(() -> memberFeignClient.getMemberReceiveAddress(memberId));

        // 2. 获取订单项列表
        var itemTask = callTaskUtils.call(() -> cartFeignClient.getRefreshedCartItems(memberId));


        return null;
    }


}
