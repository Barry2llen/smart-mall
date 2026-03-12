package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.annotation.notice.Notice;
import edu.nchu.mall.models.dto.MemberReceiveAddressDTO;
import edu.nchu.mall.models.entity.MemberReceiveAddress;
import edu.nchu.mall.models.vo.MemberReceiveAddressVO;
import edu.nchu.mall.services.member.dao.MemberReceiveAddressMapper;
import edu.nchu.mall.services.member.notice.event.impl.FlashSaleUserInfoChanged;
import edu.nchu.mall.services.member.service.MemberReceiveAddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "memberReceiveAddress")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressMapper, MemberReceiveAddress> implements MemberReceiveAddressService {

    @Override
    @Notice(event = FlashSaleUserInfoChanged.class)
    @Caching(evict = {
            @CacheEvict(key = "#dto.id"),
            @CacheEvict(key = "'list' + #dto.memberId")
    })
    public boolean updateById(MemberReceiveAddressDTO dto) {
        MemberReceiveAddress entity = new MemberReceiveAddress();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    @Notice(event = FlashSaleUserInfoChanged.class)
    @Cacheable(key = "#id")
    public MemberReceiveAddressVO getMemberReceiveAddressById(Serializable id) {
        MemberReceiveAddress entity = super.getById(id);
        if (entity == null) return null;
        MemberReceiveAddressVO vo = new MemberReceiveAddressVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    @Override
    @Notice(event = FlashSaleUserInfoChanged.class)
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'list' + #id")
    })
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Notice(event = FlashSaleUserInfoChanged.class)
    @Caching(evict = {
            @CacheEvict(key = "'list' + #dto.memberId")
    })
    public boolean save(MemberReceiveAddressDTO dto) {
        MemberReceiveAddress entity = new MemberReceiveAddress();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    @Cacheable(key = "'list' + #memberId")
    public List<MemberReceiveAddress> getMemberReceiveAddresses(Long memberId) {
        return super.list(Wrappers.<MemberReceiveAddress>lambdaQuery().eq(MemberReceiveAddress::getMemberId, memberId));
    }
}
