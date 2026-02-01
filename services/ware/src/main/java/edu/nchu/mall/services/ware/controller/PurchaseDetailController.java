package edu.nchu.mall.services.ware.controller;

import edu.nchu.mall.models.entity.PurchaseDetail;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.services.ware.service.PurchaseDetailService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "采购需求")
@Slf4j
@RestController
@RequestMapping("/purchase-details")
public class PurchaseDetailController {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Parameters(@Parameter(name = "sid", description = "采购需求主键"))
    @Operation(summary = "获取采购需求")
    @GetMapping("/{sid}")
    public R<PurchaseDetail> getPurchaseDetail(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        PurchaseDetail data = purchaseDetailService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "采购需求主键"),
            @Parameter(name = "body", description = "更新后的采购需求")
    })
    @Operation(summary = "更新采购需求")
    @PutMapping("/{sid}")
    public R<?> updatePurchaseDetail(@RequestBody @Validated(Groups.Update.class) PurchaseDetail body) {
        boolean res = purchaseDetailService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的采购需求"))
    @Operation(summary = "创建PurchaseDetail")
    @PostMapping
    public R<?> createPurchaseDetail(@RequestBody @Validated(Groups.Create.class) PurchaseDetail body) {
        boolean res = purchaseDetailService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "采购需求主键"))
    @Operation(summary = "删除采购需求")
    @DeleteMapping("/{sid}")
    public R<?> deletePurchaseDetail(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = purchaseDetailService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量"),
            @Parameter(name = "status", description = "状态（可选）"),
            @Parameter(name = "wareId", description = "仓库id（可选）"),
            @Parameter(name = "key", description = "搜索关键字（可选）（purchase_id或sku_id）")
    })
    @Operation(summary = "分页获取采购需求列表")
    @GetMapping("/list")
    public R<List<PurchaseDetail>> list(@RequestParam Integer pageNum, @RequestParam Integer pageSize,
                                        @RequestParam(required = false) Integer status,
                                        @RequestParam(required = false) Long wareId,
                                        @RequestParam(required = false) String key) {
        return R.success(purchaseDetailService.list(pageNum, pageSize, status, wareId, key));
    }
}
