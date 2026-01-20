package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.SkuLadder;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.SkuLadderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SkuLadder")
@Slf4j
@RestController
@RequestMapping("/sku-ladders")
public class SkuLadderController {

    @Autowired
    SkuLadderService skuLadderService;

    @Parameters(@Parameter(name = "sid", description = "SkuLadder主键"))
    @Operation(summary = "获取SkuLadder详情")
    @GetMapping("/{sid}")
    public R<?> getSkuLadder(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SkuLadder data = skuLadderService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SkuLadder主键"),
            @Parameter(name = "body", description = "更新后的SkuLadder")
    })
    @Operation(summary = "更新SkuLadder")
    @PutMapping("/{sid}")
    public R<?> updateSkuLadder(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SkuLadder body) {
        body.setId(Long.parseLong(sid));
        boolean res = skuLadderService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SkuLadder"))
    @Operation(summary = "创建SkuLadder")
    @PostMapping
    public R<?> createSkuLadder(@RequestBody SkuLadder body) {
        body.setId(null);
        boolean res = skuLadderService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SkuLadder主键"))
    @Operation(summary = "删除SkuLadder")
    @DeleteMapping("/{sid}")
    public R<?> deleteSkuLadder(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = skuLadderService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
