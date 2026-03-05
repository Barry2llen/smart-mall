package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SeckillSession;
import edu.nchu.mall.models.entity.SeckillSkuRelation;
import edu.nchu.mall.models.vo.SeckillSessionVO;
import edu.nchu.mall.models.vo.SeckillSkuRelationVO;
import edu.nchu.mall.services.coupon.dao.SeckillSessionMapper;
import edu.nchu.mall.services.coupon.service.SeckillSessionService;
import edu.nchu.mall.services.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Service
@CacheConfig(cacheNames = "seckillSession")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionMapper, SeckillSession> implements SeckillSessionService {

    @Autowired
    SeckillSkuRelationService relationService;

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SeckillSession entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SeckillSession getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<SeckillSession> list(Integer pageNum, Integer pageSize, String name) {
        IPage<SeckillSession> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SeckillSession> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StringUtils.hasText(name), SeckillSession::getName, name)
                .orderByDesc(SeckillSession::getId);
        return super.page(page, queryWrapper).getRecords();
    }

    @Override
    public List<SeckillSessionVO> getLatest3DaysSessions() {
        LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime threeDaysLater = now.plusDays(3).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        LambdaQueryWrapper<SeckillSession> qw = Wrappers.lambdaQuery();
        qw.between(SeckillSession::getStartTime, now, threeDaysLater)
                .eq(SeckillSession::getStatus, 1)
                .orderByAsc(SeckillSession::getStartTime);

        return super.list(qw).stream().map(session -> {
            SeckillSessionVO vo = new SeckillSessionVO();
            BeanUtils.copyProperties(session, vo);

            LambdaQueryWrapper<SeckillSkuRelation> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(SeckillSkuRelation::getPromotionSessionId, session.getId());
            List<SeckillSkuRelationVO> relationVos = relationService.list(lambdaQueryWrapper).stream().map(relation -> {
                SeckillSkuRelationVO relationVo = new SeckillSkuRelationVO();
                BeanUtils.copyProperties(relation, relationVo);
                return relationVo;
            }).toList();

            vo.setRelations(relationVos);

            return vo;
        }).toList();

    }
}
