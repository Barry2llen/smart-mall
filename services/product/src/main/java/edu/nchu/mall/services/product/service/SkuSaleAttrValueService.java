package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SkuSaleAttrValue;

import java.io.Serializable;

public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValue> {
    @Override
    boolean updateById(SkuSaleAttrValue entity);

    @Override
    SkuSaleAttrValue getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
