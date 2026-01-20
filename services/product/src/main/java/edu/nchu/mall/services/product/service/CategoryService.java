package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.Category;

import java.io.Serializable;

public interface CategoryService extends IService<Category> {
    @Override
    boolean updateById(Category entity);

    @Override
    Category getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
