package edu.nchu.mall.services.order.controller;

import edu.nchu.mall.models.entity.OrderReturnApply;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.order.service.OrderReturnApplyService;
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

@Tag(name = "OrderReturnApply")
@Slf4j
@RestController
@RequestMapping("/order-return-applies")
public class OrderReturnApplyController {

    @Autowired
    OrderReturnApplyService orderReturnApplyService;

    @Parameters(@Parameter(name = "sid", description = "退货申请ID"))
    @Operation(summary = "获取退货申请")
    @GetMapping("/{sid}")
    public R<?> getReturnApply(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        OrderReturnApply apply = orderReturnApplyService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", apply);
    }

    @Parameters({
            @Parameter(name = "sid", description = "退货申请ID"),
            @Parameter(name = "apply", description = "需要更新的退货申请")
    })
    @Operation(summary = "更新退货申请")
    @PutMapping("/{sid}")
    public R<?> updateReturnApply(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                                  @RequestBody OrderReturnApply apply) throws NumberFormatException {
        apply.setId(Long.parseLong(sid));
        boolean res = orderReturnApplyService.updateById(apply);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "apply", description = "新退货申请"))
    @Operation(summary = "创建退货申请")
    @PostMapping
    public R<?> createReturnApply(@RequestBody OrderReturnApply apply) {
        apply.setId(null);
        boolean res = orderReturnApplyService.save(apply);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "退货申请ID"))
    @Operation(summary = "删除退货申请")
    @DeleteMapping("/{sid}")
    public R<?> deleteReturnApply(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        boolean res = orderReturnApplyService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
