package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.CouponSpuCategoryRelation;

import java.io.Serializable;
import java.util.List;

public interface CouponSpuCategoryRelationService extends IService<CouponSpuCategoryRelation> {
    @Override
    boolean updateById(CouponSpuCategoryRelation entity);

    @Override
    CouponSpuCategoryRelation getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    List<CouponSpuCategoryRelation> list(Integer pageNum, Integer pageSize);
}
