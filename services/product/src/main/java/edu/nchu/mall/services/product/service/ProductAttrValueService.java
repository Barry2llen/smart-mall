package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.ProductAttrValue;

import java.io.Serializable;

public interface ProductAttrValueService extends IService<ProductAttrValue> {
    @Override
    boolean updateById(ProductAttrValue entity);

    @Override
    ProductAttrValue getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
