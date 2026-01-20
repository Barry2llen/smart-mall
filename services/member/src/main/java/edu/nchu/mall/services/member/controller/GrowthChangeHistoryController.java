package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.entity.GrowthChangeHistory;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.member.service.GrowthChangeHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "GrowthChangeHistory")
@Slf4j
@RestController
@RequestMapping("/growth-change-histories")
public class GrowthChangeHistoryController {

    @Autowired
    GrowthChangeHistoryService growthChangeHistoryService;

    @Parameters(@Parameter(name = "sid", description = "GrowthChangeHistory主键"))
    @Operation(summary = "获取GrowthChangeHistory详情")
    @GetMapping("/{sid}")
    public R<?> getGrowthChangeHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        GrowthChangeHistory data = growthChangeHistoryService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "GrowthChangeHistory主键"),
            @Parameter(name = "body", description = "更新后的GrowthChangeHistory")
    })
    @Operation(summary = "更新GrowthChangeHistory")
    @PutMapping("/{sid}")
    public R<?> updateGrowthChangeHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody GrowthChangeHistory body) {
        body.setId(Long.parseLong(sid));
        boolean res = growthChangeHistoryService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的GrowthChangeHistory"))
    @Operation(summary = "创建GrowthChangeHistory")
    @PostMapping
    public R<?> createGrowthChangeHistory(@RequestBody GrowthChangeHistory body) {
        body.setId(null);
        boolean res = growthChangeHistoryService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "GrowthChangeHistory主键"))
    @Operation(summary = "删除GrowthChangeHistory")
    @DeleteMapping("/{sid}")
    public R<?> deleteGrowthChangeHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = growthChangeHistoryService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
