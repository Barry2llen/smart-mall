package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SpuImages;

import java.io.Serializable;

public interface SpuImagesService extends IService<SpuImages> {
    @Override
    boolean updateById(SpuImages entity);

    @Override
    SpuImages getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
