package edu.nchu.mall.services.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.nchu.mall.models.entity.WareSku;
import edu.nchu.mall.models.vo.SkuStockVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WareSkuMapper extends BaseMapper<WareSku> {
    List<SkuStockVO> getStockBySkuIds(@Param("skuIds") List<Long> skuIds);

    SkuStockVO getStockBySkuId(@Param("skuId") Long skuId);
}
