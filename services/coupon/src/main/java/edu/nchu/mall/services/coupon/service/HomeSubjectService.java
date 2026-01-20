package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.HomeSubject;

import java.io.Serializable;

public interface HomeSubjectService extends IService<HomeSubject> {
    @Override
    boolean updateById(HomeSubject entity);

    @Override
    HomeSubject getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
