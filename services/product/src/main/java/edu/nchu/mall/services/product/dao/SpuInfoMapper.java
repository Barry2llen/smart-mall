package edu.nchu.mall.services.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.nchu.mall.models.entity.SpuInfo;
import edu.nchu.mall.models.vo.SpuInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

@Mapper
public interface SpuInfoMapper extends BaseMapper<SpuInfo> {

    SpuInfoVO getSpuInfoById(@Param("id") Serializable id);

    List<SpuInfoVO> getBatchSpuInfoById(@Param("ids") Iterable<Long> ids);
}
