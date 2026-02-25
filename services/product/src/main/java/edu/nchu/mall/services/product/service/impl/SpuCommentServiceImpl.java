package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SpuComment;
import edu.nchu.mall.services.product.dao.SpuCommentMapper;
import edu.nchu.mall.services.product.service.SpuCommentService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "spuComment")
public class SpuCommentServiceImpl extends ServiceImpl<SpuCommentMapper, SpuComment> implements SpuCommentService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SpuComment entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SpuComment getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
