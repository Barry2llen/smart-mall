package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SkuSaleAttrValue;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValue> {
    @Override
    boolean updateById(SkuSaleAttrValue entity);

    @Override
    SkuSaleAttrValue getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    List<String> getSkuAttrValues(Long skuId);

    Map<Long, List<String>> getBatchSkuAttrValues(Collection<Long> skuIds);
}
