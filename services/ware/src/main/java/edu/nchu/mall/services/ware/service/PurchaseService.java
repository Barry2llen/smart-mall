package edu.nchu.mall.services.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.PurchaseMergeDTO;
import edu.nchu.mall.models.entity.Purchase;
import jakarta.validation.Valid;

import java.io.Serializable;
import java.util.List;

public interface PurchaseService extends IService<Purchase> {
    @Override
    boolean updateById(Purchase entity);

    @Override
    Purchase getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    @Override
    boolean save(Purchase entity);

    List<Purchase> list(Integer pageNum, Integer pageSize);

    List<Purchase> listUnassignedPurchases(Integer pageNum, Integer pageSize);

    boolean merge(PurchaseMergeDTO dto);
}
