package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.Category;
import edu.nchu.mall.models.entity.CategoryBrandRelation;
import edu.nchu.mall.models.vo.CategoryVO;
import edu.nchu.mall.services.product.dao.CategoryBrandRelationMapper;
import edu.nchu.mall.services.product.dao.CategoryMapper;
import edu.nchu.mall.services.product.service.CategoryBrandRelationService;
import edu.nchu.mall.services.product.service.CategoryService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    CategoryBrandRelationMapper categoryBrandRelationMapper;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    RedissonClient redissonClient;

    @Override
    @Caching(
            evict = {@CacheEvict(key = "#entity.catId"), @CacheEvict(key = "'tree'")}
    )
    public boolean updateById(Category entity) {
        if(entity.getName() != null){
            //同步更新关联表中的分类名称
            LambdaQueryWrapper<CategoryBrandRelation> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(CategoryBrandRelation::getCatelogId, entity.getCatId());
            List<Long> brandIds = categoryBrandRelationMapper.selectList(queryWrapper)
                    .stream().map(CategoryBrandRelation::getBrandId).toList();

            LambdaUpdateWrapper<CategoryBrandRelation> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.eq(CategoryBrandRelation::getCatelogId, entity.getCatId())
                    .set(CategoryBrandRelation::getCatelogName, entity.getName());
            categoryBrandRelationMapper.update(updateWrapper);

            if (!brandIds.isEmpty()) {
                brandIds.forEach(brandId -> categoryBrandRelationService.removeCacheByBrandId(brandId));
            }
        }
        RLock test = redissonClient.getLock("test");
        test.lock();
        return super.updateById(entity);
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
    @Cacheable(key = "'tree'", sync = true)
    public List<CategoryVO> listWithTree() {

        List<Integer> list1 = "123".chars().mapToObj(Character::getNumericValue).toList();
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
            list.forEach(id -> stringRedisTemplate.delete("product::category::" + id));
        }
        return res;
    }

    @Override
    @CacheEvict(key = "'tree'")
    public boolean save(Category entity) {
        return super.save(entity);
    }

    @Override
    public List<Category> seqByIds(List<Long> ids) {
        Map<Long, Category> collect = super.listByIds(ids).stream().collect(Collectors.toMap(Category::getCatId, category -> category));
        return ids.stream().map(collect::get).collect(Collectors.toList());
    }
}
