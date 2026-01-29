package edu.nchu.mall.services.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nchu.mall.models.dto.CategoryDTO;
import edu.nchu.mall.models.entity.Category;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.vo.CategoryVO;
import edu.nchu.mall.services.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Tag(name = "商品分类")
@Slf4j
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    ObjectMapper objectMapper = new ObjectMapper();

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
    @PutMapping()
    public R<?> updateCategory(@RequestBody Map<String, Object> body) {
        if(body.get("catId") == null){
            return R.fail("catId不能为空");
        }
        if(body.size() == 2 && body.get("showStatus") != null) {
            return R.success(null);
        }

        Category category = objectMapper.convertValue(body, Category.class);
        boolean res = categoryService.updateById(category);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的Category"))
    @Operation(summary = "创建Category")
    @PostMapping
    public R<?> createCategory(@RequestBody @Valid CategoryDTO dto) {
        Category category = new Category();
        BeanUtils.copyProperties(dto,category);
        category.setCatId(null);
        boolean res = categoryService.save(category);
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

    @Parameters(@Parameter(name = "ids", description = "要删除分类的id"))
    @DeleteMapping
    @Operation(summary = "删除多个分类")
    public R<?> deleteCategories(@RequestBody Long[] ids){
        boolean res = categoryService.removeByIds(Arrays.asList(ids));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Operation(summary = "以树形结构查出所有分类以及子分类")
    @GetMapping("/list")
    public R<List<CategoryVO>> list(){
        return R.success(categoryService.listWithTree());
    }
}
