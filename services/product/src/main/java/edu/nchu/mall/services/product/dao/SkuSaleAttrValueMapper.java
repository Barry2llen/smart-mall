package edu.nchu.mall.services.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.nchu.mall.models.entity.SkuSaleAttrValue;
import edu.nchu.mall.models.vo.SkuItemSaleAttrVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    List<SkuItemSaleAttrVO> getSaleAttrsBySpuId(@Param("spuId") Long spuId);
}
