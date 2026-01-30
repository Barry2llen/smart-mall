package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.dto.GrowthChangeHistoryDTO;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.models.vo.GrowthChangeHistoryVO;
import edu.nchu.mall.services.member.service.GrowthChangeHistoryService;
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

@Tag(name = "成长值变更记录管理")
@Slf4j
@RestController
@RequestMapping("/growth-change-histories")
public class GrowthChangeHistoryController {

    @Autowired
    GrowthChangeHistoryService growthChangeHistoryService;

    @Parameters(@Parameter(name = "sid", description = "成长值变更记录主键"))
    @Operation(summary = "获取成长值变更记录详情")
    @GetMapping("/{sid}")
    public R<GrowthChangeHistoryVO> getGrowthChangeHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        GrowthChangeHistoryVO data = growthChangeHistoryService.getGrowthChangeHistoryById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "dto", description = "更新的成长值变更记录信息")
    })
    @Operation(summary = "更新成长值变更记录")
    @PutMapping
    public R<?> updateGrowthChangeHistory(@RequestBody @Validated(Groups.Update.class) GrowthChangeHistoryDTO dto) {
        boolean res = growthChangeHistoryService.updateById(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "dto", description = "新增的成长值变更记录"))
    @Operation(summary = "创建成长值变更记录")
    @PostMapping
    public R<?> createGrowthChangeHistory(@RequestBody @Validated(Groups.Create.class) GrowthChangeHistoryDTO dto) {
        boolean res = growthChangeHistoryService.save(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "成长值变更记录主键"))
    @Operation(summary = "删除成长值变更记录")
    @DeleteMapping("/{sid}")
    public R<?> deleteGrowthChangeHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = growthChangeHistoryService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量")
    })
    @Operation(summary = "获取成长值变更记录列表")
    @GetMapping
    public R<List<GrowthChangeHistoryVO>> list(@RequestParam @Valid @NotNull Integer pageNum,
                                              @RequestParam @Valid @NotNull Integer pageSize) {
        return R.success(growthChangeHistoryService.getGrowthChangeHistories(pageNum, pageSize));
    }
}
