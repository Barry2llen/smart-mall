package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.SkuImages;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.SkuImagesService;
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
@Tag(name = "SkuImages")
@Slf4j
@RestController
@RequestMapping("/sku-imagess")
public class SkuImagesController {

    @Autowired
    SkuImagesService skuImagesService;

    @Parameters(@Parameter(name = "sid", description = "SkuImages主键"))
    @Operation(summary = "获取SkuImages详情")
    @GetMapping("/{sid}")
    public R<?> getSkuImages(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SkuImages data = skuImagesService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SkuImages主键"),
            @Parameter(name = "body", description = "更新后的SkuImages")
    })
    @Operation(summary = "更新SkuImages")
    @PutMapping("/{sid}")
    public R<?> updateSkuImages(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SkuImages body) {
        body.setId(Long.parseLong(sid));
        boolean res = skuImagesService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SkuImages"))
    @Operation(summary = "创建SkuImages")
    @PostMapping
    public R<?> createSkuImages(@RequestBody SkuImages body) {
        body.setId(null);
        boolean res = skuImagesService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SkuImages主键"))
    @Operation(summary = "删除SkuImages")
    @DeleteMapping("/{sid}")
    public R<?> deleteSkuImages(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = skuImagesService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
