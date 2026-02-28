package edu.nchu.mall.models.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wms_ware_order_task")
@Schema(description = "库存工作单")
public class WareOrderTask {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @JsonIgnore
    @TableField("order_id")
    @Schema(description = "order_id")
    private Long orderId;

    @TableField("order_sn")
    @Schema(description = "order_sn")
    private String orderSn;

    @TableField("consignee")
    @Schema(description = "收货人")
    private String consignee;

    @TableField("consignee_tel")
    @Schema(description = "收货人电话")
    private String consigneeTel;

    @TableField("delivery_address")
    @Schema(description = "配送地址")
    private String deliveryAddress;

    @TableField("order_comment")
    @Schema(description = "订单备注")
    private String orderComment;

    @TableField("payment_way")
    @Schema(description = "付款方式【 1:在线付款 2:货到付款】")
    private Integer paymentWay;

    @TableField("task_status")
    @Schema(description = "任务状态")
    private Integer taskStatus;

    @TableField("order_body")
    @Schema(description = "订单描述")
    private String orderBody;

    @TableField("tracking_no")
    @Schema(description = "物流单号")
    private String trackingNo;

    @TableField("create_time")
    @Schema(description = "create_time")
    private LocalDateTime createTime;

    @TableField("ware_id")
    @Schema(description = "仓库id")
    private Long wareId;

    @TableField("task_comment")
    @Schema(description = "工作单备注")
    private String taskComment;
}
