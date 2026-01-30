package edu.nchu.mall.services.member.controller;

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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
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
        return new R<>(RCT.SUCCESS, "success", data);
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
        boolean res = memberService.save(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
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
    public R<List<MemberVO>> list(@RequestParam @Valid @NotNull Integer pageNum,
                                  @RequestParam @Valid @NotNull Integer pageSize) {
        return R.success(memberService.getMembers(pageNum, pageSize));
    }
}
