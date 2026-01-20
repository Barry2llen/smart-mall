package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.entity.MemberStatisticsInfo;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.member.service.MemberStatisticsInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MemberStatisticsInfo")
@Slf4j
@RestController
@RequestMapping("/member-statistics-infos")
public class MemberStatisticsInfoController {

    @Autowired
    MemberStatisticsInfoService memberStatisticsInfoService;

    @Parameters(@Parameter(name = "sid", description = "MemberStatisticsInfo主键"))
    @Operation(summary = "获取MemberStatisticsInfo详情")
    @GetMapping("/{sid}")
    public R<?> getMemberStatisticsInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberStatisticsInfo data = memberStatisticsInfoService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "MemberStatisticsInfo主键"),
            @Parameter(name = "body", description = "更新后的MemberStatisticsInfo")
    })
    @Operation(summary = "更新MemberStatisticsInfo")
    @PutMapping("/{sid}")
    public R<?> updateMemberStatisticsInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody MemberStatisticsInfo body) {
        body.setId(Long.parseLong(sid));
        boolean res = memberStatisticsInfoService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的MemberStatisticsInfo"))
    @Operation(summary = "创建MemberStatisticsInfo")
    @PostMapping
    public R<?> createMemberStatisticsInfo(@RequestBody MemberStatisticsInfo body) {
        body.setId(null);
        boolean res = memberStatisticsInfoService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "MemberStatisticsInfo主键"))
    @Operation(summary = "删除MemberStatisticsInfo")
    @DeleteMapping("/{sid}")
    public R<?> deleteMemberStatisticsInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberStatisticsInfoService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
