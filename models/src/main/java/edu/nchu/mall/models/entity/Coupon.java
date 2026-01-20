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
@TableName("sms_coupon")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("coupon")
    @Schema(description = "优惠券信息")
public class Coupon {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("coupon_type")
    @Schema(description = "优惠卷类型[0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券]")
    private Integer couponType;

    @TableField("coupon_img")
    @Schema(description = "优惠券图片")
    private String couponImg;

    @TableField("coupon_name")
    @Schema(description = "优惠卷名字")
    private String couponName;

    @TableField("num")
    @Schema(description = "数量")
    private Integer num;

    @TableField("amount")
    @Schema(description = "金额")
    private BigDecimal amount;

    @TableField("per_limit")
    @Schema(description = "每人限领张数")
    private Integer perLimit;

    @TableField("min_point")
    @Schema(description = "使用门槛")
    private BigDecimal minPoint;

    @TableField("start_time")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @TableField("end_time")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @TableField("use_type")
    @Schema(description = "使用类型[0->全场通用；1->指定分类；2->指定商品]")
    private Integer useType;

    @TableField("note")
    @Schema(description = "备注")
    private String note;

    @TableField("publish_count")
    @Schema(description = "发行数量")
    private Integer publishCount;

    @TableField("use_count")
    @Schema(description = "已使用数量")
    private Integer useCount;

    @TableField("receive_count")
    @Schema(description = "领取数量")
    private Integer receiveCount;

    @TableField("enable_start_time")
    @Schema(description = "可以领取的开始日期")
    private LocalDateTime enableStartTime;

    @TableField("enable_end_time")
    @Schema(description = "可以领取的结束日期")
    private LocalDateTime enableEndTime;

    @TableField("code")
    @Schema(description = "优惠码")
    private String code;

    @TableField("member_level")
    @Schema(description = "可以领取的会员等级[0->不限等级，其他-对应等级]")
    private Integer memberLevel;

    @TableField("publish")
    @Schema(description = "发布状态[0-未发布，1-已发布]")
    private Integer publish;
}
