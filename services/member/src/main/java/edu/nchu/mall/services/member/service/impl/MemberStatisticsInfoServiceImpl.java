package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.MemberStatisticsInfo;
import edu.nchu.mall.services.member.dao.MemberStatisticsInfoMapper;
import edu.nchu.mall.services.member.service.MemberStatisticsInfoService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "memberStatisticsInfo")
@Transactional
public class MemberStatisticsInfoServiceImpl extends ServiceImpl<MemberStatisticsInfoMapper, MemberStatisticsInfo> implements MemberStatisticsInfoService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(MemberStatisticsInfo entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public MemberStatisticsInfo getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
