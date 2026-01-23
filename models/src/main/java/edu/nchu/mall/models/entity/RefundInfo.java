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

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("oms_refund_info")
@Schema(description = "退款信息")
public class RefundInfo {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("order_return_id")
    @Schema(description = "退款的订单")
    private Long orderReturnId;

    @TableField("refund")
    @Schema(description = "退款金额")
    private BigDecimal refund;

    @TableField("refund_sn")
    @Schema(description = "退款交易流水号")
    private String refundSn;

    @TableField("refund_status")
    @Schema(description = "退款状态")
    private Integer refundStatus;

    @TableField("refund_channel")
    @Schema(description = "退款渠道[1-支付宝，2-微信，3-银联，4-汇款]")
    private Integer refundChannel;

    @TableField("refund_content")
    @Schema(description = "refund_content")
    private String refundContent;
}
