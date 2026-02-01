package edu.nchu.mall.components.feign.coupon;

import edu.nchu.mall.models.dto.BoundsDTO;
import edu.nchu.mall.models.dto.SkuReductionDTO;
import edu.nchu.mall.models.entity.SpuBounds;
import edu.nchu.mall.models.model.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("coupon")
public interface CouponFeignClient {
    @PostMapping("spu-bounds")
    R<?> createSpuBounds(@RequestBody BoundsDTO body);

    @PostMapping("spu-bounds")
    R<?> createSpuBounds(@RequestBody SpuBounds body);

    @PostMapping("/sku-full-reductions/saveInfo")
    R<?> saveInfo(@RequestBody SkuReductionDTO dto);
}
