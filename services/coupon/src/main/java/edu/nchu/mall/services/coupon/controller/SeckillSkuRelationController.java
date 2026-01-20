package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.SeckillSkuRelation;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.SeckillSkuRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SeckillSkuRelation")
@Slf4j
@RestController
@RequestMapping("/seckill-sku-relations")
public class SeckillSkuRelationController {

    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Parameters(@Parameter(name = "sid", description = "SeckillSkuRelation主键"))
    @Operation(summary = "获取SeckillSkuRelation详情")
    @GetMapping("/{sid}")
    public R<?> getSeckillSkuRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SeckillSkuRelation data = seckillSkuRelationService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SeckillSkuRelation主键"),
            @Parameter(name = "body", description = "更新后的SeckillSkuRelation")
    })
    @Operation(summary = "更新SeckillSkuRelation")
    @PutMapping("/{sid}")
    public R<?> updateSeckillSkuRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SeckillSkuRelation body) {
        body.setId(Long.parseLong(sid));
        boolean res = seckillSkuRelationService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SeckillSkuRelation"))
    @Operation(summary = "创建SeckillSkuRelation")
    @PostMapping
    public R<?> createSeckillSkuRelation(@RequestBody SeckillSkuRelation body) {
        body.setId(null);
        boolean res = seckillSkuRelationService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SeckillSkuRelation主键"))
    @Operation(summary = "删除SeckillSkuRelation")
    @DeleteMapping("/{sid}")
    public R<?> deleteSeckillSkuRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = seckillSkuRelationService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
