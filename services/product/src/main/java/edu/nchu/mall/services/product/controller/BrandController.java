package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.Brand;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Brand")
@Slf4j
@RestController
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    BrandService brandService;

    @Parameters(@Parameter(name = "sid", description = "Brand主键"))
    @Operation(summary = "获取Brand详情")
    @GetMapping("/{sid}")
    public R<?> getBrand(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        Brand data = brandService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "Brand主键"),
            @Parameter(name = "body", description = "更新后的Brand")
    })
    @Operation(summary = "更新Brand")
    @PutMapping("/{sid}")
    public R<?> updateBrand(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody Brand body) {
        body.setBrandId(Long.parseLong(sid));
        boolean res = brandService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的Brand"))
    @Operation(summary = "创建Brand")
    @PostMapping
    public R<?> createBrand(@RequestBody Brand body) {
        body.setBrandId(null);
        boolean res = brandService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "Brand主键"))
    @Operation(summary = "删除Brand")
    @DeleteMapping("/{sid}")
    public R<?> deleteBrand(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = brandService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
