package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SeckillPromotion;

import java.io.Serializable;

public interface SeckillPromotionService extends IService<SeckillPromotion> {
    @Override
    boolean updateById(SeckillPromotion entity);

    @Override
    SeckillPromotion getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
