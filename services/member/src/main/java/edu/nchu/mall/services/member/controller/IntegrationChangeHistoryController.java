package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.entity.IntegrationChangeHistory;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.member.service.IntegrationChangeHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "IntegrationChangeHistory")
@Slf4j
@RestController
@RequestMapping("/integration-change-histories")
public class IntegrationChangeHistoryController {

    @Autowired
    IntegrationChangeHistoryService integrationChangeHistoryService;

    @Parameters(@Parameter(name = "sid", description = "IntegrationChangeHistory主键"))
    @Operation(summary = "获取IntegrationChangeHistory详情")
    @GetMapping("/{sid}")
    public R<?> getIntegrationChangeHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        IntegrationChangeHistory data = integrationChangeHistoryService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "IntegrationChangeHistory主键"),
            @Parameter(name = "body", description = "更新后的IntegrationChangeHistory")
    })
    @Operation(summary = "更新IntegrationChangeHistory")
    @PutMapping("/{sid}")
    public R<?> updateIntegrationChangeHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody IntegrationChangeHistory body) {
        body.setId(Long.parseLong(sid));
        boolean res = integrationChangeHistoryService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的IntegrationChangeHistory"))
    @Operation(summary = "创建IntegrationChangeHistory")
    @PostMapping
    public R<?> createIntegrationChangeHistory(@RequestBody IntegrationChangeHistory body) {
        body.setId(null);
        boolean res = integrationChangeHistoryService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "IntegrationChangeHistory主键"))
    @Operation(summary = "删除IntegrationChangeHistory")
    @DeleteMapping("/{sid}")
    public R<?> deleteIntegrationChangeHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = integrationChangeHistoryService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
