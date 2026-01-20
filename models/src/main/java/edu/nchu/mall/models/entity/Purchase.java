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
@TableName("wms_purchase")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("purchase")
    @Schema(description = "采购信息")
public class Purchase {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "采购单id")
    private Long id;

    @TableField("assignee_id")
    @Schema(description = "采购人id")
    private Long assigneeId;

    @TableField("assignee_name")
    @Schema(description = "采购人名")
    private String assigneeName;

    @TableField("phone")
    @Schema(description = "联系方式")
    private String phone;

    @TableField("priority")
    @Schema(description = "优先级")
    private Integer priority;

    @TableField("status")
    @Schema(description = "状态")
    private Integer status;

    @TableField("ware_id")
    @Schema(description = "仓库id")
    private Long wareId;

    @TableField("amount")
    @Schema(description = "总金额")
    private BigDecimal amount;

    @TableField("create_time")
    @Schema(description = "创建日期")
    private LocalDateTime createTime;

    @TableField("update_time")
    @Schema(description = "更新日期")
    private LocalDateTime updateTime;
}
