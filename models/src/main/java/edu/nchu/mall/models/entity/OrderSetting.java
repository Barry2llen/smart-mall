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

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("oms_order_setting")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("os")
@Schema(description = "订单配置信息")
public class OrderSetting {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("flash_order_overtime")
    @Schema(description = "秒杀订单超时关闭时间(分)")
    private Integer flashOrderOvertime;

    @TableField("normal_order_overtime")
    @Schema(description = "正常订单超时时间(分)")
    private Integer normalOrderOvertime;

    @TableField("confirm_overtime")
    @Schema(description = "发货后自动确认收货时间（天）")
    private Integer confirmOvertime;

    @TableField("finish_overtime")
    @Schema(description = "自动完成交易时间，不能申请退货（天）")
    private Integer finishOvertime;

    @TableField("comment_overtime")
    @Schema(description = "订单完成后自动好评时间（天）")
    private Integer commentOvertime;

    @TableField("member_level")
    @Schema(description = "会员等级【0-不限会员等级，全部通用；其他-对应的其他会员等级】")
    private Integer memberLevel;
}
