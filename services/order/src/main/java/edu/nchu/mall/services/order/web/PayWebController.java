package edu.nchu.mall.services.order.web;

import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.models.annotation.bind.UserId;
import edu.nchu.mall.models.vo.PayVo;
import edu.nchu.mall.services.order.config.AlipayTemplate;
import edu.nchu.mall.services.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "支付")
@RestController
@RequestMapping("/public/pay")
public class PayWebController {
    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    @Parameters(@Parameter(name = "orderSn", description = "订单编号", required = true))
    @Operation(summary = "支付订单")
    @GetMapping(value = "/{orderSn}", produces = "text/html")
    public String pay(@UserId Long userId, @PathVariable String orderSn) {
        try {
            PayVo payVo = orderService.getOrderPay(userId, orderSn);
            return alipayTemplate.pay(payVo);
        } catch (Exception e) {
            throw new CustomException("获取支付信息失败: " + e.getMessage());
        }
    }
}
