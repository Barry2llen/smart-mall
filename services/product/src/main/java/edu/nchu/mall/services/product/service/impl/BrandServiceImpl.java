package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.Brand;
import edu.nchu.mall.models.entity.CategoryBrandRelation;
import edu.nchu.mall.models.vo.CategoryBrandRelationVO;
import edu.nchu.mall.services.product.dao.BrandMapper;
import edu.nchu.mall.services.product.service.BrandService;
import edu.nchu.mall.services.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "brand")
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#entity.brandId"),
            @CacheEvict(value = "brand:list", allEntries = true)
    })
    public boolean updateById(Brand entity) {
        if(entity.getName() != null){
            //同步更新关联表中的品牌名称
            List<CategoryBrandRelationVO> relations = categoryBrandRelationService.getRelatedCategoriesByBrandId(entity.getBrandId());
            LambdaUpdateWrapper<CategoryBrandRelation> wrapper = Wrappers.lambdaUpdate();
            wrapper.in(CategoryBrandRelation::getBrandId,
                    relations.stream().map(CategoryBrandRelationVO::getId).toList())
                    .set(CategoryBrandRelation::getBrandName, entity.getName());
            categoryBrandRelationService.update(wrapper);
            categoryBrandRelationService.removeCacheByBrandId(entity.getBrandId());
        }
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
            @CacheEvict(value = "brand:list", allEntries = true)
    })
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "brand:list", allEntries = true)
    })
    public boolean save(Brand entity) {
        return super.save(entity);
    }

    @Override
    @Cacheable(value = "brand:list", key = "#page.current + ':' + #page.size")
    public List<Brand> list(IPage<Brand> page){
        return super.list(page);
    }

    @Override
    public List<Brand> seqByIds(Collection<Long> ids) {
        Map<Long, Brand> collect = super.listByIds(ids).stream().collect(Collectors.toMap(Brand::getBrandId, brand -> brand));
        return ids.stream().map(collect::get).toList();
    }
}
