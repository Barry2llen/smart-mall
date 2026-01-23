package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.Category;
import edu.nchu.mall.models.vo.CategoryVO;
import edu.nchu.mall.services.product.dao.CategoryMapper;
import edu.nchu.mall.services.product.service.CategoryService;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "category")
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    @Caching(
            evict = {@CacheEvict(key = "#entity.catId"), @CacheEvict(key = "'tree'")}
    )
    public boolean updateById(Category entity) {
        Integer showStatus = entity.getShowStatus();
        entity.setShowStatus(null);

        UpdateWrapper<Category> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("cat_id", entity.getCatId());

        if (showStatus != null) {
            updateWrapper.set("show_status", showStatus);
        }

        return baseMapper.update(entity, updateWrapper) > 0;
    }

    @Override
    @Cacheable(key = "#id")
    public Category getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Cacheable(key = "'tree'")
    public List<CategoryVO> listWithTree() {

        List<Category> list = baseMapper.selectList(null);

        Map<Long, CategoryVO> map = list.stream().map(entity -> {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(entity, vo);
            vo.setChildren(new ArrayList<>());
            return vo;
        }).collect(Collectors.toMap(CategoryVO::getCatId, vo -> vo));

        map.values().forEach(entity -> {
            if(entity.getParentCid() != 0){
                CategoryVO parent = map.get(entity.getParentCid());
                if(parent != null){
                    parent.getChildren().add(entity);
                }
            }
        });

        return map.values().stream().peek(entity -> {
            entity.getChildren().sort(Comparator.comparing(CategoryVO::getSort, Comparator.nullsLast(Integer::compareTo)));
        })
                .filter(entity -> entity.getParentCid() == 0)
                .sorted(Comparator.comparingInt(CategoryVO::getSort))
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(key = "'tree'")
    public boolean removeByIds(List<Long> list) {
        boolean res = super.removeByIds(list);
        if(res){
            list.forEach(id -> stringRedisTemplate.delete("category::" + id));
        }
        return res;
    }

    @Override
    @CacheEvict(key = "'tree'")
    public boolean save(Category entity) {
        return false;
    }
}
