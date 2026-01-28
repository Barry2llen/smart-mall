package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.AttrGroup;
import edu.nchu.mall.models.vo.AttrGroupVO;
import edu.nchu.mall.services.product.dao.AttrGroupMapper;
import edu.nchu.mall.services.product.service.AttrGroupService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Service
@CacheConfig(cacheNames = "attrGroup")
@Transactional
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#entity.attrGroupId"),
            @CacheEvict(value = "attrGroup:page", allEntries = true),
    })
    public boolean updateById(AttrGroup entity) {
        redisTemplate.delete("attrGroup:byCatelog::" + entity.getCatelogId());
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public AttrGroup getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(value = "attrGroup:page", allEntries = true)
    })
    public boolean removeById(Serializable id) {
        AttrGroupServiceImpl self = (AttrGroupServiceImpl) AopContext.currentProxy();
        AttrGroup attrGroup = self.getById(id);
        if (attrGroup != null) {
            redisTemplate.delete("attrGroup:byCatelog::" + attrGroup.getCatelogId());
        }
        return super.removeById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "attrGroup:page", allEntries = true)
    })
    public boolean removeByIds(Collection<?> ids) {
        List<AttrGroup> attrGroups = super.listByIds((Collection<? extends Serializable>) ids);
        attrGroups .forEach(entity -> {
            redisTemplate.delete("product::attrGroup::" + entity.getAttrGroupId());
            redisTemplate.delete("attrGroup:byCatelog::" + entity.getCatelogId());
        });
        return super.removeByIds(ids);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "attrGroup:page", allEntries = true)
    })
    public boolean save(AttrGroup entity) {
        redisTemplate.delete("attrGroup:byCatelog::" + entity.getCatelogId());
        return super.save(entity);
    }

    @Cacheable(value = "attrGroup:page", key = "#pageNum + ':' + #pageSize")
    public List<AttrGroup> list(Integer pageNum, Integer pageSize) {
        IPage<AttrGroup> page = new Page<>((long) (pageNum - 1) * pageSize, pageSize);
        LambdaQueryWrapper<AttrGroup> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(AttrGroup::getSort);
        return super.list(page, qw);
    }

    @Cacheable(value = "attrGroup:page", key = "#pageNum + ':' + #pageSize + ':' + #catelogId")
    public List<AttrGroup> list(Integer pageNum, Integer pageSize, Integer catelogId) {
        IPage<AttrGroup> page = new Page<>((long) (pageNum - 1) * pageSize, pageSize);
        LambdaQueryWrapper<AttrGroup> qw = new LambdaQueryWrapper<>();
        qw.eq(AttrGroup::getCatelogId, catelogId);
        qw.orderByAsc(AttrGroup::getSort);
        return super.list(page, qw);
    }

    @Override
    public List<AttrGroup> list(Integer pageNum, Integer pageSize, String attrGroupName, Integer catelogId){
        if (attrGroupName == null) {
            AttrGroupServiceImpl self = (AttrGroupServiceImpl) AopContext.currentProxy();
            if (catelogId != null) {
                return self.list(pageNum, pageSize, catelogId);
            } else {
                return self.list(pageNum, pageSize);
            }
        }

        IPage<AttrGroup> page = new Page<>((long) (pageNum - 1) * pageSize, pageSize);
        LambdaQueryWrapper<AttrGroup> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(AttrGroup::getSort);
        if (!attrGroupName.isEmpty()) {
            qw.like(AttrGroup::getAttrGroupName, attrGroupName);
            if (attrGroupName.matches("\\d+")) {
                qw.or().eq(AttrGroup::getAttrGroupId, Long.parseLong(attrGroupName));
            }
        }
        if (catelogId != null) {
            qw.eq(AttrGroup::getCatelogId, catelogId);
        }
        return super.list(page, qw);
    }

    @Override
    @Cacheable(cacheNames = "attrGroup:byCatelog", key = "#catelogId")
    public List<AttrGroupVO> getAttrGroupByCatelogId(long catelogId) {
        LambdaQueryWrapper<AttrGroup> qw = Wrappers.lambdaQuery();
        qw.eq(AttrGroup::getCatelogId, catelogId);
        List<AttrGroup> attrGroups = super.list(qw);
        return attrGroups.stream()
                .map(attrGroup -> {
                    AttrGroupVO vo = new AttrGroupVO();
                    BeanUtils.copyProperties(attrGroup, vo);
                    return  vo;
                }).toList();
    }
}
