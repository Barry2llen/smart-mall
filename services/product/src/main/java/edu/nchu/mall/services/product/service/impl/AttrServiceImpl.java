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
@Transactional
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {

    @Autowired
    AttrAttrgroupRelationMapper relationMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    AttrGroupMapper attrGroupMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.attrId"),
            @CacheEvict(value = "attr:page", allEntries = true),
            @CacheEvict(value = "attr:group", key = "#dto.attrGroupId")
    })
    public boolean updateById(AttrDTO dto) {
        Attr attr = new Attr();
        BeanUtils.copyProperties(dto, attr);
        LambdaUpdateWrapper<AttrAttrgroupRelation> uw = Wrappers.lambdaUpdate();
        uw.eq(AttrAttrgroupRelation::getAttrId, dto.getAttrId())
                .set(AttrAttrgroupRelation::getAttrGroupId,  dto.getAttrGroupId())
                .set(AttrAttrgroupRelation::getAttrSort, dto.getAttrSort());
        return relationMapper.update(uw) > 0 && super.updateById(attr);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "attr:page", allEntries = true),
            @CacheEvict(value = "attr:group", key = "#dto.attrGroupId")
    })
    public boolean save(AttrDTO dto) {
        Attr attr = new Attr();
        BeanUtils.copyProperties(dto, attr);
        boolean res = super.save(attr);

        if (!res) return false;

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
        return this.convert2VO(attr);
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
            @CacheEvict(value = "attr:page", allEntries = true)
    })
    public boolean removeById(Serializable id) {
        Attr attr = super.getById(id);
        if (attr == null) return false;

        Long groupId = this.convert2VO(attr).getAttrGroupId();
        if (groupId != null) {
            redisTemplate.delete("product::attr:group::" + groupId);
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

        IPage<Attr> page = new Page<>((long) (pageNum - 1) * pageSize, pageSize);
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

    @Cacheable(value = "attr:page", key = "#pageNum + ':' + #pageSize")
    public List<AttrVO> list(Integer pageNum, Integer pageSize) {
        IPage<Attr> page = new Page<>((long) (pageNum - 1) * pageSize, pageSize);
        LambdaQueryWrapper<Attr> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(Attr::getAttrId);
        return (this.convert2VOList(super.list(page, qw)));
    }

    @Cacheable(value = "attr:page", key = "#pageNum + ':' + #pageSize + ':' + #catelogId")
    public List<AttrVO> list(Integer pageNum, Integer pageSize, Integer catelogId) {
        IPage<Attr> page = new Page<>((long) (pageNum - 1) * pageSize, pageSize);
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
        Long groupId = relationMapper.selectOne(qw).getAttrGroupId();
        vo.setAttrGroupId(groupId);
        AttrGroup group = attrGroupMapper.selectById(groupId);
        if (group != null) vo.setAttrGroupName(group.getAttrGroupName());
        Category category = categoryMapper.selectById(attr.getCatelogId());
        if (category != null) vo.setCatelogName(category.getName());
        return vo;
    }

    private List<AttrVO> convert2VOList(List<Attr> attrs) {
        return attrs.stream().map(this::convert2VO).toList();
    }
}
