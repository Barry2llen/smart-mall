package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.SpuInfoDesc;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.SpuInfoDescService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SpuInfoDesc")
@Slf4j
@RestController
@RequestMapping("/spu-info-descs")
public class SpuInfoDescController {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Parameters(@Parameter(name = "sid", description = "SpuInfoDesc主键"))
    @Operation(summary = "获取SpuInfoDesc详情")
    @GetMapping("/{sid}")
    public R<?> getSpuInfoDesc(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SpuInfoDesc data = spuInfoDescService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SpuInfoDesc主键"),
            @Parameter(name = "body", description = "更新后的SpuInfoDesc")
    })
    @Operation(summary = "更新SpuInfoDesc")
    @PutMapping("/{sid}")
    public R<?> updateSpuInfoDesc(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SpuInfoDesc body) {
        body.setSpuId(Long.parseLong(sid));
        boolean res = spuInfoDescService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SpuInfoDesc"))
    @Operation(summary = "创建SpuInfoDesc")
    @PostMapping
    public R<?> createSpuInfoDesc(@RequestBody SpuInfoDesc body) {
        body.setSpuId(null);
        boolean res = spuInfoDescService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SpuInfoDesc主键"))
    @Operation(summary = "删除SpuInfoDesc")
    @DeleteMapping("/{sid}")
    public R<?> deleteSpuInfoDesc(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = spuInfoDescService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
