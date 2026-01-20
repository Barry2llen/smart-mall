package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SkuInfo;

import java.io.Serializable;

public interface SkuInfoService extends IService<SkuInfo> {
    @Override
    boolean updateById(SkuInfo entity);

    @Override
    SkuInfo getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
