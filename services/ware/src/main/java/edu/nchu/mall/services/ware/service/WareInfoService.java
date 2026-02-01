package edu.nchu.mall.services.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.WareInfo;

import java.io.Serializable;
import java.util.List;

public interface WareInfoService extends IService<WareInfo> {
    @Override
    boolean updateById(WareInfo entity);

    @Override
    WareInfo getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    List<WareInfo> list(Integer pageNum, Integer pageSize, String key);
}
