package edu.nchu.mall.services.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.WareOrderTaskDetail;

import java.io.Serializable;

public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetail> {
    @Override
    boolean updateById(WareOrderTaskDetail entity);

    @Override
    WareOrderTaskDetail getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
