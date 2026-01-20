package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SpuInfoDesc;

import java.io.Serializable;

public interface SpuInfoDescService extends IService<SpuInfoDesc> {
    @Override
    boolean updateById(SpuInfoDesc entity);

    @Override
    SpuInfoDesc getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
