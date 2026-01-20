package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.CouponHistory;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.CouponHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CouponHistory")
@Slf4j
@RestController
@RequestMapping("/coupon-histories")
public class CouponHistoryController {

    @Autowired
    CouponHistoryService couponHistoryService;

    @Parameters(@Parameter(name = "sid", description = "CouponHistory主键"))
    @Operation(summary = "获取CouponHistory详情")
    @GetMapping("/{sid}")
    public R<?> getCouponHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        CouponHistory data = couponHistoryService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "CouponHistory主键"),
            @Parameter(name = "body", description = "更新后的CouponHistory")
    })
    @Operation(summary = "更新CouponHistory")
    @PutMapping("/{sid}")
    public R<?> updateCouponHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody CouponHistory body) {
        body.setId(Long.parseLong(sid));
        boolean res = couponHistoryService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的CouponHistory"))
    @Operation(summary = "创建CouponHistory")
    @PostMapping
    public R<?> createCouponHistory(@RequestBody CouponHistory body) {
        body.setId(null);
        boolean res = couponHistoryService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "CouponHistory主键"))
    @Operation(summary = "删除CouponHistory")
    @DeleteMapping("/{sid}")
    public R<?> deleteCouponHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = couponHistoryService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
