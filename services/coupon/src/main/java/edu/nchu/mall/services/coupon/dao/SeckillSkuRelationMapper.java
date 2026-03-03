package edu.nchu.mall.services.coupon.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.nchu.mall.models.entity.SeckillSkuRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SeckillSkuRelationMapper extends BaseMapper<SeckillSkuRelation> {

    IPage<SeckillSkuRelation> selectPageByCondition(Page<SeckillSkuRelation> page,
                                                    @Param("key") String key,
                                                    @Param("numericPrefixRanges") List<Map<String, Long>> numericPrefixRanges,
                                                    @Param("promotionSessionId") Long promotionSessionId);
}
