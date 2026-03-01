package edu.nchu.mall.services.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.utils.KeyUtils;
import edu.nchu.mall.models.dto.WareSkuLock;
import edu.nchu.mall.models.dto.mq.StockLocked;
import edu.nchu.mall.models.entity.*;
import edu.nchu.mall.models.enums.WareOrderLockStatus;
import edu.nchu.mall.models.vo.SkuStockVO;
import edu.nchu.mall.services.ware.dao.WareInfoMapper;
import edu.nchu.mall.services.ware.dao.WareSkuMapper;
import edu.nchu.mall.services.ware.service.WareOrderTaskDetailService;
import edu.nchu.mall.services.ware.service.WareOrderTaskService;
import edu.nchu.mall.services.ware.service.WareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RabbitListener(queues = "stock.release.queue")
@CacheConfig(cacheNames = "wareSku")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuMapper, WareSku> implements WareSkuService {

    @Autowired
    WareInfoMapper wareInfoMapper;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    RabbitTemplate rabbitTemplate;

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

        var address = lock.getAddress();

        Long wareId = determineWare(address);

        // 保存库存锁定任务
        WareOrderTask task = new WareOrderTask();
        task.setOrderSn(lock.getOrderSn());
        task.setCreateTime(LocalDateTime.now());
        task.setConsignee(address.getName());
        task.setConsigneeTel(address.getPhone());
        task.setDeliveryAddress(address.getComposedAddress());
        task.setWareId(wareId);
        wareOrderTaskService.save(task);

        List<Long> detailIds = new LinkedList<>();
        for (var item : lock.getItems()) {
            WareOrderTaskDetail detail = new WareOrderTaskDetail(null, item.getSkuId(), "null", item.getNum(), task.getId(), wareId, WareOrderLockStatus.LOCKED);

            boolean res = lockStock(wareId, item.getSkuId(), item.getNum());
            if (!res) throw new CustomException("锁定库存失败");

            res = wareOrderTaskDetailService.save(detail);
            if (!res) throw new CustomException("锁定库存失败");

            detailIds.add(detail.getId());
        }

        StockLocked stockLocked = new StockLocked(task.getId(), detailIds);
        rabbitTemplate.convertAndSend("stock.event.exchange", "stock.delay", stockLocked);

        return true;
    }

    @Cacheable(cacheNames = "wareSku:list", key = "#pageNum + ':' + #pageSize")
    public List<WareSku> list(Integer pageNum, Integer pageSize) {
        return super.list(new Page<>(pageNum, pageSize));
    }
}
