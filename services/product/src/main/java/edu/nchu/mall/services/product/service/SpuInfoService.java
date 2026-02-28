package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.SpuInfoDTO;
import edu.nchu.mall.models.dto.SpuSaveDTO;
import edu.nchu.mall.models.entity.SpuInfo;
import edu.nchu.mall.models.vo.SpuInfoVO;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SpuInfoService extends IService<SpuInfo> {
    boolean updateById(SpuInfoDTO dto);

    @Override
    SpuInfo getById(Serializable id);

    SpuInfoVO getVoById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    boolean save(SpuSaveDTO dto);

    List<SpuInfo> list(Integer pageNum, Integer pageSize, Long catalogId, Long brandId, String key, Integer status);

    Map<Long, SpuInfoVO> getBatchSpuInfo(Iterable<Long> spuIds);
}
