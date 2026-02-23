package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.dto.MemberReceiveAddressDTO;
import edu.nchu.mall.models.entity.MemberReceiveAddress;
import edu.nchu.mall.models.vo.MemberReceiveAddressVO;
import edu.nchu.mall.services.member.dao.MemberReceiveAddressMapper;
import edu.nchu.mall.services.member.service.MemberReceiveAddressService;
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
@CacheConfig(cacheNames = "memberReceiveAddress")
@Transactional
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressMapper, MemberReceiveAddress> implements MemberReceiveAddressService {

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.id"),
            @CacheEvict(key = "'list'")
    })
    public boolean updateById(MemberReceiveAddressDTO dto) {
        MemberReceiveAddress entity = new MemberReceiveAddress();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public MemberReceiveAddressVO getMemberReceiveAddressById(Serializable id) {
        MemberReceiveAddress entity = super.getById(id);
        if (entity == null) return null;
        MemberReceiveAddressVO vo = new MemberReceiveAddressVO();
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
    public boolean save(MemberReceiveAddressDTO dto) {
        MemberReceiveAddress entity = new MemberReceiveAddress();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    @Cacheable(key = "'list'")
    public List<MemberReceiveAddress> getMemberReceiveAddresses(Long memberId) {
        return super.list(Wrappers.<MemberReceiveAddress>lambdaQuery().eq(MemberReceiveAddress::getMemberId, memberId));
    }
}
