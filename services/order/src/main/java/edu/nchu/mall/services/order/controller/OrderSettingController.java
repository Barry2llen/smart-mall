package edu.nchu.mall.services.order.controller;

import edu.nchu.mall.models.entity.OrderSetting;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.order.service.OrderSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OrderSetting")
@Slf4j
@RestController
@RequestMapping("/order-settings")
public class OrderSettingController {

    @Autowired
    OrderSettingService orderSettingService;

    @Parameters(@Parameter(name = "sid", description = "订单设置ID"))
    @Operation(summary = "获取订单设置")
    @GetMapping("/{sid}")
    public R<?> getSetting(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        OrderSetting setting = orderSettingService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", setting);
    }

    @Parameters({
            @Parameter(name = "sid", description = "订单设置ID"),
            @Parameter(name = "setting", description = "需要更新的订单设置")
    })
    @Operation(summary = "更新订单设置")
    @PutMapping("/{sid}")
    public R<?> updateSetting(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                              @RequestBody OrderSetting setting) throws NumberFormatException {
        setting.setId(Long.parseLong(sid));
        boolean res = orderSettingService.updateById(setting);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "setting", description = "新订单设置"))
    @Operation(summary = "创建订单设置")
    @PostMapping
    public R<?> createSetting(@RequestBody OrderSetting setting) {
        setting.setId(null);
        boolean res = orderSettingService.save(setting);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "订单设置ID"))
    @Operation(summary = "删除订单设置")
    @DeleteMapping("/{sid}")
    public R<?> deleteSetting(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        boolean res = orderSettingService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
