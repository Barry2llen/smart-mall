package edu.nchu.mall.components.feign.ware;

import edu.nchu.mall.models.dto.WareSkuLock;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.vo.SkuStockVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("ware")
public interface WareFeignClient {
    @PostMapping("ware-skus/stocks")
    R<List<SkuStockVO>> getStocksBySkuIds(@RequestBody List<Long> skuIds);

    @GetMapping("ware-skus/stock/{skuId}")
    @Nullable R<SkuStockVO> getStockBySkuId(@PathVariable Long skuId);

    @PostMapping("ware-skus/lock")
    R<?> lockStock(@RequestBody WareSkuLock lock);
}
