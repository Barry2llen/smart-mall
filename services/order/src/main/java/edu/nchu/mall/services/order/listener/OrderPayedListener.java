package edu.nchu.mall.services.order.listener;

import edu.nchu.mall.models.model.Try;
import edu.nchu.mall.services.order.config.AlipayTemplate;
import edu.nchu.mall.services.order.service.OrderService;
import edu.nchu.mall.services.order.vo.PayAsyncVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("payed")
public class OrderPayedListener {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    @PostMapping("/alipay")
    public ResponseEntity<String> alipaySuccess(PayAsyncVo vo, HttpServletRequest request) {

        Try<Boolean> validateTry = Try.of(alipayTemplate::validateSign, request);
        if (validateTry.failed() || !validateTry.getValue()) {
            log.warn("支付宝异步通知签名验证失败，参数：{}，request: {}", vo, request, validateTry.getEx());
            return ResponseEntity.status(OrderService.PayStatus.INVALID_SIGN.getStatus()).build();
        }

        Try<OrderService.PayStatus> payTry = Try.of(orderService::handleAlipayAsync, vo);
        if (payTry.failed()) {
            log.error("处理支付宝异步通知失败，参数：{}，request: {}", vo, request, payTry.getEx());
            return ResponseEntity.status(OrderService.PayStatus.ERROR.getStatus()).body(OrderService.PayStatus.ERROR.getMessage());
        }

        var status = payTry.getValue();
        if (payTry.getValue() != OrderService.PayStatus.SUCCESS) {
            log.error("处理支付宝异步通知失败，参数：{}，request: {}, status: {}", vo, request, status);
            return ResponseEntity.status(status.getStatus()).body(status.getMessage());
        }

        return ResponseEntity.status(status.getStatus()).body(status.getMessage());
    }
}
