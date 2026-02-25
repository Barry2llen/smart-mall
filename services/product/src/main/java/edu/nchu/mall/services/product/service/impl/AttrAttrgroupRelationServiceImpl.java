package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.AttrAttrgroupRelation;
import edu.nchu.mall.services.product.dao.AttrAttrgroupRelationMapper;
import edu.nchu.mall.services.product.service.AttrAttrgroupRelationService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "attrAttrgroupRelation")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationMapper, AttrAttrgroupRelation> implements AttrAttrgroupRelationService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(AttrAttrgroupRelation entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public AttrAttrgroupRelation getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
