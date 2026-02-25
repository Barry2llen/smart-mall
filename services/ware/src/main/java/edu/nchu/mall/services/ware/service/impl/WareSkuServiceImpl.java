package edu.nchu.mall.services.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.components.utils.KeyUtils;
import edu.nchu.mall.models.dto.WareSkuLock;
import edu.nchu.mall.models.entity.MemberReceiveAddress;
import edu.nchu.mall.models.entity.WareSku;
import edu.nchu.mall.models.vo.SkuStockVO;
import edu.nchu.mall.services.ware.dao.WareInfoMapper;
import edu.nchu.mall.services.ware.dao.WareSkuMapper;
import edu.nchu.mall.services.ware.service.WareSkuService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "wareSku")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuMapper, WareSku> implements WareSkuService {

    @Autowired
    WareInfoMapper wareInfoMapper;

    private Long determineWare(MemberReceiveAddress address) {
        // TODO ...
        return wareInfoMapper.selectOne(null).getId();
    }

    private boolean lockStock(Long wareId, Long skuId, Integer num) {
        return baseMapper.lockStock(wareId, skuId, num) > 0;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#entity.id"),
            @CacheEvict(cacheNames = "wareSku:list", allEntries = true)
    })
    public boolean updateById(WareSku entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public WareSku getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(cacheNames = "wareSku:list", allEntries = true)
    public boolean save(WareSku entity) {
        return super.save(entity);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(cacheNames = "wareSku:list", allEntries = true)
    })
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<WareSku> list(Integer pageNum, Integer pageSize, String wareKey, String skuKey) {
        if (KeyUtils.isEmpty(wareKey) || KeyUtils.isEmpty(skuKey)) {
            var self = ((WareSkuServiceImpl) AopContext.currentProxy());
            return self.list(pageNum, pageSize);
        }

        var wareId = KeyUtils.parseKey2Long(wareKey);
        var skuId = KeyUtils.parseKey2Long(skuKey);
        IPage<WareSku> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WareSku> qw = Wrappers.lambdaQuery();
        qw.like(WareSku::getSkuName, skuKey)
                .eq(wareId.isPresent(), WareSku::getWareId, wareId.get())
                .eq(skuId.isPresent(), WareSku::getSkuId, skuId.get());
        return super.list(page, qw);
    }

    @Override
    public List<SkuStockVO> getStocksBySkuIds(List<Long> skuIds) {
        return baseMapper.getStockBySkuIds(skuIds);
    }

    @Override
    public SkuStockVO getStockBySkuId(Long skuId) {
        return baseMapper.getStockBySkuId(skuId);
    }

    @Override
    @Transactional
    public boolean lockStock(WareSkuLock lock) {

        Long wareId = determineWare(lock.getAddress());

        for (var item : lock.getItems()) {
            boolean res = lockStock(item.getSkuId(), wareId, item.getNum());
            if (!res) {
                return false;
            }
        }

        return true;
    }

    @Cacheable(cacheNames = "wareSku:list", key = "#pageNum + ':' + #pageSize")
    public List<WareSku> list(Integer pageNum, Integer pageSize) {
        return super.list(new Page<>(pageNum, pageSize));
    }
}
