package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SeckillSkuNotice;

import java.io.Serializable;

public interface SeckillSkuNoticeService extends IService<SeckillSkuNotice> {
    @Override
    boolean updateById(SeckillSkuNotice entity);

    @Override
    SeckillSkuNotice getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
