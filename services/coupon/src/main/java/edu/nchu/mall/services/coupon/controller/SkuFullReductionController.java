package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.dto.SkuReductionDTO;
import edu.nchu.mall.models.entity.SkuFullReduction;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.SkuFullReductionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SkuFullReduction")
@Slf4j
@RestController
@RequestMapping("/sku-full-reductions")
public class SkuFullReductionController {

    @Autowired
    SkuFullReductionService skuFullReductionService;

    @Parameters(@Parameter(name = "sid", description = "SkuFullReduction主键"))
    @Operation(summary = "获取SkuFullReduction详情")
    @GetMapping("/{sid}")
    public R<?> getSkuFullReduction(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SkuFullReduction data = skuFullReductionService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SkuFullReduction主键"),
            @Parameter(name = "body", description = "更新后的SkuFullReduction")
    })
    @Operation(summary = "更新SkuFullReduction")
    @PutMapping("/{sid}")
    public R<?> updateSkuFullReduction(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SkuFullReduction body) {
        body.setId(Long.parseLong(sid));
        boolean res = skuFullReductionService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SkuFullReduction"))
    @Operation(summary = "创建SkuFullReduction")
    @PostMapping
    public R<?> createSkuFullReduction(@RequestBody SkuFullReduction body) {
        body.setId(null);
        boolean res = skuFullReductionService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SkuFullReduction主键"))
    @Operation(summary = "删除SkuFullReduction")
    @DeleteMapping("/{sid}")
    public R<?> deleteSkuFullReduction(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = skuFullReductionService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @PostMapping("/saveInfo")
    public R<?> saveInfo(@RequestBody SkuReductionDTO dto) {
        boolean res = skuFullReductionService.saveSkuReduction(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("save failed");
    }
}
