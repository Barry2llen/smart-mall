package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.components.feign.OSSFeignClient;
import edu.nchu.mall.models.dto.BrandDTO;
import edu.nchu.mall.models.entity.Brand;
import edu.nchu.mall.services.product.dao.BrandMapper;
import edu.nchu.mall.services.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "brand")
@Transactional
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#entity.brandId"),
            @CacheEvict(key = "'list'")
    })
    public boolean updateById(Brand entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public Brand getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'list'")
    })
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Cacheable(key = "'list'")
    public List<Brand> list(){
        return super.list();
    }
}
