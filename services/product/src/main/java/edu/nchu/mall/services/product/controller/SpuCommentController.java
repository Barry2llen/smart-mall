package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.SpuComment;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.SpuCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SpuComment")
@Slf4j
@RestController
@RequestMapping("/spu-comments")
public class SpuCommentController {

    @Autowired
    SpuCommentService spuCommentService;

    @Parameters(@Parameter(name = "sid", description = "SpuComment主键"))
    @Operation(summary = "获取SpuComment详情")
    @GetMapping("/{sid}")
    public R<?> getSpuComment(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SpuComment data = spuCommentService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SpuComment主键"),
            @Parameter(name = "body", description = "更新后的SpuComment")
    })
    @Operation(summary = "更新SpuComment")
    @PutMapping("/{sid}")
    public R<?> updateSpuComment(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SpuComment body) {
        body.setId(Long.parseLong(sid));
        boolean res = spuCommentService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SpuComment"))
    @Operation(summary = "创建SpuComment")
    @PostMapping
    public R<?> createSpuComment(@RequestBody SpuComment body) {
        body.setId(null);
        boolean res = spuCommentService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SpuComment主键"))
    @Operation(summary = "删除SpuComment")
    @DeleteMapping("/{sid}")
    public R<?> deleteSpuComment(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = spuCommentService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
