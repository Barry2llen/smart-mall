package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.CategoryBrandRelation;
import edu.nchu.mall.models.vo.CategoryBrandRelationVO;
import edu.nchu.mall.services.product.dao.BrandMapper;
import edu.nchu.mall.services.product.dao.CategoryBrandRelationMapper;
import edu.nchu.mall.services.product.dao.CategoryMapper;
import edu.nchu.mall.services.product.service.BrandService;
import edu.nchu.mall.services.product.service.CategoryBrandRelationService;
import edu.nchu.mall.services.product.service.CategoryService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "categoryBrandRelation")
@Transactional
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationMapper, CategoryBrandRelation> implements CategoryBrandRelationService {

    @Autowired
    BrandMapper brandMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @CacheEvict(cacheNames = "categoryBrandRelation:brand", key = "#brandId")
    public void removeCacheByBrandId(Long brandId){
    }

    @CacheEvict(cacheNames = "categoryBrandRelation:cat", key = "#catelogId")
    public void removeCacheByCatelogId(Long catelogId){
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "categoryBrandRelation:brand", key = "#entity.brandId"),
            @CacheEvict(cacheNames = "categoryBrandRelation:cat", key = "#entity.catelogId")
    })
    public boolean updateById(CategoryBrandRelation entity) {
        return super.updateById(entity);
    }

    @Override
    public boolean update(Wrapper<CategoryBrandRelation> wrapper) {
        return super.update(wrapper);
    }

    @Override
    @Cacheable(key = "#id")
    public CategoryBrandRelation getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "categoryBrandRelation:brand", key = "#brandId"),
            @CacheEvict(cacheNames = "categoryBrandRelation:cat", key = "#catelogId")
    })
    public boolean save(Long brandId, Long catelogId) {
        CategoryBrandRelation entity = new CategoryBrandRelation();
        entity.setBrandId(brandId);
        entity.setCatelogId(catelogId);
        entity.setBrandName(brandMapper.selectById(brandId).getName());
        entity.setCatelogName(categoryMapper.selectById(catelogId).getName());
        return super.save(entity);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        var self = (CategoryBrandRelationServiceImpl)AopContext.currentProxy();

        CategoryBrandRelation entity = self.getById(id);
        removeCacheByBrandId(entity.getBrandId());
        removeCacheByCatelogId(entity.getCatelogId());

        return super.removeById(id);
    }

    @Override
    @Cacheable(cacheNames = "categoryBrandRelation:brand", key = "#brandId")
    public List<CategoryBrandRelationVO> getRelatedCategoriesByBrandId(Long brandId) {
        LambdaQueryWrapper<CategoryBrandRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryBrandRelation::getBrandId, brandId);
        List<CategoryBrandRelation> res = this.list(queryWrapper);
        return res.stream().map(entity -> {
            CategoryBrandRelationVO vo = new CategoryBrandRelationVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }

    @Override
    @Cacheable(cacheNames = "categoryBrandRelation:cat", key = "#catId")
    public List<CategoryBrandRelationVO> getRelatedBrandsByCatId(Long catId) {
        LambdaQueryWrapper<CategoryBrandRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryBrandRelation::getCatelogId, catId);
        List<CategoryBrandRelation> res = this.list(queryWrapper);
        return res.stream().map(entity -> {
            CategoryBrandRelationVO vo = new CategoryBrandRelationVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }
}
