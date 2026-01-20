package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.Brand;

import java.io.Serializable;

public interface BrandService extends IService<Brand> {
    @Override
    boolean updateById(Brand entity);

    @Override
    Brand getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
