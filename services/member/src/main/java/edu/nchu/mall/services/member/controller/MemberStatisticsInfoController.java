package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.dto.MemberStatisticsInfoDTO;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.models.vo.MemberStatisticsInfoVO;
import edu.nchu.mall.services.member.service.MemberStatisticsInfoService;
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

@Tag(name = "会员统计信息管理")
@Slf4j
@RestController
@RequestMapping("/member-statistics-infos")
public class MemberStatisticsInfoController {

    @Autowired
    MemberStatisticsInfoService memberStatisticsInfoService;

    @Parameters(@Parameter(name = "sid", description = "会员统计信息主键"))
    @Operation(summary = "获取会员统计信息详情")
    @GetMapping("/{sid}")
    public R<MemberStatisticsInfoVO> getMemberStatisticsInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberStatisticsInfoVO data = memberStatisticsInfoService.getMemberStatisticsInfoById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "dto", description = "更新的会员统计信息")
    })
    @Operation(summary = "更新会员统计信息")
    @PutMapping
    public R<?> updateMemberStatisticsInfo(@RequestBody @Validated(Groups.Update.class) MemberStatisticsInfoDTO dto) {
        boolean res = memberStatisticsInfoService.updateById(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "dto", description = "新增的会员统计信息"))
    @Operation(summary = "创建会员统计信息")
    @PostMapping
    public R<?> createMemberStatisticsInfo(@RequestBody @Validated(Groups.Create.class) MemberStatisticsInfoDTO dto) {
        boolean res = memberStatisticsInfoService.save(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "会员统计信息主键"))
    @Operation(summary = "删除会员统计信息")
    @DeleteMapping("/{sid}")
    public R<?> deleteMemberStatisticsInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberStatisticsInfoService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量")
    })
    @Operation(summary = "获取会员统计信息列表")
    @GetMapping
    public R<List<MemberStatisticsInfoVO>> list(@RequestParam @Valid @NotNull Integer pageNum,
                                               @RequestParam @Valid @NotNull Integer pageSize) {
        return R.success(memberStatisticsInfoService.getMemberStatisticsInfos(pageNum, pageSize));
    }
}
