package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.CategoryBrandRelation;

import java.io.Serializable;

public interface CategoryBrandRelationService extends IService<CategoryBrandRelation> {
    @Override
    boolean updateById(CategoryBrandRelation entity);

    @Override
    CategoryBrandRelation getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
