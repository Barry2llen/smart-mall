package edu.nchu.mall.services.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.WareSku;
import edu.nchu.mall.models.vo.SkuStockVO;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

public interface WareSkuService extends IService<WareSku> {
    @Override
    boolean updateById(WareSku entity);

    @Override
    WareSku getById(Serializable id);

    @Override
    boolean save(WareSku entity);

    @Override
    boolean removeById(Serializable id);

    List<WareSku> list(Integer pageNum, Integer pageSize, String wareKey, String skuKey);

    List<SkuStockVO> getStocksBySkuIds(List<Long> skuIds);

    @Nullable SkuStockVO getStockBySkuId(Long skuId);
}
