package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.dto.GrowthChangeHistoryDTO;
import edu.nchu.mall.models.entity.GrowthChangeHistory;
import edu.nchu.mall.models.vo.GrowthChangeHistoryVO;
import edu.nchu.mall.services.member.dao.GrowthChangeHistoryMapper;
import edu.nchu.mall.services.member.service.GrowthChangeHistoryService;
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
@CacheConfig(cacheNames = "growthChangeHistory")
@Transactional
public class GrowthChangeHistoryServiceImpl extends ServiceImpl<GrowthChangeHistoryMapper, GrowthChangeHistory> implements GrowthChangeHistoryService {

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.id"),
            @CacheEvict(key = "'list'")
    })
    public boolean updateById(GrowthChangeHistoryDTO dto) {
        GrowthChangeHistory entity = new GrowthChangeHistory();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public GrowthChangeHistoryVO getGrowthChangeHistoryById(Serializable id) {
        GrowthChangeHistory entity = super.getById(id);
        if (entity == null) return null;
        GrowthChangeHistoryVO vo = new GrowthChangeHistoryVO();
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
    public boolean save(GrowthChangeHistoryDTO dto) {
        GrowthChangeHistory entity = new GrowthChangeHistory();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    @Cacheable(key = "'list'")
    public List<GrowthChangeHistoryVO> getGrowthChangeHistories(Integer pageNum, Integer pageSize) {
        IPage<GrowthChangeHistory> page = new Page<>(pageNum, pageSize);
        return super.list(page).stream().map(entity -> {
            GrowthChangeHistoryVO vo = new GrowthChangeHistoryVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }
}
