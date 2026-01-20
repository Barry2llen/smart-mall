package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.AttrAttrgroupRelation;

import java.io.Serializable;

public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelation> {
    @Override
    boolean updateById(AttrAttrgroupRelation entity);

    @Override
    AttrAttrgroupRelation getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
