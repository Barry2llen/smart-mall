package edu.nchu.mall.services.ware.controller;

import edu.nchu.mall.models.entity.Purchase;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.ware.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Purchase")
@Slf4j
@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService;

    @Parameters(@Parameter(name = "sid", description = "Purchase主键"))
    @Operation(summary = "获取Purchase详情")
    @GetMapping("/{sid}")
    public R<?> getPurchase(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        Purchase data = purchaseService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "Purchase主键"),
            @Parameter(name = "body", description = "更新后的Purchase")
    })
    @Operation(summary = "更新Purchase")
    @PutMapping("/{sid}")
    public R<?> updatePurchase(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody Purchase body) {
        body.setId(Long.parseLong(sid));
        boolean res = purchaseService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的Purchase"))
    @Operation(summary = "创建Purchase")
    @PostMapping
    public R<?> createPurchase(@RequestBody Purchase body) {
        body.setId(null);
        boolean res = purchaseService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "Purchase主键"))
    @Operation(summary = "删除Purchase")
    @DeleteMapping("/{sid}")
    public R<?> deletePurchase(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = purchaseService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
