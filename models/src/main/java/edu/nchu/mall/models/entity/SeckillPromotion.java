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
@TableName("sms_seckill_promotion")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("seckillPromotion")
@Schema(description = "秒杀活动")
public class SeckillPromotion {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("title")
    @Schema(description = "活动标题")
    private String title;

    @TableField("start_time")
    @Schema(description = "开始日期")
    private LocalDateTime startTime;

    @TableField("end_time")
    @Schema(description = "结束日期")
    private LocalDateTime endTime;

    @TableField("status")
    @Schema(description = "上下线状态")
    private Integer status;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("user_id")
    @Schema(description = "创建人")
    private Long userId;
}
