package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.CouponHistory;

import java.io.Serializable;
import java.util.List;

public interface CouponHistoryService extends IService<CouponHistory> {
    @Override
    boolean updateById(CouponHistory entity);

    @Override
    CouponHistory getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    List<CouponHistory> list(Integer pageNum, Integer pageSize, String memberNickName, Integer useType);
}
