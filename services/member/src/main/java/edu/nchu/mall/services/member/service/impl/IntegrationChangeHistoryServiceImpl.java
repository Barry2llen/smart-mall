package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.dto.IntegrationChangeHistoryDTO;
import edu.nchu.mall.models.entity.IntegrationChangeHistory;
import edu.nchu.mall.models.vo.IntegrationChangeHistoryVO;
import edu.nchu.mall.services.member.dao.IntegrationChangeHistoryMapper;
import edu.nchu.mall.services.member.service.IntegrationChangeHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "integrationChangeHistory")
public class IntegrationChangeHistoryServiceImpl extends ServiceImpl<IntegrationChangeHistoryMapper, IntegrationChangeHistory> implements IntegrationChangeHistoryService {

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.id"),
            @CacheEvict(key = "'list'")
    })
    public boolean updateById(IntegrationChangeHistoryDTO dto) {
        IntegrationChangeHistory entity = new IntegrationChangeHistory();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public IntegrationChangeHistoryVO getIntegrationChangeHistoryById(Serializable id) {
        IntegrationChangeHistory entity = super.getById(id);
        if (entity == null) return null;
        IntegrationChangeHistoryVO vo = new IntegrationChangeHistoryVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'list'")
    })
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "'list'")
    })
    public boolean save(IntegrationChangeHistoryDTO dto) {
        IntegrationChangeHistory entity = new IntegrationChangeHistory();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    @Cacheable(key = "'list'")
    public List<IntegrationChangeHistoryVO> getIntegrationChangeHistories(Integer pageNum, Integer pageSize) {
        IPage<IntegrationChangeHistory> page = new Page<>(pageNum, pageSize);
        return super.list(page).stream().map(entity -> {
            IntegrationChangeHistoryVO vo = new IntegrationChangeHistoryVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }
}
