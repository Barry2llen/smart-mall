package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.Coupon;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Coupon")
@Slf4j
@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    CouponService couponService;

    @Parameters(@Parameter(name = "sid", description = "Coupon主键"))
    @Operation(summary = "获取Coupon详情")
    @GetMapping("/{sid}")
    public R<?> getCoupon(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        Coupon data = couponService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "Coupon主键"),
            @Parameter(name = "body", description = "更新后的Coupon")
    })
    @Operation(summary = "更新Coupon")
    @PutMapping("/{sid}")
    public R<?> updateCoupon(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody Coupon body) {
        body.setId(Long.parseLong(sid));
        boolean res = couponService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的Coupon"))
    @Operation(summary = "创建Coupon")
    @PostMapping
    public R<?> createCoupon(@RequestBody Coupon body) {
        body.setId(null);
        boolean res = couponService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "Coupon主键"))
    @Operation(summary = "删除Coupon")
    @DeleteMapping("/{sid}")
    public R<?> deleteCoupon(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = couponService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
