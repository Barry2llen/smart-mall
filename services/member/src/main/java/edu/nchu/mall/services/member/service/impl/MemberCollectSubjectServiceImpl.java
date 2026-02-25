package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.dto.MemberCollectSubjectDTO;
import edu.nchu.mall.models.entity.MemberCollectSubject;
import edu.nchu.mall.models.vo.MemberCollectSubjectVO;
import edu.nchu.mall.services.member.dao.MemberCollectSubjectMapper;
import edu.nchu.mall.services.member.service.MemberCollectSubjectService;
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
@CacheConfig(cacheNames = "memberCollectSubject")
public class MemberCollectSubjectServiceImpl extends ServiceImpl<MemberCollectSubjectMapper, MemberCollectSubject> implements MemberCollectSubjectService {

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.id"),
            @CacheEvict(key = "'list'")
    })
    public boolean updateById(MemberCollectSubjectDTO dto) {
        MemberCollectSubject entity = new MemberCollectSubject();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public MemberCollectSubjectVO getMemberCollectSubjectById(Serializable id) {
        MemberCollectSubject entity = super.getById(id);
        if (entity == null) return null;
        MemberCollectSubjectVO vo = new MemberCollectSubjectVO();
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
    public boolean save(MemberCollectSubjectDTO dto) {
        MemberCollectSubject entity = new MemberCollectSubject();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    @Cacheable(key = "'list'")
    public List<MemberCollectSubjectVO> getMemberCollectSubjects(Integer pageNum, Integer pageSize) {
        IPage<MemberCollectSubject> page = new Page<>(pageNum, pageSize);
        return super.list(page).stream().map(entity -> {
            MemberCollectSubjectVO vo = new MemberCollectSubjectVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }
}
