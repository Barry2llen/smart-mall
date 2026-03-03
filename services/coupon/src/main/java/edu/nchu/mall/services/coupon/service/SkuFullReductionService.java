package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.SkuReductionDTO;
import edu.nchu.mall.models.entity.SkuFullReduction;

import java.io.Serializable;
import java.util.List;

public interface SkuFullReductionService extends IService<SkuFullReduction> {
    @Override
    boolean updateById(SkuFullReduction entity);

    @Override
    SkuFullReduction getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    List<SkuFullReduction> list(Integer pageNum, Integer pageSize);

    boolean saveSkuReduction(SkuReductionDTO dto);
}
