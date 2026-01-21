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
@TableName("ums_growth_change_history")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("growthChangeHistory")
@Schema(description = "成长值变化历史记录")
public class GrowthChangeHistory {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("member_id")
    @Schema(description = "member_id")
    private Long memberId;

    @TableField("create_time")
    @Schema(description = "create_time")
    private LocalDateTime createTime;

    @TableField("change_count")
    @Schema(description = "改变的值（正负计数）")
    private Integer changeCount;

    @TableField("note")
    @Schema(description = "备注")
    private String note;

    @TableField("source_type")
    @Schema(description = "积分来源[0-购物，1-管理员修改]")
    private Integer sourceType;
}
