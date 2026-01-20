package edu.nchu.mall.models.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("oms_payment_info")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("pi")
    @Schema(description = "支付信息表")
public class PaymentInfo {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("order_sn")
    @Schema(description = "订单号（对外业务号）")
    private String orderSn;

    @TableField("order_id")
    @Schema(description = "订单id")
    private Long orderId;

    @TableField("alipay_trade_no")
    @Schema(description = "支付宝交易流水号")
    private String alipayTradeNo;

    @TableField("total_amount")
    @Schema(description = "支付总金额")
    private BigDecimal totalAmount;

    @TableField("subject")
    @Schema(description = "交易内容")
    private String subject;

    @TableField("payment_status")
    @Schema(description = "支付状态")
    private String paymentStatus;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("confirm_time")
    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;

    @TableField("callback_content")
    @Schema(description = "回调内容")
    private String callbackContent;

    @TableField("callback_time")
    @Schema(description = "回调时间")
    private LocalDateTime callbackTime;
}
