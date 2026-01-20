package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.HomeAdv;

import java.io.Serializable;

public interface HomeAdvService extends IService<HomeAdv> {
    @Override
    boolean updateById(HomeAdv entity);

    @Override
    HomeAdv getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
