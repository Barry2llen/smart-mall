package edu.nchu.mall.services.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.Purchase;
import edu.nchu.mall.services.ware.dao.PurchaseMapper;
import edu.nchu.mall.services.ware.service.PurchaseService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "purchase")
@Transactional
public class PurchaseServiceImpl extends ServiceImpl<PurchaseMapper, Purchase> implements PurchaseService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Purchase entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public Purchase getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
