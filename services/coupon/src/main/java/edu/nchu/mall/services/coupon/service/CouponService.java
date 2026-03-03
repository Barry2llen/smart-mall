package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.Coupon;

import java.io.Serializable;
import java.util.List;

public interface CouponService extends IService<Coupon> {
    @Override
    boolean updateById(Coupon entity);

    @Override
    Coupon getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    List<Coupon> list(Integer pageNum, Integer pageSize, String couponName, Integer couponType, Integer publish);
}
