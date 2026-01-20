package edu.nchu.mall.services.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.RefundInfo;

import java.io.Serializable;

public interface RefundInfoService extends IService<RefundInfo> {
    @Override
    boolean updateById(RefundInfo entity);

    @Override
    RefundInfo getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
