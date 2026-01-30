package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.dto.MemberDTO;
import edu.nchu.mall.models.entity.Member;
import edu.nchu.mall.models.vo.MemberVO;
import edu.nchu.mall.services.member.dao.MemberMapper;
import edu.nchu.mall.services.member.service.MemberService;
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
@CacheConfig(cacheNames = "member")
@Transactional
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.id"),
            @CacheEvict(key = "'list'")
    })
    public boolean updateById(MemberDTO dto) {
        Member entity = new Member();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public MemberVO getMemberById(Serializable id) {
        Member entity = super.getById(id);
        if (entity == null) return null;
        MemberVO vo = new MemberVO();
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
    public boolean save(MemberDTO dto) {
        Member entity = new Member();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    @Cacheable(key = "'list'")
    public List<MemberVO> getMembers(Integer pageNum, Integer pageSize) {
        IPage<Member> page = new Page<>(pageNum, pageSize);
        return super.list(page).stream().map(entity -> {
            MemberVO vo = new MemberVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }
}
