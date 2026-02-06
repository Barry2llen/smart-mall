package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.Brand;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface BrandService extends IService<Brand> {
    @Override
    boolean updateById(Brand entity);

    @Override
    Brand getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    @Override
    List<Brand> list(IPage<Brand> page);

    @Override
    boolean save(Brand entity);

    List<Brand> seqByIds(Collection<Long> ids);
}
