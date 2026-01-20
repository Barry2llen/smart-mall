package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.GrowthChangeHistory;
import edu.nchu.mall.services.member.dao.GrowthChangeHistoryMapper;
import edu.nchu.mall.services.member.service.GrowthChangeHistoryService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "growthChangeHistory")
@Transactional
public class GrowthChangeHistoryServiceImpl extends ServiceImpl<GrowthChangeHistoryMapper, GrowthChangeHistory> implements GrowthChangeHistoryService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(GrowthChangeHistory entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public GrowthChangeHistory getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
