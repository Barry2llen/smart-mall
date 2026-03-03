package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.HomeSubjectSpu;

import java.io.Serializable;
import java.util.List;

public interface HomeSubjectSpuService extends IService<HomeSubjectSpu> {
    @Override
    boolean updateById(HomeSubjectSpu entity);

    @Override
    HomeSubjectSpu getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    List<HomeSubjectSpu> list(Integer pageNum, Integer pageSize);
}
