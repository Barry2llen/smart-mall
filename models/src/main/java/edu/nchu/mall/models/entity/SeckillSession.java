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
@TableName("sms_seckill_session")
@Schema(description = "秒杀活动场次")
public class SeckillSession {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("name")
    @Schema(description = "场次名称")
    private String name;

    @TableField("start_time")
    @Schema(description = "每日开始时间")
    private LocalDateTime startTime;

    @TableField("end_time")
    @Schema(description = "每日结束时间")
    private LocalDateTime endTime;

    @TableField("status")
    @Schema(description = "启用状态")
    private Integer status;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
