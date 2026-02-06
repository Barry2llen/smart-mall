package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.Category;
import edu.nchu.mall.models.vo.CategoryVO;
import org.springframework.beans.PropertyValues;

import java.io.Serializable;
import java.util.List;

public interface CategoryService extends IService<Category> {
    @Override
    boolean updateById(Category entity);

    @Override
    Category getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    List<CategoryVO> listWithTree();

    boolean removeByIds(List<Long> list);

    @Override
    boolean save(Category entity);

    List<Category> seqByIds(List<Long> ids);
}
