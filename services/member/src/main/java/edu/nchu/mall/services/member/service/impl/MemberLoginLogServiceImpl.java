package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.dto.MemberLoginLogDTO;
import edu.nchu.mall.models.entity.MemberLoginLog;
import edu.nchu.mall.models.vo.MemberLoginLogVO;
import edu.nchu.mall.services.member.dao.MemberLoginLogMapper;
import edu.nchu.mall.services.member.service.MemberLoginLogService;
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
@CacheConfig(cacheNames = "memberLoginLog")
public class MemberLoginLogServiceImpl extends ServiceImpl<MemberLoginLogMapper, MemberLoginLog> implements MemberLoginLogService {

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.id"),
            @CacheEvict(key = "'list'")
    })
    public boolean updateById(MemberLoginLogDTO dto) {
        MemberLoginLog entity = new MemberLoginLog();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public MemberLoginLogVO getMemberLoginLogById(Serializable id) {
        MemberLoginLog entity = super.getById(id);
        if (entity == null) return null;
        MemberLoginLogVO vo = new MemberLoginLogVO();
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
    public boolean save(MemberLoginLogDTO dto) {
        MemberLoginLog entity = new MemberLoginLog();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    @Cacheable(key = "'list'")
    public List<MemberLoginLogVO> getMemberLoginLogs(Integer pageNum, Integer pageSize) {
        IPage<MemberLoginLog> page = new Page<>(pageNum, pageSize);
        return super.list(page).stream().map(entity -> {
            MemberLoginLogVO vo = new MemberLoginLogVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }
}
