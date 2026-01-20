package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.MemberPrice;

import java.io.Serializable;

public interface MemberPriceService extends IService<MemberPrice> {
    @Override
    boolean updateById(MemberPrice entity);

    @Override
    MemberPrice getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
