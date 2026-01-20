package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SkuImages;

import java.io.Serializable;

public interface SkuImagesService extends IService<SkuImages> {
    @Override
    boolean updateById(SkuImages entity);

    @Override
    SkuImages getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
