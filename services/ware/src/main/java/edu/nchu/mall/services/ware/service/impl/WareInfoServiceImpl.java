package edu.nchu.mall.services.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.components.utils.KeyUtils;
import edu.nchu.mall.models.entity.WareInfo;
import edu.nchu.mall.services.ware.dao.WareInfoMapper;
import edu.nchu.mall.services.ware.service.WareInfoService;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "wareInfo")
@Transactional
public class WareInfoServiceImpl extends ServiceImpl<WareInfoMapper, WareInfo> implements WareInfoService {

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#entity.id"),
            @CacheEvict(cacheNames = "wareinfo:list", allEntries = true)
    })
    public boolean updateById(WareInfo entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public WareInfo getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(cacheNames = "wareinfo:list", allEntries = true)
    })
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<WareInfo> list(Integer pageNum, Integer pageSize, String key) {
        if (key == null){
            var self = ((WareInfoServiceImpl) AopContext.currentProxy());
            return self.list(pageNum, pageSize);
        }

        IPage<WareInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WareInfo> qw = Wrappers.lambdaQuery();
        Optional<Long> id = KeyUtils.parseKey2Long(key);
        qw.like(WareInfo::getName, key)
                .or(id.isPresent(), wrapper -> wrapper.eq(WareInfo::getId, id.get()));
        return super.list(page, qw);
    }

    @Cacheable(cacheNames = "wareinfo:list", key = "#pageNum + ':' + #pageSize")
    public List<WareInfo> list(Integer pageNum, Integer pageSize) {
        IPage<WareInfo> page = new Page<>(pageNum, pageSize);
        return super.list(page);
    }
}
