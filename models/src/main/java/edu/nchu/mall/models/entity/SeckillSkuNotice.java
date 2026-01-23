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
@TableName("sms_seckill_sku_notice")
@Schema(description = "秒杀商品通知订阅")
public class SeckillSkuNotice {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("member_id")
    @Schema(description = "member_id")
    private Long memberId;

    @TableField("sku_id")
    @Schema(description = "sku_id")
    private Long skuId;

    @TableField("session_id")
    @Schema(description = "活动场次id")
    private Long sessionId;

    @TableField("subcribe_time")
    @Schema(description = "订阅时间")
    private LocalDateTime subcribeTime;

    @TableField("send_time")
    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @TableField("notice_type")
    @Schema(description = "通知方式[0-短信，1-邮件]")
    private Integer noticeType;
}
