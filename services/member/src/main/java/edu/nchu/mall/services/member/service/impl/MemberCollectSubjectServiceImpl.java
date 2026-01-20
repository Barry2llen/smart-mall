package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.MemberCollectSubject;
import edu.nchu.mall.services.member.dao.MemberCollectSubjectMapper;
import edu.nchu.mall.services.member.service.MemberCollectSubjectService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "memberCollectSubject")
@Transactional
public class MemberCollectSubjectServiceImpl extends ServiceImpl<MemberCollectSubjectMapper, MemberCollectSubject> implements MemberCollectSubjectService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(MemberCollectSubject entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public MemberCollectSubject getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
