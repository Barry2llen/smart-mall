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

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("oms_order_operate_history")
@Schema(description = "订单操作历史记录")
public class OrderOperateHistory {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("order_id")
    @Schema(description = "订单id")
    private Long orderId;

    @TableField("operate_man")
    @Schema(description = "操作人[用户；系统；后台管理员]")
    private String operateMan;

    @TableField("create_time")
    @Schema(description = "操作时间")
    private LocalDateTime createTime;

    @TableField("order_status")
    @Schema(description = "订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】")
    private Integer orderStatus;

    @TableField("note")
    @Schema(description = "备注")
    private String note;
}
