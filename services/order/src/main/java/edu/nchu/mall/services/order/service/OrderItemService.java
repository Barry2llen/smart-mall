package edu.nchu.mall.services.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.OrderItem;

import java.io.Serializable;

public interface OrderItemService extends IService<OrderItem> {
    @Override
    boolean updateById(OrderItem entity);

    @Override
    OrderItem getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
