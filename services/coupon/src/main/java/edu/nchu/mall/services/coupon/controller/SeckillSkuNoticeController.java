package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.SeckillSkuNotice;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.SeckillSkuNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SeckillSkuNotice")
@Slf4j
@RestController
@RequestMapping("/seckill-sku-notices")
public class SeckillSkuNoticeController {

    @Autowired
    SeckillSkuNoticeService seckillSkuNoticeService;

    @Parameters(@Parameter(name = "sid", description = "SeckillSkuNotice主键"))
    @Operation(summary = "获取SeckillSkuNotice详情")
    @GetMapping("/{sid}")
    public R<?> getSeckillSkuNotice(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SeckillSkuNotice data = seckillSkuNoticeService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SeckillSkuNotice主键"),
            @Parameter(name = "body", description = "更新后的SeckillSkuNotice")
    })
    @Operation(summary = "更新SeckillSkuNotice")
    @PutMapping("/{sid}")
    public R<?> updateSeckillSkuNotice(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SeckillSkuNotice body) {
        body.setId(Long.parseLong(sid));
        boolean res = seckillSkuNoticeService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SeckillSkuNotice"))
    @Operation(summary = "创建SeckillSkuNotice")
    @PostMapping
    public R<?> createSeckillSkuNotice(@RequestBody SeckillSkuNotice body) {
        body.setId(null);
        boolean res = seckillSkuNoticeService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SeckillSkuNotice主键"))
    @Operation(summary = "删除SeckillSkuNotice")
    @DeleteMapping("/{sid}")
    public R<?> deleteSeckillSkuNotice(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = seckillSkuNoticeService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
