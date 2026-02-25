package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.dto.MemberLevelDTO;
import edu.nchu.mall.models.entity.MemberLevel;
import edu.nchu.mall.models.vo.MemberLevelVO;
import edu.nchu.mall.services.member.dao.MemberLevelMapper;
import edu.nchu.mall.services.member.service.MemberLevelService;
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
@CacheConfig(cacheNames = "memberLevel")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelMapper, MemberLevel> implements MemberLevelService {

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.id"),
            @CacheEvict(key = "'list'")
    })
    public boolean updateById(MemberLevelDTO dto) {
        MemberLevel entity = new MemberLevel();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public MemberLevelVO getMemberLevelById(Serializable id) {
        MemberLevel entity = super.getById(id);
        if (entity == null) return null;
        MemberLevelVO vo = new MemberLevelVO();
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
    public boolean save(MemberLevelDTO dto) {
        MemberLevel entity = new MemberLevel();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    @Cacheable(key = "'list'")
    public List<MemberLevelVO> getMemberLevels(Integer pageNum, Integer pageSize) {
        IPage<MemberLevel> page = new Page<>(pageNum, pageSize);
        return super.list(page).stream().map(entity -> {
            MemberLevelVO vo = new MemberLevelVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }
}
