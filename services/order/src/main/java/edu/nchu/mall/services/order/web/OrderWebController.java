package edu.nchu.mall.services.order.web;

import edu.nchu.mall.models.annotation.bind.UserId;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.services.order.dto.OrderSubmit;
import edu.nchu.mall.services.order.service.OrderService;
import edu.nchu.mall.services.order.utils.OrderContext;
import edu.nchu.mall.services.order.vo.OrderConfirm;
import edu.nchu.mall.services.order.vo.OrderWithItems;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单操作")
@Validated
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

    @Parameters(@Parameter(name = "orderSubmit", description = "订单提交信息，返回订单sn", required = true))
    @Operation(description = "提交订单")
    @PostMapping("/submit")
    public R<String> submitOrder(@UserId Long memberId, @RequestBody @Valid OrderSubmit orderSubmit) {
        OrderService.OrderSubmitStatus res = orderService.submitOrder(memberId, orderSubmit);
        R<String> r = null;
        if (res == OrderService.OrderSubmitStatus.OK) {
            r = R.success(OrderContext.ORDER_SN.get());
        }
        else r = R.fail(res.getMessage());

        OrderContext.ORDER_SN.remove();

        return r;
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码（从1开始）", required = true),
            @Parameter(name = "pageSize", description = "每页数量", required = true),
            @Parameter(name = "status", description = "订单状态（0-5，可选）"),
            @Parameter(name = "keyword", description = "订单号前缀关键词（可选）")
    })
    @Operation(description = "分页查询当前用户订单列表（包含订单项）")
    @GetMapping("/orders")
    public R<List<OrderWithItems>> listOrder(@UserId Long memberId,
                                             @RequestParam @NotNull @Min(1) Integer pageNum,
                                             @RequestParam @NotNull @Min(1) Integer pageSize,
                                             @RequestParam(required = false) @Min(0) @Max(5) Integer status,
                                             @RequestParam(required = false) String keyword) {
        return R.success(orderService.listByMemberId(memberId, pageNum, pageSize, status, keyword));
    }

    @Parameters({
            @Parameter(name = "sn", description = "订单编号", required = true),
            @Parameter(name = "includeItems", description = "是否包含订单项，默认false")
    })
    @Operation(description = "根据订单编号查询当前用户单个订单信息")
    @GetMapping("/orders/{sn}")
    public R<OrderWithItems> getOrderBySn(@UserId Long memberId,
                                          @PathVariable @NotBlank String sn,
                                          @RequestParam(defaultValue = "false") boolean includeItems) {
        OrderWithItems order = orderService.getOrderBySn(memberId, sn, includeItems);
        return order == null ? R.fail("订单不存在") : R.success(order);
    }

}
