package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.SpuInfo;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.SpuInfoService;
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
@Tag(name = "SpuInfo")
@Slf4j
@RestController
@RequestMapping("/spu-infos")
public class SpuInfoController {

    @Autowired
    SpuInfoService spuInfoService;

    @Parameters(@Parameter(name = "sid", description = "SpuInfo主键"))
    @Operation(summary = "获取SpuInfo详情")
    @GetMapping("/{sid}")
    public R<?> getSpuInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SpuInfo data = spuInfoService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SpuInfo主键"),
            @Parameter(name = "body", description = "更新后的SpuInfo")
    })
    @Operation(summary = "更新SpuInfo")
    @PutMapping("/{sid}")
    public R<?> updateSpuInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SpuInfo body) {
        body.setId(Long.parseLong(sid));
        boolean res = spuInfoService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SpuInfo"))
    @Operation(summary = "创建SpuInfo")
    @PostMapping
    public R<?> createSpuInfo(@RequestBody SpuInfo body) {
        body.setId(null);
        boolean res = spuInfoService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SpuInfo主键"))
    @Operation(summary = "删除SpuInfo")
    @DeleteMapping("/{sid}")
    public R<?> deleteSpuInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = spuInfoService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
