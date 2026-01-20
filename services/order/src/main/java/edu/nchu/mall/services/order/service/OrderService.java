package edu.nchu.mall.services.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.Order;

import java.io.Serializable;

public interface OrderService extends IService<Order> {
    @Override
    boolean updateById(Order entity);

    @Override
    Order getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
