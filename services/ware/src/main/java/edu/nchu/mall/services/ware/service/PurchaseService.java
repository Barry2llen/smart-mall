package edu.nchu.mall.services.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.Purchase;

import java.io.Serializable;

public interface PurchaseService extends IService<Purchase> {
    @Override
    boolean updateById(Purchase entity);

    @Override
    Purchase getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
