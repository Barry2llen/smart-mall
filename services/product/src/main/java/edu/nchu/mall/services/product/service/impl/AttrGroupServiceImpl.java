package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.AttrGroup;
import edu.nchu.mall.services.product.dao.AttrGroupMapper;
import edu.nchu.mall.services.product.service.AttrGroupService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "attrGroup")
@Transactional
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {

    @Override
    @CacheEvict(key = "#entity.attrGroupId")
    public boolean updateById(AttrGroup entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public AttrGroup getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
