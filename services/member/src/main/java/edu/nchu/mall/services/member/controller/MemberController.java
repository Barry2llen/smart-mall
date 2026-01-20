package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.entity.Member;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member")
@Slf4j
@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    MemberService memberService;

    @Parameters(@Parameter(name = "sid", description = "Member主键"))
    @Operation(summary = "获取Member详情")
    @GetMapping("/{sid}")
    public R<?> getMember(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        Member data = memberService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "Member主键"),
            @Parameter(name = "body", description = "更新后的Member")
    })
    @Operation(summary = "更新Member")
    @PutMapping("/{sid}")
    public R<?> updateMember(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody Member body) {
        body.setId(Long.parseLong(sid));
        boolean res = memberService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的Member"))
    @Operation(summary = "创建Member")
    @PostMapping
    public R<?> createMember(@RequestBody Member body) {
        body.setId(null);
        boolean res = memberService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "Member主键"))
    @Operation(summary = "删除Member")
    @DeleteMapping("/{sid}")
    public R<?> deleteMember(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
