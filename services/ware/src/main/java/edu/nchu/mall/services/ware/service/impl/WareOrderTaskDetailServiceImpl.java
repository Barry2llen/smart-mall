package edu.nchu.mall.services.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.WareOrderTaskDetail;
import edu.nchu.mall.services.ware.dao.WareOrderTaskDetailMapper;
import edu.nchu.mall.services.ware.service.WareOrderTaskDetailService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = "wareOrderTaskDetail")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailMapper, WareOrderTaskDetail> implements WareOrderTaskDetailService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(WareOrderTaskDetail entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public WareOrderTaskDetail getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
