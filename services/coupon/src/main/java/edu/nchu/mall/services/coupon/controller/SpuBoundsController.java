package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.SpuBounds;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.SpuBoundsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SpuBounds")
@Slf4j
@RestController
@RequestMapping("/spu-bounds")
public class SpuBoundsController {

    @Autowired
    SpuBoundsService spuBoundsService;

    @Parameters(@Parameter(name = "sid", description = "SpuBounds主键"))
    @Operation(summary = "获取SpuBounds详情")
    @GetMapping("/{sid}")
    public R<?> getSpuBounds(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SpuBounds data = spuBoundsService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SpuBounds主键"),
            @Parameter(name = "body", description = "更新后的SpuBounds")
    })
    @Operation(summary = "更新SpuBounds")
    @PutMapping("/{sid}")
    public R<?> updateSpuBounds(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SpuBounds body) {
        body.setId(Long.parseLong(sid));
        boolean res = spuBoundsService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SpuBounds"))
    @Operation(summary = "创建SpuBounds")
    @PostMapping
    public R<?> createSpuBounds(@RequestBody SpuBounds body) {
        body.setId(null);
        boolean res = spuBoundsService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SpuBounds主键"))
    @Operation(summary = "删除SpuBounds")
    @DeleteMapping("/{sid}")
    public R<?> deleteSpuBounds(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = spuBoundsService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
