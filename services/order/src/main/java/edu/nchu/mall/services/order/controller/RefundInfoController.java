package edu.nchu.mall.services.order.controller;

import edu.nchu.mall.models.entity.RefundInfo;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.order.service.RefundInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "RefundInfo")
@Slf4j
@RestController
@RequestMapping("/refund-infos")
public class RefundInfoController {

    @Autowired
    RefundInfoService refundInfoService;

    @Parameters(@Parameter(name = "sid", description = "退款信息ID"))
    @Operation(summary = "获取退款信息")
    @GetMapping("/{sid}")
    public R<?> getRefundInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        RefundInfo info = refundInfoService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", info);
    }

    @Parameters({
            @Parameter(name = "sid", description = "退款信息ID"),
            @Parameter(name = "info", description = "需要更新的退款信息")
    })
    @Operation(summary = "更新退款信息")
    @PutMapping("/{sid}")
    public R<?> updateRefundInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                                 @RequestBody RefundInfo info) throws NumberFormatException {
        info.setId(Long.parseLong(sid));
        boolean res = refundInfoService.updateById(info);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "info", description = "新退款信息"))
    @Operation(summary = "创建退款信息")
    @PostMapping
    public R<?> createRefundInfo(@RequestBody RefundInfo info) {
        info.setId(null);
        boolean res = refundInfoService.save(info);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "退款信息ID"))
    @Operation(summary = "删除退款信息")
    @DeleteMapping("/{sid}")
    public R<?> deleteRefundInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        boolean res = refundInfoService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
