package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.SpuImages;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.SpuImagesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SpuImages")
@Slf4j
@RestController
@RequestMapping("/spu-imagess")
public class SpuImagesController {

    @Autowired
    SpuImagesService spuImagesService;

    @Parameters(@Parameter(name = "sid", description = "SpuImages主键"))
    @Operation(summary = "获取SpuImages详情")
    @GetMapping("/{sid}")
    public R<?> getSpuImages(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SpuImages data = spuImagesService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SpuImages主键"),
            @Parameter(name = "body", description = "更新后的SpuImages")
    })
    @Operation(summary = "更新SpuImages")
    @PutMapping("/{sid}")
    public R<?> updateSpuImages(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SpuImages body) {
        body.setId(Long.parseLong(sid));
        boolean res = spuImagesService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SpuImages"))
    @Operation(summary = "创建SpuImages")
    @PostMapping
    public R<?> createSpuImages(@RequestBody SpuImages body) {
        body.setId(null);
        boolean res = spuImagesService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SpuImages主键"))
    @Operation(summary = "删除SpuImages")
    @DeleteMapping("/{sid}")
    public R<?> deleteSpuImages(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = spuImagesService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
