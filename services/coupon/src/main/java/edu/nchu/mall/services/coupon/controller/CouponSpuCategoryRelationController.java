package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.CouponSpuCategoryRelation;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.CouponSpuCategoryRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CouponSpuCategoryRelation")
@Slf4j
@RestController
@RequestMapping("/coupon-spu-category-relations")
public class CouponSpuCategoryRelationController {

    @Autowired
    CouponSpuCategoryRelationService couponSpuCategoryRelationService;

    @Parameters(@Parameter(name = "sid", description = "CouponSpuCategoryRelation主键"))
    @Operation(summary = "获取CouponSpuCategoryRelation详情")
    @GetMapping("/{sid}")
    public R<?> getCouponSpuCategoryRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        CouponSpuCategoryRelation data = couponSpuCategoryRelationService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "CouponSpuCategoryRelation主键"),
            @Parameter(name = "body", description = "更新后的CouponSpuCategoryRelation")
    })
    @Operation(summary = "更新CouponSpuCategoryRelation")
    @PutMapping("/{sid}")
    public R<?> updateCouponSpuCategoryRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody CouponSpuCategoryRelation body) {
        body.setId(Long.parseLong(sid));
        boolean res = couponSpuCategoryRelationService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的CouponSpuCategoryRelation"))
    @Operation(summary = "创建CouponSpuCategoryRelation")
    @PostMapping
    public R<?> createCouponSpuCategoryRelation(@RequestBody CouponSpuCategoryRelation body) {
        body.setId(null);
        boolean res = couponSpuCategoryRelationService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "CouponSpuCategoryRelation主键"))
    @Operation(summary = "删除CouponSpuCategoryRelation")
    @DeleteMapping("/{sid}")
    public R<?> deleteCouponSpuCategoryRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = couponSpuCategoryRelationService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
