package edu.nchu.mall.services.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.OrderSetting;

import java.io.Serializable;

public interface OrderSettingService extends IService<OrderSetting> {
    @Override
    boolean updateById(OrderSetting entity);

    @Override
    OrderSetting getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
