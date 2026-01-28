package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.CategoryBrandRelation;
import edu.nchu.mall.models.vo.CategoryBrandRelationVO;

import java.io.Serializable;
import java.util.List;

public interface CategoryBrandRelationService extends IService<CategoryBrandRelation> {
    @Override
    boolean updateById(CategoryBrandRelation entity);

    boolean removeCacheByBrandId(Long brandId);

    @Override
    boolean update(Wrapper<CategoryBrandRelation> wrapper);

    @Override
    CategoryBrandRelation getById(Serializable id);

    boolean save(Long brandId, Long catelogId);

    @Override
    boolean removeById(Serializable id);

    List<CategoryBrandRelationVO> getRelatedCategoriesByBrandId(Long brandId);
}
