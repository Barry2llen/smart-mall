package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.dto.CategoryBrandRelationDTO;
import edu.nchu.mall.models.entity.CategoryBrandRelation;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.models.vo.CategoryBrandRelationVO;
import edu.nchu.mall.services.product.service.BrandService;
import edu.nchu.mall.services.product.service.CategoryBrandRelationService;
import edu.nchu.mall.services.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "品牌关联分类关系")
@Slf4j
@RestController
@RequestMapping("/category-brand-relations")
public class CategoryBrandRelationController {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Parameters(@Parameter(name = "sid", description = "品牌关联分类信息主键"))
    @Operation(summary = "品牌关联分类信息详情")
    @GetMapping("/{sid}")
    public R<?> getCategoryBrandRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        CategoryBrandRelation data = categoryBrandRelationService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters(@Parameter(name = "body", description = "新增的品牌关联分类信息"))
    @Operation(summary = "创建品牌关联分类信息")
    @PostMapping
    public R<?> createCategoryBrandRelation(@RequestBody @Validated(Groups.Update.class) CategoryBrandRelationDTO dto) {
        boolean res = categoryBrandRelationService.save(dto.getBrandId(), dto.getCatelogId());
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "品牌关联分类信息主键"))
    @Operation(summary = "删除品牌关联分类信息")
    @DeleteMapping("/{sid}")
    public R<?> deleteCategoryBrandRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = categoryBrandRelationService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters(@Parameter(name = "brandId", description = "品牌id"))
    @Operation(summary = "获取品牌关联的分类列表")
    @GetMapping("/catelog/{brandId}")
    public R<List<CategoryBrandRelationVO>> getCatelogList(@PathVariable("brandId") Long brandId) {
        return R.success(categoryBrandRelationService.getRelatedCategoriesByBrandId(brandId));
    }

    @Parameters(@Parameter(name = "catId", description = "分类id"))
    @Operation(summary = "获取分类关联的品牌列表")
    @GetMapping("/brand/{catId}")
    public R<List<CategoryBrandRelationVO>> getBrandList(@PathVariable("catId") Long catId) {
        return R.success(categoryBrandRelationService.getRelatedBrandsByCatId(catId));
    }
}
