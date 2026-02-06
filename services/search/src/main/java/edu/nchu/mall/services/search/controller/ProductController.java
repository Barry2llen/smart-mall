package edu.nchu.mall.services.search.controller;


import edu.nchu.mall.models.annotation.NotNullCollection;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.services.search.document.Product;
import edu.nchu.mall.services.search.exception.EsOperationException;
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

import java.util.List;

@Tag(name = "商品")
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @Parameters({
            @Parameter(name = "product", description = "商品信息")
    })
    @Operation(summary = "保存商品至es")
    @PostMapping
    public R<?> saveProduct(@RequestBody @Valid Product product) {
        try{
            productService.save(product);
        } catch (Exception e){
            throw new EsOperationException("保存商品异常", e);
        }
        return R.success();
    }

    @Parameters({
            @Parameter(name = "products", description = "商品信息")
    })
    @Operation(summary = "批量保存商品至es")
    @PostMapping("/bulk")
    public R<?> saveProductAll(@RequestBody @Valid @NotNullCollection List<Product> products) {
        try{
            productService.saveAll(products);
        } catch (Exception e) {
            throw new EsOperationException("保存商品异常", e);
        }
        return R.success();
    }
}
