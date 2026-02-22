package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.SkuInfoDTO;
import edu.nchu.mall.models.entity.SkuInfo;
import edu.nchu.mall.models.vo.SkuInfoVO;
import edu.nchu.mall.models.vo.SkuItemVO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SkuInfoService extends IService<SkuInfo> {

    SkuInfoVO getVOById(Serializable id);

    boolean updateById(SkuInfoDTO entity);

    List<SkuInfoVO> list(Integer pageNum, Integer pageSize, Long catalogId, Long brandId, String key, BigDecimal minPrice, BigDecimal maxPrice);

    SkuItemVO getSkuItem(long l);

    Map<Long, SkuInfoVO> getBatchByIds(Iterable<Long> ids);

    boolean existsById(Long skuId);
}
