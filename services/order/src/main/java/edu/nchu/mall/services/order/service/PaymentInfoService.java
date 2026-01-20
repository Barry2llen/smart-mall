package edu.nchu.mall.services.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.PaymentInfo;

import java.io.Serializable;

public interface PaymentInfoService extends IService<PaymentInfo> {
    @Override
    boolean updateById(PaymentInfo entity);

    @Override
    PaymentInfo getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
