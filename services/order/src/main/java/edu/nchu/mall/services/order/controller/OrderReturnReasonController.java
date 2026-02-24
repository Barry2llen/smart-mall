package edu.nchu.mall.services.order.controller;

import edu.nchu.mall.models.entity.OrderReturnReason;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.order.service.OrderReturnReasonService;
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

@Tag(name = "OrderReturnReason")
@Slf4j
@RestController
@RequestMapping("/order-return-reasons")
public class OrderReturnReasonController {

    @Autowired
    OrderReturnReasonService orderReturnReasonService;

    @Parameters(@Parameter(name = "sid", description = "退货原因ID"))
    @Operation(summary = "获取退货原因")
    @GetMapping("/{sid}")
    public R<?> getReason(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        OrderReturnReason reason = orderReturnReasonService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", reason);
    }

    @Parameters({
            @Parameter(name = "sid", description = "退货原因ID"),
            @Parameter(name = "reason", description = "需要更新的退货原因")
    })
    @Operation(summary = "更新退货原因")
    @PutMapping("/{sid}")
    public R<?> updateReason(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                             @RequestBody OrderReturnReason reason) throws NumberFormatException {
        reason.setId(Long.parseLong(sid));
        boolean res = orderReturnReasonService.updateById(reason);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "reason", description = "新退货原因"))
    @Operation(summary = "创建退货原因")
    @PostMapping
    public R<?> createReason(@RequestBody OrderReturnReason reason) {
        reason.setId(null);
        boolean res = orderReturnReasonService.save(reason);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "退货原因ID"))
    @Operation(summary = "删除退货原因")
    @DeleteMapping("/{sid}")
    public R<?> deleteReason(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        boolean res = orderReturnReasonService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
