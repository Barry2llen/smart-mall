package edu.nchu.mall.services.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.WareOrderTask;
import edu.nchu.mall.services.ware.dao.WareOrderTaskMapper;
import edu.nchu.mall.services.ware.service.WareOrderTaskService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
public class WareOrderTaskServiceImpl extends ServiceImpl<WareOrderTaskMapper, WareOrderTask> implements WareOrderTaskService {
}
