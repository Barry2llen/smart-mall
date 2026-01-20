package edu.nchu.mall.services.ware.controller;

import edu.nchu.mall.models.entity.PurchaseDetail;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.ware.service.PurchaseDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PurchaseDetail")
@Slf4j
@RestController
@RequestMapping("/purchase-details")
public class PurchaseDetailController {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Parameters(@Parameter(name = "sid", description = "PurchaseDetail主键"))
    @Operation(summary = "获取PurchaseDetail详情")
    @GetMapping("/{sid}")
    public R<?> getPurchaseDetail(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        PurchaseDetail data = purchaseDetailService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "PurchaseDetail主键"),
            @Parameter(name = "body", description = "更新后的PurchaseDetail")
    })
    @Operation(summary = "更新PurchaseDetail")
    @PutMapping("/{sid}")
    public R<?> updatePurchaseDetail(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody PurchaseDetail body) {
        body.setId(Long.parseLong(sid));
        boolean res = purchaseDetailService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的PurchaseDetail"))
    @Operation(summary = "创建PurchaseDetail")
    @PostMapping
    public R<?> createPurchaseDetail(@RequestBody PurchaseDetail body) {
        body.setId(null);
        boolean res = purchaseDetailService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "PurchaseDetail主键"))
    @Operation(summary = "删除PurchaseDetail")
    @DeleteMapping("/{sid}")
    public R<?> deletePurchaseDetail(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = purchaseDetailService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
