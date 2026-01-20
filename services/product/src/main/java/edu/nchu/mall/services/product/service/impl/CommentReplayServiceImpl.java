package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.CommentReplay;
import edu.nchu.mall.services.product.dao.CommentReplayMapper;
import edu.nchu.mall.services.product.service.CommentReplayService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "commentReplay")
@Transactional
public class CommentReplayServiceImpl extends ServiceImpl<CommentReplayMapper, CommentReplay> implements CommentReplayService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(CommentReplay entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public CommentReplay getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
