package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SpuImages;
import edu.nchu.mall.services.product.dao.SpuImagesMapper;
import edu.nchu.mall.services.product.service.SpuImagesService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@CacheConfig(cacheNames = "spuImages")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesMapper, SpuImages> implements SpuImagesService {
    @Override
    public Map<Long, String> getSpuDefaultImagesBatch(Collection<Long> spuIds) {

        if (spuIds.isEmpty()) {
            return Map.of();
        }

        LambdaQueryWrapper<SpuImages> qw = Wrappers.lambdaQuery();
        qw.select(SpuImages::getSpuId, SpuImages::getImgUrl);
        qw.in(SpuImages::getSpuId, spuIds);
        qw.eq(SpuImages::getDefaultImg, 1);

        List<SpuImages> list = this.list(qw);
        return list.stream().collect(Collectors.toMap(SpuImages::getSpuId, SpuImages::getImgUrl));
    }
}
