package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.dto.MemberLoginLogDTO;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.models.vo.MemberLoginLogVO;
import edu.nchu.mall.services.member.service.MemberLoginLogService;
import io.swagger.v3.oas.annotations.Hidden;
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

@Tag(name = "会员登录日志管理")
@Slf4j
@RestController
@RequestMapping("/member-login-logs")
public class MemberLoginLogController {

    @Autowired
    MemberLoginLogService memberLoginLogService;

    @Parameters(@Parameter(name = "sid", description = "会员登录日志主键"))
    @Operation(summary = "获取会员登录日志详情")
    @GetMapping("/{sid}")
    public R<MemberLoginLogVO> getMemberLoginLog(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberLoginLogVO data = memberLoginLogService.getMemberLoginLogById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "dto", description = "更新的会员登录日志信息")
    })
    @Operation(summary = "更新会员登录日志")
    @PutMapping
    public R<?> updateMemberLoginLog(@RequestBody @Validated(Groups.Update.class) MemberLoginLogDTO dto) {
        boolean res = memberLoginLogService.updateById(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "dto", description = "新增的会员登录日志"))
    @Operation(summary = "创建会员登录日志")
    @PostMapping
    public R<?> createMemberLoginLog(@RequestBody @Validated(Groups.Create.class) MemberLoginLogDTO dto) {
        boolean res = memberLoginLogService.save(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "会员登录日志主键"))
    @Operation(summary = "删除会员登录日志")
    @DeleteMapping("/{sid}")
    public R<?> deleteMemberLoginLog(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberLoginLogService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量")
    })
    @Operation(summary = "获取会员登录日志列表")
    @GetMapping
    public R<List<MemberLoginLogVO>> list(@RequestParam @Valid @NotNull Integer pageNum,
                                         @RequestParam @Valid @NotNull Integer pageSize) {
        return R.success(memberLoginLogService.getMemberLoginLogs(pageNum, pageSize));
    }
}
