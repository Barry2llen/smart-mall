package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.ProductAttrValue;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.ProductAttrValueService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Hidden
@Tag(name = "ProductAttrValue")
@Slf4j
@RestController
@RequestMapping("/product-attr-values")
public class ProductAttrValueController {

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Parameters(@Parameter(name = "sid", description = "ProductAttrValue主键"))
    @Operation(summary = "获取ProductAttrValue详情")
    @GetMapping("/{sid}")
    public R<?> getProductAttrValue(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        ProductAttrValue data = productAttrValueService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "ProductAttrValue主键"),
            @Parameter(name = "body", description = "更新后的ProductAttrValue")
    })
    @Operation(summary = "更新ProductAttrValue")
    @PutMapping("/{sid}")
    public R<?> updateProductAttrValue(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody ProductAttrValue body) {
        body.setId(Long.parseLong(sid));
        boolean res = productAttrValueService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的ProductAttrValue"))
    @Operation(summary = "创建ProductAttrValue")
    @PostMapping
    public R<?> createProductAttrValue(@RequestBody ProductAttrValue body) {
        body.setId(null);
        boolean res = productAttrValueService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "ProductAttrValue主键"))
    @Operation(summary = "删除ProductAttrValue")
    @DeleteMapping("/{sid}")
    public R<?> deleteProductAttrValue(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = productAttrValueService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
