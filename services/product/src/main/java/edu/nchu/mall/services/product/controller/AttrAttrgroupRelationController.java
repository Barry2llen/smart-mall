package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.AttrAttrgroupRelation;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.AttrAttrgroupRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AttrAttrgroupRelation")
@Slf4j
@RestController
@RequestMapping("/attr-attrgroup-relations")
public class AttrAttrgroupRelationController {

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Parameters(@Parameter(name = "sid", description = "AttrAttrgroupRelation主键"))
    @Operation(summary = "获取AttrAttrgroupRelation详情")
    @GetMapping("/{sid}")
    public R<?> getAttrAttrgroupRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        AttrAttrgroupRelation data = attrAttrgroupRelationService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "AttrAttrgroupRelation主键"),
            @Parameter(name = "body", description = "更新后的AttrAttrgroupRelation")
    })
    @Operation(summary = "更新AttrAttrgroupRelation")
    @PutMapping("/{sid}")
    public R<?> updateAttrAttrgroupRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody AttrAttrgroupRelation body) {
        body.setId(Long.parseLong(sid));
        boolean res = attrAttrgroupRelationService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的AttrAttrgroupRelation"))
    @Operation(summary = "创建AttrAttrgroupRelation")
    @PostMapping
    public R<?> createAttrAttrgroupRelation(@RequestBody AttrAttrgroupRelation body) {
        body.setId(null);
        boolean res = attrAttrgroupRelationService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "AttrAttrgroupRelation主键"))
    @Operation(summary = "删除AttrAttrgroupRelation")
    @DeleteMapping("/{sid}")
    public R<?> deleteAttrAttrgroupRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = attrAttrgroupRelationService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
