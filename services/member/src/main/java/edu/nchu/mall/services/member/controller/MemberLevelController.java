package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.dto.MemberLevelDTO;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.models.vo.MemberLevelVO;
import edu.nchu.mall.services.member.service.MemberLevelService;
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

@Tag(name = "会员等级管理")
@Slf4j
@RestController
@RequestMapping("/member-levels")
public class MemberLevelController {

    @Autowired
    MemberLevelService memberLevelService;

    @Parameters(@Parameter(name = "sid", description = "会员等级主键"))
    @Operation(summary = "获取会员等级详情")
    @GetMapping("/{sid}")
    public R<MemberLevelVO> getMemberLevel(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberLevelVO data = memberLevelService.getMemberLevelById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "dto", description = "更新的会员等级信息")
    })
    @Operation(summary = "更新会员等级")
    @PutMapping
    public R<?> updateMemberLevel(@RequestBody @Validated(Groups.Update.class) MemberLevelDTO dto) {
        boolean res = memberLevelService.updateById(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "dto", description = "新增的会员等级"))
    @Operation(summary = "创建会员等级")
    @PostMapping
    public R<?> createMemberLevel(@RequestBody @Validated(Groups.Create.class) MemberLevelDTO dto) {
        boolean res = memberLevelService.save(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "会员等级主键"))
    @Operation(summary = "删除会员等级")
    @DeleteMapping("/{sid}")
    public R<?> deleteMemberLevel(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberLevelService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量")
    })
    @Operation(summary = "获取会员等级列表")
    @GetMapping
    public R<List<MemberLevelVO>> list(@RequestParam @Valid @NotNull Integer pageNum,
                                       @RequestParam @Valid @NotNull Integer pageSize) {
        return R.success(memberLevelService.getMemberLevels(pageNum, pageSize));
    }
}
