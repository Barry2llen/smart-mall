package edu.nchu.mall.services.order.controller;

import edu.nchu.mall.models.entity.OrderItem;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.order.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OrderItem")
@Slf4j
@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    @Autowired
    OrderItemService orderItemService;

    @Parameters(@Parameter(name = "sid", description = "订单项ID"))
    @Operation(summary = "获取订单项")
    @GetMapping("/{sid}")
    public R<?> getOrderItem(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        OrderItem item = orderItemService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", item);
    }

    @Parameters({
            @Parameter(name = "sid", description = "订单项ID"),
            @Parameter(name = "orderItem", description = "需要更新的订单项信息")
    })
    @Operation(summary = "更新订单项")
    @PutMapping("/{sid}")
    public R<?> updateOrderItem(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                                @RequestBody OrderItem orderItem) throws NumberFormatException {
        orderItem.setId(Long.parseLong(sid));
        boolean res = orderItemService.updateById(orderItem);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "orderItem", description = "新订单项信息"))
    @Operation(summary = "创建订单项")
    @PostMapping
    public R<?> createOrderItem(@RequestBody OrderItem orderItem) {
        orderItem.setId(null);
        boolean res = orderItemService.save(orderItem);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "订单项ID"))
    @Operation(summary = "删除订单项")
    @DeleteMapping("/{sid}")
    public R<?> deleteOrderItem(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        boolean res = orderItemService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
