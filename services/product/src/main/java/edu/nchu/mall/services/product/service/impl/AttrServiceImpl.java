package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.dto.AttrDTO;
import edu.nchu.mall.models.entity.Attr;
import edu.nchu.mall.models.entity.AttrAttrgroupRelation;
import edu.nchu.mall.models.entity.AttrGroup;
import edu.nchu.mall.models.entity.Category;
import edu.nchu.mall.models.vo.AttrVO;
import edu.nchu.mall.services.product.dao.AttrAttrgroupRelationMapper;
import edu.nchu.mall.services.product.dao.AttrGroupMapper;
import edu.nchu.mall.services.product.dao.AttrMapper;
import edu.nchu.mall.services.product.dao.CategoryMapper;
import edu.nchu.mall.services.product.service.AttrService;
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
import java.util.List;

@Service
@CacheConfig(cacheNames = "attr")
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {

    @Autowired
    AttrAttrgroupRelationMapper relationMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    AttrGroupMapper attrGroupMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 根据属性分组ID删除缓存
     */
    @CacheEvict(value = "attr:group", key = "#groupId", condition = "#groupId != null")
    public void deleteCacheByGroupId(Long groupId) {
    }

    /**
     * 清除无关联属性缓存
     */
    @CacheEvict(value = "attr:no-relation", allEntries = true)
    public void evictNoRelationCache() {
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.attrId"),
            @CacheEvict(value = "attr:page", allEntries = true),
    })
    @Transactional
    public boolean updateById(AttrDTO dto) {
        var self = (AttrServiceImpl) AopContext.currentProxy();
        Long catlogId = dto.getCatelogId();
        Long attrGroupId = dto.getAttrGroupId();

        boolean setCatlogIdNull = catlogId != null && catlogId.equals(0L);
        boolean setAttrGroupIdNull = attrGroupId != null && attrGroupId.equals(0L);

        if (setCatlogIdNull || setAttrGroupIdNull) {
            self.deleteAttrRelation(dto.getAttrId());
        }

        Attr attr = new Attr();
        BeanUtils.copyProperties(dto, attr);
        AttrAttrgroupRelation relation = new AttrAttrgroupRelation();
        BeanUtils.copyProperties(dto, relation);

        LambdaUpdateWrapper<Attr> uw1 = Wrappers.lambdaUpdate();
        uw1.eq(Attr::getAttrId, dto.getAttrId())
            .set(setCatlogIdNull, Attr::getCatelogId, null);

        LambdaUpdateWrapper<AttrAttrgroupRelation> uw2 = Wrappers.lambdaUpdate();
        uw2.eq(AttrAttrgroupRelation::getAttrId, dto.getAttrId())
                .set(setAttrGroupIdNull, AttrAttrgroupRelation::getAttrGroupId, null);

        relationMapper.update(relation, uw2);
        return super.update(attr, uw1);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "attr:page", allEntries = true),
            @CacheEvict(value = "attr:group", key = "#dto.attrGroupId", condition = "#dto.attrGroupId != null")
    })
    public boolean save(AttrDTO dto) {
        Attr attr = new Attr();
        BeanUtils.copyProperties(dto, attr);
        boolean res = super.save(attr);

        if (!res || dto.getAttrGroupId() == null) return res;

        AttrAttrgroupRelation relation = new AttrAttrgroupRelation();
        relation.setAttrId(attr.getAttrId());
        relation.setAttrGroupId(dto.getAttrGroupId());
        relation.setAttrSort(dto.getAttrSort());
        return relationMapper.insert(relation) > 0;
    }

    @Override
    @Cacheable(key = "#id")
    public AttrVO getVoById(Serializable id) {
        Attr attr = super.getById(id);
        return attr != null ? this.convert2VO(attr) : null;
    }

    @Override
    @Cacheable(value = "attr:group", key = "#groupId")
    public List<AttrVO> getVosByGroupId(Serializable groupId) {
        LambdaQueryWrapper<AttrAttrgroupRelation> qw = Wrappers.lambdaQuery();
        qw.eq(AttrAttrgroupRelation::getAttrGroupId, groupId);
        List<Long> ids = relationMapper.selectList(qw)
                .stream().map(AttrAttrgroupRelation::getAttrId).toList();
        if (ids.isEmpty()) return List.of();
        return this.convert2VOList(super.listByIds(ids));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(value = "attr:page", allEntries = true),
    })
    public boolean removeById(Serializable id) {
        Attr attr = super.getById(id);
        if (attr == null) return false;

        Long groupId = this.convert2VO(attr).getAttrGroupId();
        if (groupId != null) {
            // 删除属性分组下已关联属性的缓存
            redisTemplate.delete("product::attr:group::" + groupId);
            ((AttrServiceImpl)AopContext.currentProxy()).evictNoRelationCache();
        }

        LambdaQueryWrapper<AttrAttrgroupRelation> qw = Wrappers.lambdaQuery();
        qw.eq(AttrAttrgroupRelation::getAttrId, id);
        return relationMapper.delete(qw) > 0 && super.removeById(id);
    }

    @Override
    public List<AttrVO> list(Integer pageNum, Integer pageSize, String attrName, Integer catelogId) {
        if (attrName == null) {
            AttrServiceImpl self = (AttrServiceImpl) AopContext.currentProxy();
            if (catelogId != null) {
                return self.list(pageNum, pageSize, catelogId);
            } else {
                return self.list(pageNum, pageSize);
            }
        }

        IPage<Attr> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Attr> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(Attr::getAttrId);
        if (!attrName.isEmpty()) {
            qw.like(Attr::getAttrName, attrName);
            if (attrName.matches("\\d+")) {
                qw.or().eq(Attr::getAttrId, Long.parseLong(attrName));
            }
        }
        if (catelogId != null) {
            qw.eq(Attr::getCatelogId, catelogId);
        }
        return this.convert2VOList(super.list(page, qw));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(value = "attr:page", allEntries = true),
            @CacheEvict(value = "attr:no-relation", allEntries = true)
    })
    public boolean deleteAttrRelation(long id) {
        LambdaQueryWrapper<AttrAttrgroupRelation> qw = Wrappers.lambdaQuery();
        qw.eq(AttrAttrgroupRelation::getAttrId, id);
        AttrAttrgroupRelation relation = relationMapper.selectOne(qw);

        if (relation == null || relation.getAttrGroupId() == null) return false;

        if (relationMapper.delete(qw) <= 0) return false;

        // 删除属性分组下已关联属性的缓存
        var self = (AttrServiceImpl) AopContext.currentProxy();
        self.deleteCacheByGroupId(relation.getAttrGroupId());
        return true;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#attrId"),
            @CacheEvict(value = "attr:page", allEntries = true),
            @CacheEvict(value = "attr:no-relation", allEntries = true)
    })
    public boolean newAttrRelation(Long attrId, Long attrGroupId) {
        var relation = new AttrAttrgroupRelation();
        relation.setAttrId(attrId);
        relation.setAttrGroupId(attrGroupId);

        // 更新catalogId
        AttrGroup attrGroup = attrGroupMapper.selectById(attrGroupId);
        if (attrGroup == null) return false;

        LambdaUpdateWrapper<Attr> uw = Wrappers.lambdaUpdate();
        uw.eq(Attr::getAttrId, attrId)
          .set(Attr::getCatelogId, attrGroup.getCatelogId());

        boolean res = baseMapper.update(uw) > 0 && relationMapper.insert(relation) > 0;

        // 删除属性分组下已关联属性的缓存
        if (res){
            var self = (AttrServiceImpl) AopContext.currentProxy();
            self.deleteCacheByGroupId(attrGroupId);
        }

        return res;
    }

    @Override
    @Cacheable(value = "attr:no-relation", key = "#pageNum + ':' + #pageSize + (#catlogId == null ? '' : (':' + #catlogId.toString()))")
    public List<AttrVO> listNonRelationAttrs(Integer pageNum, Integer pageSize, Long catlogId) {
        IPage<Attr> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Attr> qw = Wrappers.lambdaQuery();
        qw.and(i -> {
            i.isNull(Attr::getCatelogId); // 总是查询为 NULL 的记录
            if (catlogId != null) {
                i.or().eq(Attr::getCatelogId, catlogId); // 如果有 ID，再 OR 一下
            }
        });
        return this.convert2VOList(super.list(page, qw)).stream().filter(vo -> vo.getAttrGroupId() == null).toList();
    }

    @Cacheable(value = "attr:page", key = "#pageNum + ':' + #pageSize")
    public List<AttrVO> list(Integer pageNum, Integer pageSize) {
        IPage<Attr> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Attr> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(Attr::getAttrId);
        return this.convert2VOList(super.list(page, qw));
    }

    @Cacheable(value = "attr:page", key = "#pageNum + ':' + #pageSize + ':' + #catelogId")
    public List<AttrVO> list(Integer pageNum, Integer pageSize, Integer catelogId) {
        IPage<Attr> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Attr> qw = new LambdaQueryWrapper<>();
        qw.eq(Attr::getCatelogId, catelogId);
        qw.orderByAsc(Attr::getAttrId);
        return this.convert2VOList(super.list(page, qw));
    }

    private AttrVO convert2VO(Attr attr) {
        AttrVO vo = new AttrVO();
        BeanUtils.copyProperties(attr, vo);
        LambdaQueryWrapper<AttrAttrgroupRelation> qw = Wrappers.lambdaQuery();
        qw.eq(AttrAttrgroupRelation::getAttrId, attr.getAttrId());
        AttrAttrgroupRelation relation = relationMapper.selectOne(qw);
        if (relation != null) {
            Long groupId = relation.getAttrGroupId();
            vo.setAttrGroupId(groupId);
            AttrGroup group = attrGroupMapper.selectById(groupId);
            if (group != null) vo.setAttrGroupName(group.getAttrGroupName());
        }
        if (attr.getCatelogId() != null) {
            Category category = categoryMapper.selectById(attr.getCatelogId());
            if (category != null) vo.setCatelogName(category.getName());
        }
        return vo;
    }


    private List<AttrVO> convert2VOList(List<Attr> attrs) {
        return attrs.stream().map(this::convert2VO).toList();
    }
}
