package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.AttrGroup;

import java.io.Serializable;

public interface AttrGroupService extends IService<AttrGroup> {
    @Override
    boolean updateById(AttrGroup entity);

    @Override
    AttrGroup getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
