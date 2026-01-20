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
@TableName("sms_coupon_history")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("couponHistory")
    @Schema(description = "优惠券领取历史记录")
public class CouponHistory {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("coupon_id")
    @Schema(description = "优惠券id")
    private Long couponId;

    @TableField("member_id")
    @Schema(description = "会员id")
    private Long memberId;

    @TableField("member_nick_name")
    @Schema(description = "会员名字")
    private String memberNickName;

    @TableField("get_type")
    @Schema(description = "获取方式[0->后台赠送；1->主动领取]")
    private Integer getType;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("use_type")
    @Schema(description = "使用状态[0->未使用；1->已使用；2->已过期]")
    private Integer useType;

    @TableField("use_time")
    @Schema(description = "使用时间")
    private LocalDateTime useTime;

    @TableField("order_id")
    @Schema(description = "订单id")
    private Long orderId;

    @TableField("order_sn")
    @Schema(description = "订单号")
    private Long orderSn;
}
