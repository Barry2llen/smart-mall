package edu.nchu.mall.services.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.enums.PurchaseDetailStatus;
import edu.nchu.mall.models.enums.PurchaseStatus;
import edu.nchu.mall.models.dto.PurchaseMergeDTO;
import edu.nchu.mall.models.entity.Purchase;
import edu.nchu.mall.models.entity.PurchaseDetail;
import edu.nchu.mall.services.ware.dao.PurchaseMapper;
import edu.nchu.mall.services.ware.service.PurchaseDetailService;
import edu.nchu.mall.services.ware.service.PurchaseService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Service
@CacheConfig(cacheNames = "purchase")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseMapper, Purchase> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    public PurchaseServiceImpl(PurchaseDetailService purchaseDetailService) {
        this.purchaseDetailService = purchaseDetailService;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#entity.id"),
            @CacheEvict(cacheNames = "purchase:list", allEntries = true)
    })
    public boolean updateById(Purchase entity) {
        entity.setUpdateTime(LocalDateTime.now());
        if (entity.getAssigneeId() != null){
            entity.setStatus(PurchaseStatus.ASSIGNED.getCode());
        }
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public Purchase getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(cacheNames = "purchase:list", allEntries = true)
    })
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CacheEvict(cacheNames = "purchase:list", allEntries = true)
    public boolean save(Purchase entity) {
        entity.setCreateTime(LocalDateTime.now());
//        if (entity.getAssigneeId() != null){
//            entity.setStatus(PurchaseStatus.ASSIGNED.getCode());
//        }
        return super.save(entity);
    }

    @Override
    @Cacheable(cacheNames = "purchase:list", key = "#pageNum + ':' + #pageSize")
    public List<Purchase> list(Integer pageNum, Integer pageSize) {
        return super.list(new Page<>(pageNum, pageSize));
    }

    @Override
    @Cacheable(cacheNames = "purchase:list", key = "'unassigned' + ':' + #pageNum + ':' + #pageSize")
    public List<Purchase> listUnassignedPurchases(Integer pageNum, Integer pageSize) {
        Page<Purchase> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Purchase> qw = Wrappers.lambdaQuery();
        qw.eq(Purchase::getStatus, PurchaseStatus.CREATED.getCode());
        return super.list(page, qw);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "purchase:list", allEntries = true),
            @CacheEvict(cacheNames = "purchaseDetail:list", allEntries = true)
    })
    public boolean merge(PurchaseMergeDTO dto) {
        if (dto.getPurchaseId() == null) {
            var self = (PurchaseServiceImpl) AopContext.currentProxy();

            Purchase purchase = new Purchase();
            purchase.setStatus(PurchaseStatus.CREATED.getCode());
            purchase.setCreateTime(LocalDateTime.now());
            self.save(purchase);
            dto.setPurchaseId(purchase.getId());
        }

        Long purchaseId = dto.getPurchaseId();
        this.update(new LambdaUpdateWrapper<Purchase>().eq(Purchase::getId, purchaseId).set(Purchase::getUpdateTime, LocalDateTime.now()).eq(Purchase::getStatus, PurchaseStatus.CREATED.getCode()));

        List<PurchaseDetail> purchaseDetails = dto.getPurchaseDetailIds().stream().map(id -> {
            PurchaseDetail purchaseDetail = new PurchaseDetail();
            purchaseDetail.setPurchaseId(purchaseId);
            purchaseDetail.setId(id);
            purchaseDetail.setStatus(PurchaseDetailStatus.CREATED.getCode());
            return purchaseDetail;
        }).toList();

        return purchaseDetailService.updateBatchById(purchaseDetails);
    }
}
