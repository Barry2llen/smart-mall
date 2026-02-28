package edu.nchu.mall.services.order.controller;

import edu.nchu.mall.models.entity.Order;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.order.service.OrderService;
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

@Tag(name = "Order")
@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Parameters(
            @Parameter(name = "id", description = "订单ID")
    )
    @Operation(summary = "获取订单信息")
    @GetMapping("/{id}")
    public R<?> getOrder(@PathVariable Long id) {
        Order order = orderService.getById(id);
        return order == null ? R.fail("order not found") : R.success(order);
    }

    @Parameters(
            @Parameter(name = "id", description = "订单ID")
    )
    @Operation(summary = "获取订单信息")
    @GetMapping("/sn/{sn}")
    public R<?> getOrder(@PathVariable String sn) {
        Order order = orderService.getBySn(sn);
        return order == null ? R.fail("order not found") : R.success(order);
    }

    @Parameters({
            @Parameter(name = "sid", description = "订单ID"),
            @Parameter(name = "order", description = "需要更新的订单信息")
    })
    @Operation(summary = "更新订单信息")
    @PutMapping("/{sid}")
    public R<?> updateOrder(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                             @RequestBody Order order) throws NumberFormatException {
        order.setId(Long.parseLong(sid));
        boolean res = orderService.updateById(order);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters({
            @Parameter(name = "order", description = "新订单信息")
    })
    @Operation(summary = "创建订单")
    @PostMapping
    public R<?> createOrder(@RequestBody Order order) {
        order.setId(null);
        boolean res = orderService.save(order);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(
            @Parameter(name = "sid", description = "订单ID")
    )
    @Operation(summary = "删除订单")
    @DeleteMapping("/{sid}")
    public R<?> removeOrder(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        boolean res = orderService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
