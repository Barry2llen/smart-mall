package edu.nchu.mall.services.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.OrderReturnReason;

import java.io.Serializable;

public interface OrderReturnReasonService extends IService<OrderReturnReason> {
    @Override
    boolean updateById(OrderReturnReason entity);

    @Override
    OrderReturnReason getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
