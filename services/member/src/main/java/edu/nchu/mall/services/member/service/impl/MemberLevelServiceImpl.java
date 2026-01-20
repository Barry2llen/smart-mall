package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.MemberLevel;
import edu.nchu.mall.services.member.dao.MemberLevelMapper;
import edu.nchu.mall.services.member.service.MemberLevelService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "memberLevel")
@Transactional
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelMapper, MemberLevel> implements MemberLevelService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(MemberLevel entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public MemberLevel getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
