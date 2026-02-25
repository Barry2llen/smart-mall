package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.dto.MemberStatisticsInfoDTO;
import edu.nchu.mall.models.entity.MemberStatisticsInfo;
import edu.nchu.mall.models.vo.MemberStatisticsInfoVO;
import edu.nchu.mall.services.member.dao.MemberStatisticsInfoMapper;
import edu.nchu.mall.services.member.service.MemberStatisticsInfoService;
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
@CacheConfig(cacheNames = "memberStatisticsInfo")
public class MemberStatisticsInfoServiceImpl extends ServiceImpl<MemberStatisticsInfoMapper, MemberStatisticsInfo> implements MemberStatisticsInfoService {

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.id"),
            @CacheEvict(key = "'list'")
    })
    public boolean updateById(MemberStatisticsInfoDTO dto) {
        MemberStatisticsInfo entity = new MemberStatisticsInfo();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public MemberStatisticsInfoVO getMemberStatisticsInfoById(Serializable id) {
        MemberStatisticsInfo entity = super.getById(id);
        if (entity == null) return null;
        MemberStatisticsInfoVO vo = new MemberStatisticsInfoVO();
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
    public boolean save(MemberStatisticsInfoDTO dto) {
        MemberStatisticsInfo entity = new MemberStatisticsInfo();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    @Cacheable(key = "'list'")
    public List<MemberStatisticsInfoVO> getMemberStatisticsInfos(Integer pageNum, Integer pageSize) {
        IPage<MemberStatisticsInfo> page = new Page<>(pageNum, pageSize);
        return super.list(page).stream().map(entity -> {
            MemberStatisticsInfoVO vo = new MemberStatisticsInfoVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }
}
