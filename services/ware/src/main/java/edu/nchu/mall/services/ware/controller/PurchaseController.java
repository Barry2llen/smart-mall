package edu.nchu.mall.services.ware.controller;

import edu.nchu.mall.models.dto.PurchaseMergeDTO;
import edu.nchu.mall.models.entity.Purchase;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.services.ware.service.PurchaseService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "采购单")
@Slf4j
@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService;

    @Parameters(@Parameter(name = "sid", description = "采购单主键"))
    @Operation(summary = "获取采购单详情")
    @GetMapping("/{sid}")
    public R<Purchase> getPurchase(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        Purchase data = purchaseService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "采购单主键"),
            @Parameter(name = "body", description = "更新后的采购单")
    })
    @Operation(summary = "更新采购单")
    @PutMapping("/{sid}")
    public R<?> updatePurchase(@RequestBody @Validated(Groups.Update.class) Purchase body) {
        boolean res = purchaseService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的采购单"))
    @Operation(summary = "创建采购单")
    @PostMapping
    public R<?> createPurchase(@RequestBody @Validated(Groups.Create.class) Purchase body) {
        body.setId(null);
        boolean res = purchaseService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "采购单主键"))
    @Operation(summary = "删除采购单")
    @DeleteMapping("/{sid}")
    public R<?> deletePurchase(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = purchaseService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量")
    })
    @Operation(summary = "获取采购单列表")
    @GetMapping("/list")
    public R<List<Purchase>> list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return new R<>(RCT.SUCCESS, "success", purchaseService.list(pageNum, pageSize));
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量")
    })
    @Operation(summary = "获取未分配的采购单列表")
    @GetMapping("/list/unassigned")
    public R<List<Purchase>> getUnassignedPurchases(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return new R<>(RCT.SUCCESS, "success", purchaseService.listUnassignedPurchases(pageNum, pageSize));
    }

    @Parameters(@Parameter(name = "dto", description = "合并的信息"))
    @Operation(summary = "合并采购需求")
    @PostMapping("/merge")
    public R<?> mergePurchase(@RequestBody @Valid PurchaseMergeDTO dto) {
        return R.success(purchaseService.merge(dto));
    }
}
