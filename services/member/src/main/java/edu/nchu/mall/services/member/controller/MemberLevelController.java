package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.entity.MemberLevel;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.member.service.MemberLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MemberLevel")
@Slf4j
@RestController
@RequestMapping("/member-levels")
public class MemberLevelController {

    @Autowired
    MemberLevelService memberLevelService;

    @Parameters(@Parameter(name = "sid", description = "MemberLevel主键"))
    @Operation(summary = "获取MemberLevel详情")
    @GetMapping("/{sid}")
    public R<?> getMemberLevel(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberLevel data = memberLevelService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "MemberLevel主键"),
            @Parameter(name = "body", description = "更新后的MemberLevel")
    })
    @Operation(summary = "更新MemberLevel")
    @PutMapping("/{sid}")
    public R<?> updateMemberLevel(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody MemberLevel body) {
        body.setId(Long.parseLong(sid));
        boolean res = memberLevelService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的MemberLevel"))
    @Operation(summary = "创建MemberLevel")
    @PostMapping
    public R<?> createMemberLevel(@RequestBody MemberLevel body) {
        body.setId(null);
        boolean res = memberLevelService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "MemberLevel主键"))
    @Operation(summary = "删除MemberLevel")
    @DeleteMapping("/{sid}")
    public R<?> deleteMemberLevel(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberLevelService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
