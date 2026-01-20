package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.entity.MemberLoginLog;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.member.service.MemberLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MemberLoginLog")
@Slf4j
@RestController
@RequestMapping("/member-login-logs")
public class MemberLoginLogController {

    @Autowired
    MemberLoginLogService memberLoginLogService;

    @Parameters(@Parameter(name = "sid", description = "MemberLoginLog主键"))
    @Operation(summary = "获取MemberLoginLog详情")
    @GetMapping("/{sid}")
    public R<?> getMemberLoginLog(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberLoginLog data = memberLoginLogService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "MemberLoginLog主键"),
            @Parameter(name = "body", description = "更新后的MemberLoginLog")
    })
    @Operation(summary = "更新MemberLoginLog")
    @PutMapping("/{sid}")
    public R<?> updateMemberLoginLog(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody MemberLoginLog body) {
        body.setId(Long.parseLong(sid));
        boolean res = memberLoginLogService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的MemberLoginLog"))
    @Operation(summary = "创建MemberLoginLog")
    @PostMapping
    public R<?> createMemberLoginLog(@RequestBody MemberLoginLog body) {
        body.setId(null);
        boolean res = memberLoginLogService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "MemberLoginLog主键"))
    @Operation(summary = "删除MemberLoginLog")
    @DeleteMapping("/{sid}")
    public R<?> deleteMemberLoginLog(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberLoginLogService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
