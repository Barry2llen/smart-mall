package edu.nchu.mall.services.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.OrderOperateHistory;

import java.io.Serializable;

public interface OrderOperateHistoryService extends IService<OrderOperateHistory> {
    @Override
    boolean updateById(OrderOperateHistory entity);

    @Override
    OrderOperateHistory getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
