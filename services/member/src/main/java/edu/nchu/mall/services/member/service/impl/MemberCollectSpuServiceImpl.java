package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.MemberCollectSpu;
import edu.nchu.mall.services.member.dao.MemberCollectSpuMapper;
import edu.nchu.mall.services.member.service.MemberCollectSpuService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "memberCollectSpu")
@Transactional
public class MemberCollectSpuServiceImpl extends ServiceImpl<MemberCollectSpuMapper, MemberCollectSpu> implements MemberCollectSpuService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(MemberCollectSpu entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public MemberCollectSpu getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
