package edu.nchu.mall.services.order.controller;

import edu.nchu.mall.models.entity.OrderOperateHistory;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.order.service.OrderOperateHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OrderOperateHistory")
@Slf4j
@RestController
@RequestMapping("/order-operate-histories")
public class OrderOperateHistoryController {

    @Autowired
    OrderOperateHistoryService orderOperateHistoryService;

    @Parameters(@Parameter(name = "sid", description = "操作记录ID"))
    @Operation(summary = "获取订单操作记录")
    @GetMapping("/{sid}")
    public R<?> getHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        OrderOperateHistory history = orderOperateHistoryService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", history);
    }

    @Parameters({
            @Parameter(name = "sid", description = "操作记录ID"),
            @Parameter(name = "history", description = "需要更新的操作记录")
    })
    @Operation(summary = "更新订单操作记录")
    @PutMapping("/{sid}")
    public R<?> updateHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                              @RequestBody OrderOperateHistory history) throws NumberFormatException {
        history.setId(Long.parseLong(sid));
        boolean res = orderOperateHistoryService.updateById(history);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "history", description = "新操作记录"))
    @Operation(summary = "创建订单操作记录")
    @PostMapping
    public R<?> createHistory(@RequestBody OrderOperateHistory history) {
        history.setId(null);
        boolean res = orderOperateHistoryService.save(history);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "操作记录ID"))
    @Operation(summary = "删除订单操作记录")
    @DeleteMapping("/{sid}")
    public R<?> deleteHistory(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        boolean res = orderOperateHistoryService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
