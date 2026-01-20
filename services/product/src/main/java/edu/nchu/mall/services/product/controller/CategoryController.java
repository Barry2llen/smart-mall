package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.Category;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Category")
@Slf4j
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Parameters(@Parameter(name = "sid", description = "Category主键"))
    @Operation(summary = "获取Category详情")
    @GetMapping("/{sid}")
    public R<?> getCategory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        Category data = categoryService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "Category主键"),
            @Parameter(name = "body", description = "更新后的Category")
    })
    @Operation(summary = "更新Category")
    @PutMapping("/{sid}")
    public R<?> updateCategory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody Category body) {
        body.setCatId(Long.parseLong(sid));
        boolean res = categoryService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的Category"))
    @Operation(summary = "创建Category")
    @PostMapping
    public R<?> createCategory(@RequestBody Category body) {
        body.setCatId(null);
        boolean res = categoryService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "Category主键"))
    @Operation(summary = "删除Category")
    @DeleteMapping("/{sid}")
    public R<?> deleteCategory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = categoryService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
