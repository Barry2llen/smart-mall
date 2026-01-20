package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.IntegrationChangeHistory;
import edu.nchu.mall.services.member.dao.IntegrationChangeHistoryMapper;
import edu.nchu.mall.services.member.service.IntegrationChangeHistoryService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "integrationChangeHistory")
@Transactional
public class IntegrationChangeHistoryServiceImpl extends ServiceImpl<IntegrationChangeHistoryMapper, IntegrationChangeHistory> implements IntegrationChangeHistoryService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(IntegrationChangeHistory entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public IntegrationChangeHistory getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
