package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.SkuInfo;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.SkuInfoService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Hidden
@Tag(name = "SkuInfo")
@Slf4j
@RestController
@RequestMapping("/sku-infos")
public class SkuInfoController {

    @Autowired
    SkuInfoService skuInfoService;

    @Parameters(@Parameter(name = "sid", description = "SkuInfo主键"))
    @Operation(summary = "获取SkuInfo详情")
    @GetMapping("/{sid}")
    public R<?> getSkuInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SkuInfo data = skuInfoService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SkuInfo主键"),
            @Parameter(name = "body", description = "更新后的SkuInfo")
    })
    @Operation(summary = "更新SkuInfo")
    @PutMapping("/{sid}")
    public R<?> updateSkuInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SkuInfo body) {
        body.setSkuId(Long.parseLong(sid));
        boolean res = skuInfoService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SkuInfo"))
    @Operation(summary = "创建SkuInfo")
    @PostMapping
    public R<?> createSkuInfo(@RequestBody SkuInfo body) {
        body.setSkuId(null);
        boolean res = skuInfoService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SkuInfo主键"))
    @Operation(summary = "删除SkuInfo")
    @DeleteMapping("/{sid}")
    public R<?> deleteSkuInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = skuInfoService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
