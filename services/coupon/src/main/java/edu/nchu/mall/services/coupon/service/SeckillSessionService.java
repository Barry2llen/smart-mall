package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SeckillSession;

import java.io.Serializable;

public interface SeckillSessionService extends IService<SeckillSession> {
    @Override
    boolean updateById(SeckillSession entity);

    @Override
    SeckillSession getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
