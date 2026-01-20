package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SpuBounds;

import java.io.Serializable;

public interface SpuBoundsService extends IService<SpuBounds> {
    @Override
    boolean updateById(SpuBounds entity);

    @Override
    SpuBounds getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
