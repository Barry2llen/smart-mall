package edu.nchu.mall.services.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.RefundInfo;
import edu.nchu.mall.services.order.dao.RefundInfoMapper;
import edu.nchu.mall.services.order.service.RefundInfoService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "refundInfo")
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(RefundInfo entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public RefundInfo getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
