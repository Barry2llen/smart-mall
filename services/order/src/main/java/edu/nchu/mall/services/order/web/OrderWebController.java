package edu.nchu.mall.services.order.web;

import edu.nchu.mall.models.annotation.bind.UserId;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.services.order.dto.OrderSubmit;
import edu.nchu.mall.services.order.service.OrderService;
import edu.nchu.mall.services.order.vo.OrderConfirm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单操作")
@RestController
@RequestMapping("public")
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @Parameters
    @Operation(description = "确认订单信息（购物车结算）")
    @GetMapping("/confirm")
    public R<OrderConfirm> confirmOrder(@UserId Long memberId) {
        return R.success(orderService.confirmOrder(memberId));
    }

    @Parameters(@Parameter(name = "orderSubmit", description = "订单提交信息"))
    @Operation(description = "提交订单")
    @PostMapping("/submit")
    public R<?> submitOrder(@UserId Long memberId, @RequestBody @Valid OrderSubmit orderSubmit) {
        OrderService.OrderSubmitStatus res = orderService.submitOrder(memberId, orderSubmit);
        if (res == OrderService.OrderSubmitStatus.OK) {
            return R.success(null);
        }
        return R.fail(res.getMessage());
    }

}
