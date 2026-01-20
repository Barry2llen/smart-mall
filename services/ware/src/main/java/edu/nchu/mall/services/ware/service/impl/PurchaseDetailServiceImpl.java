package edu.nchu.mall.services.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.PurchaseDetail;
import edu.nchu.mall.services.ware.dao.PurchaseDetailMapper;
import edu.nchu.mall.services.ware.service.PurchaseDetailService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "purchaseDetail")
@Transactional
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailMapper, PurchaseDetail> implements PurchaseDetailService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(PurchaseDetail entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public PurchaseDetail getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
