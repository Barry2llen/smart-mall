package edu.nchu.mall.services.order.controller;

import edu.nchu.mall.models.entity.PaymentInfo;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.order.service.PaymentInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PaymentInfo")
@Slf4j
@RestController
@RequestMapping("/payment-infos")
public class PaymentInfoController {

    @Autowired
    PaymentInfoService paymentInfoService;

    @Parameters(@Parameter(name = "sid", description = "支付信息ID"))
    @Operation(summary = "获取支付信息")
    @GetMapping("/{sid}")
    public R<?> getPaymentInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        PaymentInfo info = paymentInfoService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", info);
    }

    @Parameters({
            @Parameter(name = "sid", description = "支付信息ID"),
            @Parameter(name = "info", description = "需要更新的支付信息")
    })
    @Operation(summary = "更新支付信息")
    @PutMapping("/{sid}")
    public R<?> updatePaymentInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                                  @RequestBody PaymentInfo info) throws NumberFormatException {
        info.setId(Long.parseLong(sid));
        boolean res = paymentInfoService.updateById(info);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "info", description = "新支付信息"))
    @Operation(summary = "创建支付信息")
    @PostMapping
    public R<?> createPaymentInfo(@RequestBody PaymentInfo info) {
        info.setId(null);
        boolean res = paymentInfoService.save(info);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "支付信息ID"))
    @Operation(summary = "删除支付信息")
    @DeleteMapping("/{sid}")
    public R<?> deletePaymentInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException {
        boolean res = paymentInfoService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}
