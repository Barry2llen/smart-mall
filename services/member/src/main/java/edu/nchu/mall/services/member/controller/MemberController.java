package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.models.dto.MemberDTO;
import edu.nchu.mall.models.entity.Member;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.models.vo.MemberVO;
import edu.nchu.mall.services.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "会员管理")
@Slf4j
@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    MemberService memberService;

    @Parameters(@Parameter(name = "sid", description = "会员主键"))
    @Operation(summary = "获取会员详情")
    @GetMapping("/{sid}")
    public R<MemberVO> getMember(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberVO data = memberService.getMemberById(Long.parseLong(sid));
        return data != null ? R.success(data) : R.fail();
    }

    @Parameters({
            @Parameter(name = "dto", description = "更新的会员信息"),
    })
    @Operation(summary = "更新会员")
    @PutMapping
    public R<?> updateMember(@RequestBody @Validated(Groups.Update.class) MemberDTO dto) {
        boolean res = memberService.updateById(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的会员"))
    @Operation(summary = "创建会员")
    @PostMapping
    public R<?> createMember(@RequestBody @Validated(Groups.Create.class) MemberDTO dto) {
        try{
            boolean res = memberService.save(dto);
            return res ? R.success(null) : R.fail("create failed");
        } catch (CustomException e) {
            return R.fail(e.getMessage());
        }
    }

    @Parameters(@Parameter(name = "sid", description = "会员主键"))
    @Operation(summary = "删除Member")
    @DeleteMapping("/{sid}")
    public R<?> deleteMember(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量")
    })
    @Operation(summary = "获取会员列表")
    @GetMapping
    public R<List<MemberVO>> list(@RequestParam @NotNull Integer pageNum,
                                  @RequestParam @NotNull Integer pageSize) {
        return R.success(memberService.getMembers(pageNum, pageSize));
    }

    @Parameters(@Parameter(name = "key", description = "用户的用户名或邮箱又或电话号码"))
    @Operation(summary = "获取加盐后的密码")
    @GetMapping("/salt")
    public @Nullable String getSaltedPassword(@RequestParam @NotBlank String key) {
        return memberService.getSaltedPassword(key);
    }

    @Parameters({
            @Parameter(name = "email", description = "邮箱", required = true),
            @Parameter(name = "username", description = "用户名", required = false)
    })
    @Operation(summary = "通过邮箱获取用户/创建新用户（仅在oauth登录时内部调用）")
    @GetMapping("/putByEmail")
    public @Nullable Member putByEmail(@RequestParam @NotNull String email, @RequestParam String username) {
        return memberService.putByEmail(email, username);
    }

    @Parameters(@Parameter(name = "key", description = "username/email"))
    @Operation(description = "根据username/email获取用户id")
    @GetMapping("/id/{key}")
    public @Nullable Long getId(@PathVariable @NotBlank String key) {
        return memberService.getMemberIdByUsernameOrEmail(key);
    }
}
