package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SpuInfo;

import java.io.Serializable;

public interface SpuInfoService extends IService<SpuInfo> {
    @Override
    boolean updateById(SpuInfo entity);

    @Override
    SpuInfo getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
