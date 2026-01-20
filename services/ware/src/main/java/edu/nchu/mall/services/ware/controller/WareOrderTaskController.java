package edu.nchu.mall.services.ware.controller;

import edu.nchu.mall.models.entity.WareOrderTask;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.ware.service.WareOrderTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WareOrderTask")
@Slf4j
@RestController
@RequestMapping("/ware-order-tasks")
public class WareOrderTaskController {

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Parameters(@Parameter(name = "sid", description = "WareOrderTask主键"))
    @Operation(summary = "获取WareOrderTask详情")
    @GetMapping("/{sid}")
    public R<?> getWareOrderTask(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        WareOrderTask data = wareOrderTaskService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "WareOrderTask主键"),
            @Parameter(name = "body", description = "更新后的WareOrderTask")
    })
    @Operation(summary = "更新WareOrderTask")
    @PutMapping("/{sid}")
    public R<?> updateWareOrderTask(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody WareOrderTask body) {
        body.setId(Long.parseLong(sid));
        boolean res = wareOrderTaskService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的WareOrderTask"))
    @Operation(summary = "创建WareOrderTask")
    @PostMapping
    public R<?> createWareOrderTask(@RequestBody WareOrderTask body) {
        body.setId(null);
        boolean res = wareOrderTaskService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "WareOrderTask主键"))
    @Operation(summary = "删除WareOrderTask")
    @DeleteMapping("/{sid}")
    public R<?> deleteWareOrderTask(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = wareOrderTaskService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
