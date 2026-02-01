package edu.nchu.mall.services.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.components.utils.KeyUtils;
import edu.nchu.mall.models.entity.PurchaseDetail;
import edu.nchu.mall.services.ware.dao.PurchaseDetailMapper;
import edu.nchu.mall.services.ware.service.PurchaseDetailService;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "purchaseDetail")
@Transactional
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailMapper, PurchaseDetail> implements PurchaseDetailService {

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#entity.id"),
            @CacheEvict(cacheNames = "purchaseDetail:list", allEntries = true)
    })
    public boolean updateById(PurchaseDetail entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public PurchaseDetail getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#entity.id"),
            @CacheEvict(cacheNames = "purchaseDetail:list", allEntries = true)
    })
    public boolean save(PurchaseDetail entity) {
        return super.save(entity);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(cacheNames = "purchaseDetail:list", allEntries = true)
    })
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<PurchaseDetail> list(Integer pageNum, Integer pageSize, Integer status, Long wareId, String key) {
        if (status == null && wareId == null && KeyUtils.isEmpty(key)) {
            var self = ((PurchaseDetailServiceImpl) AopContext.currentProxy());
            return self.list(pageNum, pageSize);
        }

        IPage<PurchaseDetail> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseDetail> qw = Wrappers.lambdaQuery();
        qw.eq(status != null, PurchaseDetail::getStatus, status)
                .eq(wareId != null, PurchaseDetail::getWareId, wareId);
        if (!KeyUtils.isEmpty(key)) {
            qw.and(wrapper -> wrapper.like(PurchaseDetail::getPurchaseId, key)
                    .or()
                    .like(PurchaseDetail::getSkuId, key));
        }
        return super.list(page, qw);
    }

    @Cacheable(cacheNames = "purchaseDetail:list", key = "#pageNum + ':' + #pageSize")
    public List<PurchaseDetail> list(Integer pageNum, Integer pageSize) {
        return super.list(new Page<>(pageNum, pageSize));
    }
}
