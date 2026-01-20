package edu.nchu.mall.services.ware.controller;

import edu.nchu.mall.models.entity.WareSku;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.ware.service.WareSkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WareSku")
@Slf4j
@RestController
@RequestMapping("/ware-skus")
public class WareSkuController {

    @Autowired
    WareSkuService wareSkuService;

    @Parameters(@Parameter(name = "sid", description = "WareSku主键"))
    @Operation(summary = "获取WareSku详情")
    @GetMapping("/{sid}")
    public R<?> getWareSku(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        WareSku data = wareSkuService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "WareSku主键"),
            @Parameter(name = "body", description = "更新后的WareSku")
    })
    @Operation(summary = "更新WareSku")
    @PutMapping("/{sid}")
    public R<?> updateWareSku(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody WareSku body) {
        body.setId(Long.parseLong(sid));
        boolean res = wareSkuService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的WareSku"))
    @Operation(summary = "创建WareSku")
    @PostMapping
    public R<?> createWareSku(@RequestBody WareSku body) {
        body.setId(null);
        boolean res = wareSkuService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "WareSku主键"))
    @Operation(summary = "删除WareSku")
    @DeleteMapping("/{sid}")
    public R<?> deleteWareSku(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = wareSkuService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
