package edu.nchu.mall.services.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.WareSku;

import java.io.Serializable;

public interface WareSkuService extends IService<WareSku> {
    @Override
    boolean updateById(WareSku entity);

    @Override
    WareSku getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
