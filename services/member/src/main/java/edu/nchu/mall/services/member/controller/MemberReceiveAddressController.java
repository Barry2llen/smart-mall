package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.entity.MemberReceiveAddress;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.member.service.MemberReceiveAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MemberReceiveAddress")
@Slf4j
@RestController
@RequestMapping("/member-receive-addresss")
public class MemberReceiveAddressController {

    @Autowired
    MemberReceiveAddressService memberReceiveAddressService;

    @Parameters(@Parameter(name = "sid", description = "MemberReceiveAddress主键"))
    @Operation(summary = "获取MemberReceiveAddress详情")
    @GetMapping("/{sid}")
    public R<?> getMemberReceiveAddress(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberReceiveAddress data = memberReceiveAddressService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "MemberReceiveAddress主键"),
            @Parameter(name = "body", description = "更新后的MemberReceiveAddress")
    })
    @Operation(summary = "更新MemberReceiveAddress")
    @PutMapping("/{sid}")
    public R<?> updateMemberReceiveAddress(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody MemberReceiveAddress body) {
        body.setId(Long.parseLong(sid));
        boolean res = memberReceiveAddressService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的MemberReceiveAddress"))
    @Operation(summary = "创建MemberReceiveAddress")
    @PostMapping
    public R<?> createMemberReceiveAddress(@RequestBody MemberReceiveAddress body) {
        body.setId(null);
        boolean res = memberReceiveAddressService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "MemberReceiveAddress主键"))
    @Operation(summary = "删除MemberReceiveAddress")
    @DeleteMapping("/{sid}")
    public R<?> deleteMemberReceiveAddress(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberReceiveAddressService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
