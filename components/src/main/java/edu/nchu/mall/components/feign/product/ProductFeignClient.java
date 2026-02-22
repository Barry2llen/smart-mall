package edu.nchu.mall.components.feign.product;

import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.vo.SkuInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient("product")
public interface ProductFeignClient {
    @GetMapping("/sku-infos/{sid}")
    R<SkuInfoVO> getSkuInfo(@PathVariable String sid);

    @PostMapping("/sku-infos/batch")
    R<Map<Long, SkuInfoVO>> getBatch(@RequestBody Collection<Long> ids);

    @GetMapping("/sku-infos/exists/{skuId}")
    R<Boolean> exists(@PathVariable Long skuId);

    @GetMapping("/sku-sale-attr-values/sku/{skuId}")
    R<List<String>> getSkuAttrValues(@PathVariable Long skuId);

    @PostMapping("/sku-sale-attr-values/sku/batch")
    R<Map<Long, List<String>>> getBatchSkuAttrValues(@RequestParam List<Long> skuIds);
}
