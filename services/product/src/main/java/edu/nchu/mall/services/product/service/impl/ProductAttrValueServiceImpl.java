package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.ProductAttrValue;
import edu.nchu.mall.services.product.dao.ProductAttrValueMapper;
import edu.nchu.mall.services.product.service.ProductAttrValueService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "productAttrValue")
@Transactional
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueMapper, ProductAttrValue> implements ProductAttrValueService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(ProductAttrValue entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public ProductAttrValue getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
