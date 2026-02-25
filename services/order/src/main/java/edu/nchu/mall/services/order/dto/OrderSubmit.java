package edu.nchu.mall.services.order.dto;

import edu.nchu.mall.models.enums.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

@Data
@Schema(description = "提交订单信息")
public class OrderSubmit {
    @NotNull
    @Schema(description = "收货地址id")
    private Long addrId;
    @Schema(description = "支付方式")
    @NotNull
    private Payment payment;
    @Schema(description = "防重令牌")
    @NotNull
    private String token;
    @NotNull
    @Schema(description = "订单确认页的总价（用于验价）")
    private BigDecimal price;
    @Nullable
    @Schema(description = "备注")
    private String notes;
}
