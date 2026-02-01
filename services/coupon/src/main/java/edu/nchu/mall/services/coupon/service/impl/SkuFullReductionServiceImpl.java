package edu.nchu.mall.services.coupon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.models.dto.SkuReductionDTO;
import edu.nchu.mall.models.dto.SpuSaveDTO;
import edu.nchu.mall.models.entity.MemberPrice;
import edu.nchu.mall.models.entity.SkuFullReduction;
import edu.nchu.mall.models.entity.SkuLadder;
import edu.nchu.mall.services.coupon.dao.MemberPriceMapper;
import edu.nchu.mall.services.coupon.dao.SkuFullReductionMapper;
import edu.nchu.mall.services.coupon.dao.SkuLadderMapper;
import edu.nchu.mall.services.coupon.service.MemberPriceService;
import edu.nchu.mall.services.coupon.service.SkuFullReductionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "skuFullReduction")
@Transactional
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionMapper, SkuFullReduction> implements SkuFullReductionService {

    @Autowired
    SkuLadderMapper skuLadderMapper;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SkuFullReduction entity) {
        return super.updateById(entity);
    }

    @Override
    @Cacheable(key = "#id")
    public SkuFullReduction getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean saveSkuReduction(SkuReductionDTO dto) {
        SkuLadder ladder = new SkuLadder();
        ladder.setSkuId(dto.getSkuId());
        ladder.setFullCount(dto.getFullCount());
        ladder.setDiscount(dto.getDiscount());
        ladder.setAddOther(dto.getCountStatus());

        if (ladder.getFullCount() > 0) {
            boolean res = skuLadderMapper.insert(ladder) > 0;
            if (!res) {
                return false;
            }
        }

        SkuFullReduction reduction = new SkuFullReduction();
        BeanUtils.copyProperties(dto, reduction);
        if (reduction.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
            boolean res = this.save(reduction);
            if (!res) {
                return false;
            }
        }

        List<SpuSaveDTO.Skus.MemberPrice> list = dto.getMemberPrice() != null ? dto.getMemberPrice() : List.of();
        List<MemberPrice> memberPrices = list.stream().map(each -> {
            MemberPrice memberPrice = new MemberPrice();
            memberPrice.setSkuId(dto.getSkuId());
            memberPrice.setMemberLevelId(each.getMemberLevelId());
            memberPrice.setMemberLevelName(each.getMemberLevelName());
            memberPrice.setMemberPrice(each.getMemberPrice());
            memberPrice.setAddOther(1);
            return memberPrice;
        }).filter(each -> each.getMemberPrice().compareTo(BigDecimal.ZERO) > 0).toList();

        return memberPrices.isEmpty() || memberPriceService.saveBatch(memberPrices);
    }
}
