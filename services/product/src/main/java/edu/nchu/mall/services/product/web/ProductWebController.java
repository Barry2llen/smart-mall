package edu.nchu.mall.services.product.web;

import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.vo.CategoryVO;
import edu.nchu.mall.services.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "商品接口", description = "提供商品相关的API")
@RequestMapping("/public/product")
@RestController
public class ProductWebController {

    @Autowired
    CategoryService categoryService;

    @Operation(summary = "以树形结构查出所有分类以及子分类")
    @GetMapping("/category/list")
    public R<List<CategoryVO>> list(){
        return R.success(categoryService.listWithTree());
    }
}
