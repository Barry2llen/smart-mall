package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SkuLadder;

import java.io.Serializable;
import java.util.List;

public interface SkuLadderService extends IService<SkuLadder> {
    @Override
    boolean updateById(SkuLadder entity);

    @Override
    SkuLadder getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    List<SkuLadder> list(Integer pageNum, Integer pageSize);
}
