package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.CategoryBrandRelation;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.CategoryBrandRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CategoryBrandRelation")
@Slf4j
@RestController
@RequestMapping("/category-brand-relations")
public class CategoryBrandRelationController {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Parameters(@Parameter(name = "sid", description = "CategoryBrandRelation主键"))
    @Operation(summary = "获取CategoryBrandRelation详情")
    @GetMapping("/{sid}")
    public R<?> getCategoryBrandRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        CategoryBrandRelation data = categoryBrandRelationService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "CategoryBrandRelation主键"),
            @Parameter(name = "body", description = "更新后的CategoryBrandRelation")
    })
    @Operation(summary = "更新CategoryBrandRelation")
    @PutMapping("/{sid}")
    public R<?> updateCategoryBrandRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody CategoryBrandRelation body) {
        body.setId(Long.parseLong(sid));
        boolean res = categoryBrandRelationService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的CategoryBrandRelation"))
    @Operation(summary = "创建CategoryBrandRelation")
    @PostMapping
    public R<?> createCategoryBrandRelation(@RequestBody CategoryBrandRelation body) {
        body.setId(null);
        boolean res = categoryBrandRelationService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "CategoryBrandRelation主键"))
    @Operation(summary = "删除CategoryBrandRelation")
    @DeleteMapping("/{sid}")
    public R<?> deleteCategoryBrandRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = categoryBrandRelationService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
