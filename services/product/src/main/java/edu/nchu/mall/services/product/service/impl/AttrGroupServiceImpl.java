package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.Attr;
import edu.nchu.mall.models.entity.AttrAttrgroupRelation;
import edu.nchu.mall.models.entity.AttrGroup;
import edu.nchu.mall.models.vo.AttrGroupVO;
import edu.nchu.mall.models.vo.AttrGroupWithAttrVO;
import edu.nchu.mall.services.product.dao.AttrAttrgroupRelationMapper;
import edu.nchu.mall.services.product.dao.AttrGroupMapper;
import edu.nchu.mall.services.product.dao.AttrMapper;
import edu.nchu.mall.services.product.service.AttrGroupService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@CacheConfig(cacheNames = "attrGroup")
@Transactional
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {

    @Autowired
    AttrAttrgroupRelationMapper relationMapper;

    @Autowired
    AttrMapper attrMapper;

    @CacheEvict(cacheNames = "attrGroup:byCatelog", key = "#catelogId")
    public void deleteAttrGroupCacheByCatelogId(Long catelogId) {
    }

    @CacheEvict(key = "#id")
    public void deleteAttrGroupCacheById(Long id) {
    }

    @CacheEvict(cacheNames = "attrGroup:attrsInGroupByCatelog", key = "#catelogId")
    public void deleteAttrsInGroupCacheByCatelog(Long catelogId) {
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#entity.attrGroupId"),
            @CacheEvict(value = "attrGroup:page", allEntries = true),
    })
    public boolean updateById(AttrGroup entity) {
        var self = (AttrGroupServiceImpl) AopContext.currentProxy();
        self.deleteAttrGroupCacheByCatelogId(entity.getCatelogId());
        if (entity.getAttrGroupId() != null) {
            self.deleteAttrGroupCacheById(entity.getAttrGroupId());
        }
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
        var self = (AttrGroupServiceImpl) AopContext.currentProxy();
        AttrGroup attrGroup = self.getById(id);
        if (attrGroup != null) {
            self.deleteAttrGroupCacheByCatelogId(attrGroup.getCatelogId());
            if (attrGroup.getAttrGroupId() != null) {
                self.deleteAttrGroupCacheById(attrGroup.getAttrGroupId());
            }
        }
        return super.removeById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "attrGroup:page", allEntries = true)
    })
    public boolean removeByIds(Collection<?> ids) {
        var self = (AttrGroupServiceImpl) AopContext.currentProxy();
        List<AttrGroup> attrGroups = super.listByIds((Collection<? extends Serializable>) ids);
        attrGroups.forEach(entity -> {
            self.deleteAttrGroupCacheById(entity.getAttrGroupId());
            self.deleteAttrGroupCacheByCatelogId(entity.getCatelogId());
            if (entity.getAttrGroupId() != null) {
                self.deleteAttrGroupCacheById(entity.getAttrGroupId());
            }
        });
        return super.removeByIds(ids);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "attrGroup:page", allEntries = true),
    })
    public boolean save(AttrGroup entity) {
        var self = (AttrGroupServiceImpl) AopContext.currentProxy();
        self.deleteAttrGroupCacheByCatelogId(entity.getCatelogId());
        if (entity.getAttrGroupId() != null) {
            self.deleteAttrGroupCacheById(entity.getAttrGroupId());
        }
        return super.save(entity);
    }

    @Cacheable(value = "attrGroup:page", key = "#pageNum + ':' + #pageSize")
    public List<AttrGroup> list(Integer pageNum, Integer pageSize) {
        IPage<AttrGroup> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AttrGroup> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(AttrGroup::getSort);
        return super.list(page, qw);
    }

    @Cacheable(value = "attrGroup:page", key = "#pageNum + ':' + #pageSize + ':' + #catelogId")
    public List<AttrGroup> list(Integer pageNum, Integer pageSize, Integer catelogId) {
        IPage<AttrGroup> page = new Page<>(pageNum, pageSize);
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

        IPage<AttrGroup> page = new Page<>(pageNum, pageSize);
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

    @Override
    @Cacheable(cacheNames = "attrGroup:attrsInGroupByCatelog", key = "#catelogId")
    public List<AttrGroupWithAttrVO> listAttrInGroupByCatelogId(long catelogId) {
        LambdaQueryWrapper<AttrGroup> qw = Wrappers.lambdaQuery();
        qw.eq(AttrGroup::getCatelogId, catelogId);
        return super.list(qw).stream().map(entity -> {
            AttrGroupWithAttrVO vo = new AttrGroupWithAttrVO();
            vo.setAttrs(List.of());
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).map(vo -> {
            LambdaQueryWrapper<AttrAttrgroupRelation> relationQw = Wrappers.lambdaQuery();
            relationQw.eq(AttrAttrgroupRelation::getAttrGroupId, vo.getAttrGroupId());
            List<Long> attrIds = relationMapper.selectList(relationQw).stream().map(AttrAttrgroupRelation::getAttrId).toList();
            if (!attrIds.isEmpty()) {
                LambdaQueryWrapper <Attr> attrQw = Wrappers.lambdaQuery();
                attrQw.in(Attr::getAttrId, attrIds);
                vo.setAttrs(attrMapper.selectList(attrQw).stream().map(attr -> {
                    AttrGroupWithAttrVO.AttrInfo info = new AttrGroupWithAttrVO.AttrInfo();
                    BeanUtils.copyProperties(attr, info);
                    return info;
                }).toList());
            }
            return vo;
        }).toList();
    }
}
