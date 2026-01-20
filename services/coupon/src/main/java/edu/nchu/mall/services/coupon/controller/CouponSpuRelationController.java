package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.CouponSpuRelation;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.CouponSpuRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CouponSpuRelation")
@Slf4j
@RestController
@RequestMapping("/coupon-spu-relations")
public class CouponSpuRelationController {

    @Autowired
    CouponSpuRelationService couponSpuRelationService;

    @Parameters(@Parameter(name = "sid", description = "CouponSpuRelation主键"))
    @Operation(summary = "获取CouponSpuRelation详情")
    @GetMapping("/{sid}")
    public R<?> getCouponSpuRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        CouponSpuRelation data = couponSpuRelationService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "CouponSpuRelation主键"),
            @Parameter(name = "body", description = "更新后的CouponSpuRelation")
    })
    @Operation(summary = "更新CouponSpuRelation")
    @PutMapping("/{sid}")
    public R<?> updateCouponSpuRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody CouponSpuRelation body) {
        body.setId(Long.parseLong(sid));
        boolean res = couponSpuRelationService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的CouponSpuRelation"))
    @Operation(summary = "创建CouponSpuRelation")
    @PostMapping
    public R<?> createCouponSpuRelation(@RequestBody CouponSpuRelation body) {
        body.setId(null);
        boolean res = couponSpuRelationService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "CouponSpuRelation主键"))
    @Operation(summary = "删除CouponSpuRelation")
    @DeleteMapping("/{sid}")
    public R<?> deleteCouponSpuRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = couponSpuRelationService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
