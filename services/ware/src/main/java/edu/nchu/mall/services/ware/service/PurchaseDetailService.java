package edu.nchu.mall.services.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.PurchaseDetail;

import java.io.Serializable;
import java.util.List;

public interface PurchaseDetailService extends IService<PurchaseDetail> {
    @Override
    boolean updateById(PurchaseDetail entity);

    @Override
    PurchaseDetail getById(Serializable id);

    @Override
    boolean save(PurchaseDetail entity);

    @Override
    boolean removeById(Serializable id);

    List<PurchaseDetail> list(Integer pageNum, Integer pageSize, Integer status, Long wareId, String key);
}
