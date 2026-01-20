package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.Attr;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.AttrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Attr")
@Slf4j
@RestController
@RequestMapping("/attrs")
public class AttrController {

    @Autowired
    AttrService attrService;

    @Parameters(@Parameter(name = "sid", description = "Attr主键"))
    @Operation(summary = "获取Attr详情")
    @GetMapping("/{sid}")
    public R<?> getAttr(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        Attr data = attrService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "Attr主键"),
            @Parameter(name = "body", description = "更新后的Attr")
    })
    @Operation(summary = "更新Attr")
    @PutMapping("/{sid}")
    public R<?> updateAttr(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody Attr body) {
        body.setAttrId(Long.parseLong(sid));
        boolean res = attrService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的Attr"))
    @Operation(summary = "创建Attr")
    @PostMapping
    public R<?> createAttr(@RequestBody Attr body) {
        body.setAttrId(null);
        boolean res = attrService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "Attr主键"))
    @Operation(summary = "删除Attr")
    @DeleteMapping("/{sid}")
    public R<?> deleteAttr(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = attrService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
