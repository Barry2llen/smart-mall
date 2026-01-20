package edu.nchu.mall.services.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.OrderReturnApply;

import java.io.Serializable;

public interface OrderReturnApplyService extends IService<OrderReturnApply> {
    @Override
    boolean updateById(OrderReturnApply entity);

    @Override
    OrderReturnApply getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
