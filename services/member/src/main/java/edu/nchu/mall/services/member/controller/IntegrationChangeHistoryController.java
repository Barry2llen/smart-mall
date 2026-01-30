package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.dto.IntegrationChangeHistoryDTO;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.models.vo.IntegrationChangeHistoryVO;
import edu.nchu.mall.services.member.service.IntegrationChangeHistoryService;
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

@Tag(name = "积分变更记录管理")
@Slf4j
@RestController
@RequestMapping("/integration-change-histories")
public class IntegrationChangeHistoryController {

    @Autowired
    IntegrationChangeHistoryService integrationChangeHistoryService;

    @Parameters(@Parameter(name = "sid", description = "积分变更记录主键"))
    @Operation(summary = "获取积分变更记录详情")
    @GetMapping("/{sid}")
    public R<IntegrationChangeHistoryVO> getIntegrationChangeHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        IntegrationChangeHistoryVO data = integrationChangeHistoryService.getIntegrationChangeHistoryById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "dto", description = "更新的积分变更记录信息")
    })
    @Operation(summary = "更新积分变更记录")
    @PutMapping
    public R<?> updateIntegrationChangeHistory(@RequestBody @Validated(Groups.Update.class) IntegrationChangeHistoryDTO dto) {
        boolean res = integrationChangeHistoryService.updateById(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "dto", description = "新增的积分变更记录"))
    @Operation(summary = "创建积分变更记录")
    @PostMapping
    public R<?> createIntegrationChangeHistory(@RequestBody @Validated(Groups.Create.class) IntegrationChangeHistoryDTO dto) {
        boolean res = integrationChangeHistoryService.save(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "积分变更记录主键"))
    @Operation(summary = "删除积分变更记录")
    @DeleteMapping("/{sid}")
    public R<?> deleteIntegrationChangeHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = integrationChangeHistoryService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量")
    })
    @Operation(summary = "获取积分变更记录列表")
    @GetMapping
    public R<List<IntegrationChangeHistoryVO>> list(@RequestParam @Valid @NotNull Integer pageNum,
                                                   @RequestParam @Valid @NotNull Integer pageSize) {
        return R.success(integrationChangeHistoryService.getIntegrationChangeHistories(pageNum, pageSize));
    }
}
