package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.MemberPrice;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.MemberPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MemberPrice")
@Slf4j
@RestController
@RequestMapping("/member-prices")
public class MemberPriceController {

    @Autowired
    MemberPriceService memberPriceService;

    @Parameters(@Parameter(name = "sid", description = "MemberPrice主键"))
    @Operation(summary = "获取MemberPrice详情")
    @GetMapping("/{sid}")
    public R<?> getMemberPrice(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberPrice data = memberPriceService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "MemberPrice主键"),
            @Parameter(name = "body", description = "更新后的MemberPrice")
    })
    @Operation(summary = "更新MemberPrice")
    @PutMapping("/{sid}")
    public R<?> updateMemberPrice(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody MemberPrice body) {
        body.setId(Long.parseLong(sid));
        boolean res = memberPriceService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的MemberPrice"))
    @Operation(summary = "创建MemberPrice")
    @PostMapping
    public R<?> createMemberPrice(@RequestBody MemberPrice body) {
        body.setId(null);
        boolean res = memberPriceService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "MemberPrice主键"))
    @Operation(summary = "删除MemberPrice")
    @DeleteMapping("/{sid}")
    public R<?> deleteMemberPrice(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberPriceService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
