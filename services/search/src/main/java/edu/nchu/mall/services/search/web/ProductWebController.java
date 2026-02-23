package edu.nchu.mall.services.search.web;

import edu.nchu.mall.models.annotation.bind.UserId;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.services.search.dto.ProductSearchParam;
import edu.nchu.mall.services.search.dto.ProductSearchResult;
import edu.nchu.mall.services.search.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "商品")
@RestController
@RequestMapping("/public/product")
public class ProductWebController {

    @Autowired
    ProductService productService;

    @Parameters({
            @Parameter(name = "param", description = "搜索参数")
    })
    @Operation(summary = "搜索商品")
    @PostMapping("/search")
    public R<ProductSearchResult> search(
            @UserId Long userId,
            @RequestBody @Valid ProductSearchParam param) {
        ProductSearchResult result = productService.search(param);
        return R.success(result);
    }
}
