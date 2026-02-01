package edu.nchu.mall.services.ware.controller;

import edu.nchu.mall.models.entity.WareOrderTaskDetail;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.ware.service.WareOrderTaskDetailService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Hidden
@Tag(name = "WareOrderTaskDetail")
@Slf4j
@RestController
@RequestMapping("/ware-order-task-details")
public class WareOrderTaskDetailController {

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Parameters(@Parameter(name = "sid", description = "WareOrderTaskDetail主键"))
    @Operation(summary = "获取WareOrderTaskDetail详情")
    @GetMapping("/{sid}")
    public R<?> getWareOrderTaskDetail(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        WareOrderTaskDetail data = wareOrderTaskDetailService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "WareOrderTaskDetail主键"),
            @Parameter(name = "body", description = "更新后的WareOrderTaskDetail")
    })
    @Operation(summary = "更新WareOrderTaskDetail")
    @PutMapping("/{sid}")
    public R<?> updateWareOrderTaskDetail(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody WareOrderTaskDetail body) {
        body.setId(Long.parseLong(sid));
        boolean res = wareOrderTaskDetailService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的WareOrderTaskDetail"))
    @Operation(summary = "创建WareOrderTaskDetail")
    @PostMapping
    public R<?> createWareOrderTaskDetail(@RequestBody WareOrderTaskDetail body) {
        body.setId(null);
        boolean res = wareOrderTaskDetailService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "WareOrderTaskDetail主键"))
    @Operation(summary = "删除WareOrderTaskDetail")
    @DeleteMapping("/{sid}")
    public R<?> deleteWareOrderTaskDetail(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = wareOrderTaskDetailService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
