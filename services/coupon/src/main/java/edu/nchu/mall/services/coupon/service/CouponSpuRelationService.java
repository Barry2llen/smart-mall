package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.CouponSpuRelation;

import java.io.Serializable;

public interface CouponSpuRelationService extends IService<CouponSpuRelation> {
    @Override
    boolean updateById(CouponSpuRelation entity);

    @Override
    CouponSpuRelation getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
