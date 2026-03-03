package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SeckillSkuRelation;

import java.io.Serializable;
import java.util.List;

public interface SeckillSkuRelationService extends IService<SeckillSkuRelation> {
    @Override
    boolean updateById(SeckillSkuRelation entity);

    @Override
    SeckillSkuRelation getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    List<SeckillSkuRelation> list(Integer pageNum, Integer pageSize, String key, Long promotionSessionId);
}
