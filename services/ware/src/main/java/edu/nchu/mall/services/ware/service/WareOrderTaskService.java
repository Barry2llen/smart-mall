package edu.nchu.mall.services.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.WareOrderTask;

import java.io.Serializable;

public interface WareOrderTaskService extends IService<WareOrderTask> {
    @Override
    boolean updateById(WareOrderTask entity);

    @Override
    WareOrderTask getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
