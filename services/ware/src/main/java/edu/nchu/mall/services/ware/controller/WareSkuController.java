package edu.nchu.mall.services.ware.controller;

import edu.nchu.mall.models.annotation.NotNullCollection;
import edu.nchu.mall.models.entity.WareSku;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.models.vo.SkuStockVO;
import edu.nchu.mall.services.ware.service.WareSkuService;
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

@Tag(name = "sku库存")
@Slf4j
@RestController
@RequestMapping("/ware-skus")
public class WareSkuController {

    @Autowired
    WareSkuService wareSkuService;

    @Parameters(@Parameter(name = "sid", description = "sku库存主键"))
    @Operation(summary = "获取sku库存详情")
    @GetMapping("/{sid}")
    public R<WareSku> getWareSku(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        WareSku data = wareSkuService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "sku库存主键"),
            @Parameter(name = "body", description = "更新后的sku库存")
    })
    @Operation(summary = "更新sku库存")
    @PutMapping("/{sid}")
    public R<?> updateWareSku(@RequestBody @Validated(Groups.Update.class) WareSku body) {
        boolean res = wareSkuService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的sku库存"))
    @Operation(summary = "创建sku库存")
    @PostMapping
    public R<?> createWareSku(@RequestBody @Validated(Groups.Create.class) WareSku body) {
        boolean res = wareSkuService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "sku库存主键"))
    @Operation(summary = "删除sku库存")
    @DeleteMapping("/{sid}")
    public R<?> deleteWareSku(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = wareSkuService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }


    @Parameters({
            @Parameter(name = "skuIds", description = "要查询的skuId列表")
    })
    @Operation(summary = "获取sku库存数量")
    @PostMapping("/stocks")
    public R<List<SkuStockVO>> getStocksBySkuIds(@RequestBody @Valid @NotNullCollection List<Long> skuIds) {
        return R.success(wareSkuService.getStocksBySkuIds(skuIds));
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量"),
            @Parameter(name = "wareKey", description = "仓库关键字（可选）（仓库id）"),
            @Parameter(name = "skuKey", description = "sku关键字（可选）（sku名或id）")
    })
    @Operation(summary = "获取sku库存")
    @GetMapping("/list")
    public R<List<WareSku>> list(@RequestParam Integer pageNum, @RequestParam Integer pageSize,
                                 @RequestParam(required = false) String wareKey, @RequestParam(required = false) String skuKey){
        return R.success(wareSkuService.list(pageNum, pageSize, wareKey, skuKey));
    }
}
