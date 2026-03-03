package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.entity.SeckillSkuRelation;
import edu.nchu.mall.services.coupon.dao.SeckillSkuRelationMapper;
import edu.nchu.mall.services.coupon.service.SeckillSkuRelationService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "seckillSkuRelation")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationMapper, SeckillSkuRelation> implements SeckillSkuRelationService {

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SeckillSkuRelation entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SeckillSkuRelation getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public List<SeckillSkuRelation> list(Integer pageNum, Integer pageSize, String key, Long promotionSessionId) {
        IPage<SeckillSkuRelation> page = new Page<>(pageNum, pageSize);
        String normalizedKey = StringUtils.hasText(key) ? key.trim() : null;
        List<Map<String, Long>> numericPrefixRanges = buildNumericPrefixRanges(normalizedKey);
        return baseMapper.selectPageByCondition((Page<SeckillSkuRelation>) page, normalizedKey, numericPrefixRanges, promotionSessionId).getRecords();
    }

    private List<Map<String, Long>> buildNumericPrefixRanges(String key) {
        if (!StringUtils.hasText(key) || !key.matches("^[1-9]\\d{0,18}$")) {
            return List.of();
        }

        int extraDigitsLimit = 19 - key.length();
        BigInteger prefix = new BigInteger(key);
        BigInteger longMax = BigInteger.valueOf(Long.MAX_VALUE);
        List<Map<String, Long>> ranges = new ArrayList<>(extraDigitsLimit + 1);
        for (int extraDigits = 0; extraDigits <= extraDigitsLimit; extraDigits++) {
            BigInteger factor = BigInteger.TEN.pow(extraDigits);
            BigInteger start = prefix.multiply(factor);
            if (start.compareTo(longMax) > 0) {
                break;
            }

            BigInteger end = prefix.add(BigInteger.ONE).multiply(factor).subtract(BigInteger.ONE);
            if (end.compareTo(longMax) > 0) {
                end = longMax;
            }

            Map<String, Long> range = new HashMap<>(2);
            range.put("start", start.longValueExact());
            range.put("end", end.longValueExact());
            ranges.add(range);
        }
        return ranges;
    }
}
