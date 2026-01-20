package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.SeckillPromotion;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.SeckillPromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SeckillPromotion")
@Slf4j
@RestController
@RequestMapping("/seckill-promotions")
public class SeckillPromotionController {

    @Autowired
    SeckillPromotionService seckillPromotionService;

    @Parameters(@Parameter(name = "sid", description = "SeckillPromotion主键"))
    @Operation(summary = "获取SeckillPromotion详情")
    @GetMapping("/{sid}")
    public R<?> getSeckillPromotion(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SeckillPromotion data = seckillPromotionService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SeckillPromotion主键"),
            @Parameter(name = "body", description = "更新后的SeckillPromotion")
    })
    @Operation(summary = "更新SeckillPromotion")
    @PutMapping("/{sid}")
    public R<?> updateSeckillPromotion(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SeckillPromotion body) {
        body.setId(Long.parseLong(sid));
        boolean res = seckillPromotionService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SeckillPromotion"))
    @Operation(summary = "创建SeckillPromotion")
    @PostMapping
    public R<?> createSeckillPromotion(@RequestBody SeckillPromotion body) {
        body.setId(null);
        boolean res = seckillPromotionService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SeckillPromotion主键"))
    @Operation(summary = "删除SeckillPromotion")
    @DeleteMapping("/{sid}")
    public R<?> deleteSeckillPromotion(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = seckillPromotionService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
