package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.annotation.validation.NotNullCollection;
import edu.nchu.mall.models.entity.SkuSaleAttrValue;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.SkuSaleAttrValueService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Hidden
@Tag(name = "SkuSaleAttrValue")
@Slf4j
@RestController
@RequestMapping("/sku-sale-attr-values")
public class SkuSaleAttrValueController {

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Parameters(@Parameter(name = "skuId", description = "SkuId"))
    @Operation(summary = "获取Sku的属性值")
    @GetMapping("/sku/{skuId}")
    public R<List<String>> getSkuAttrValues(@PathVariable Long skuId) {
        return new R<>(RCT.SUCCESS, "success", skuSaleAttrValueService.getSkuAttrValues(skuId));
    }

    @Parameters(@Parameter(name = "skuIds", description = "SkuId列表"))
    @Operation(summary = "批量获取Sku的属性值")
    @PostMapping("/sku/batch")
    public R<Map<Long, List<String>>> getBatchSkuAttrValues(@RequestParam @Valid @NotNullCollection List<Long> skuIds) {
        return new R<>(RCT.SUCCESS, "success", skuSaleAttrValueService.getBatchSkuAttrValues(skuIds));
    }

    @Parameters(@Parameter(name = "sid", description = "SkuSaleAttrValue主键"))
    @Operation(summary = "获取SkuSaleAttrValue详情")
    @GetMapping("/{sid}")
    public R<?> getSkuSaleAttrValue(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SkuSaleAttrValue data = skuSaleAttrValueService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SkuSaleAttrValue主键"),
            @Parameter(name = "body", description = "更新后的SkuSaleAttrValue")
    })
    @Operation(summary = "更新SkuSaleAttrValue")
    @PutMapping("/{sid}")
    public R<?> updateSkuSaleAttrValue(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SkuSaleAttrValue body) {
        body.setId(Long.parseLong(sid));
        boolean res = skuSaleAttrValueService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SkuSaleAttrValue"))
    @Operation(summary = "创建SkuSaleAttrValue")
    @PostMapping
    public R<?> createSkuSaleAttrValue(@RequestBody SkuSaleAttrValue body) {
        body.setId(null);
        boolean res = skuSaleAttrValueService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SkuSaleAttrValue主键"))
    @Operation(summary = "删除SkuSaleAttrValue")
    @DeleteMapping("/{sid}")
    public R<?> deleteSkuSaleAttrValue(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = skuSaleAttrValueService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
