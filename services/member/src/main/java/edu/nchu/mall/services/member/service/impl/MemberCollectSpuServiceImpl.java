package edu.nchu.mall.services.member.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.dto.MemberCollectSpuDTO;
import edu.nchu.mall.models.entity.MemberCollectSpu;
import edu.nchu.mall.models.vo.MemberCollectSpuVO;
import edu.nchu.mall.services.member.dao.MemberCollectSpuMapper;
import edu.nchu.mall.services.member.service.MemberCollectSpuService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@CacheConfig(cacheNames = "memberCollectSpu")
@Transactional
public class MemberCollectSpuServiceImpl extends ServiceImpl<MemberCollectSpuMapper, MemberCollectSpu> implements MemberCollectSpuService {

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#dto.id"),
            @CacheEvict(key = "'list'")
    })
    public boolean updateById(MemberCollectSpuDTO dto) {
        MemberCollectSpu entity = new MemberCollectSpu();
        BeanUtils.copyProperties(dto, entity);
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public MemberCollectSpuVO getMemberCollectSpuById(Serializable id) {
        MemberCollectSpu entity = super.getById(id);
        if (entity == null) return null;
        MemberCollectSpuVO vo = new MemberCollectSpuVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'list'")
    })
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "'list'")
    })
    public boolean save(MemberCollectSpuDTO dto) {
        MemberCollectSpu entity = new MemberCollectSpu();
        BeanUtils.copyProperties(dto, entity);
        return super.save(entity);
    }

    @Override
    @Cacheable(key = "'list'")
    public List<MemberCollectSpuVO> getMemberCollectSpus(Integer pageNum, Integer pageSize) {
        IPage<MemberCollectSpu> page = new Page<>(pageNum, pageSize);
        return super.list(page).stream().map(entity -> {
            MemberCollectSpuVO vo = new MemberCollectSpuVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }
}
