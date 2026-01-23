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
@TableName("oms_order")
@Schema(description = "订单")
public class Order {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("member_id")
    @Schema(description = "member_id")
    private Long memberId;

    @TableField("order_sn")
    @Schema(description = "订单号")
    private String orderSn;

    @TableField("coupon_id")
    @Schema(description = "使用的优惠券")
    private Long couponId;

    @TableField("create_time")
    @Schema(description = "create_time")
    private LocalDateTime createTime;

    @TableField("member_username")
    @Schema(description = "用户名")
    private String memberUsername;

    @TableField("total_amount")
    @Schema(description = "订单总额")
    private BigDecimal totalAmount;

    @TableField("pay_amount")
    @Schema(description = "应付总额")
    private BigDecimal payAmount;

    @TableField("freight_amount")
    @Schema(description = "运费金额")
    private BigDecimal freightAmount;

    @TableField("promotion_amount")
    @Schema(description = "促销优化金额（促销价、满减、阶梯价）")
    private BigDecimal promotionAmount;

    @TableField("integration_amount")
    @Schema(description = "积分抵扣金额")
    private BigDecimal integrationAmount;

    @TableField("coupon_amount")
    @Schema(description = "优惠券抵扣金额")
    private BigDecimal couponAmount;

    @TableField("discount_amount")
    @Schema(description = "后台调整订单使用的折扣金额")
    private BigDecimal discountAmount;

    @TableField("pay_type")
    @Schema(description = "支付方式【1->支付宝；2->微信；3->银联； 4->货到付款；】")
    private Integer payType;

    @TableField("source_type")
    @Schema(description = "订单来源[0->PC订单；1->app订单]")
    private Integer sourceType;

    @TableField("status")
    @Schema(description = "订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】")
    private Integer status;

    @TableField("delivery_company")
    @Schema(description = "物流公司(配送方式)")
    private String deliveryCompany;

    @TableField("delivery_sn")
    @Schema(description = "物流单号")
    private String deliverySn;

    @TableField("auto_confirm_day")
    @Schema(description = "自动确认时间（天）")
    private Integer autoConfirmDay;

    @TableField("integration")
    @Schema(description = "可以获得的积分")
    private Integer integration;

    @TableField("growth")
    @Schema(description = "可以获得的成长值")
    private Integer growth;

    @TableField("bill_type")
    @Schema(description = "发票类型[0->不开发票；1->电子发票；2->纸质发票]")
    private Integer billType;

    @TableField("bill_header")
    @Schema(description = "发票抬头")
    private String billHeader;

    @TableField("bill_content")
    @Schema(description = "发票内容")
    private String billContent;

    @TableField("bill_receiver_phone")
    @Schema(description = "收票人电话")
    private String billReceiverPhone;

    @TableField("bill_receiver_email")
    @Schema(description = "收票人邮箱")
    private String billReceiverEmail;

    @TableField("receiver_name")
    @Schema(description = "收货人姓名")
    private String receiverName;

    @TableField("receiver_phone")
    @Schema(description = "收货人电话")
    private String receiverPhone;

    @TableField("receiver_post_code")
    @Schema(description = "收货人邮编")
    private String receiverPostCode;

    @TableField("receiver_province")
    @Schema(description = "省份/直辖市")
    private String receiverProvince;

    @TableField("receiver_city")
    @Schema(description = "城市")
    private String receiverCity;

    @TableField("receiver_region")
    @Schema(description = "区")
    private String receiverRegion;

    @TableField("receiver_detail_address")
    @Schema(description = "详细地址")
    private String receiverDetailAddress;

    @TableField("note")
    @Schema(description = "订单备注")
    private String note;

    @TableField("confirm_status")
    @Schema(description = "确认收货状态[0->未确认；1->已确认]")
    private Integer confirmStatus;

    @TableField("delete_status")
    @Schema(description = "删除状态【0->未删除；1->已删除】")
    private Integer deleteStatus;

    @TableField("use_integration")
    @Schema(description = "下单时使用的积分")
    private Integer useIntegration;

    @TableField("payment_time")
    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @TableField("delivery_time")
    @Schema(description = "发货时间")
    private LocalDateTime deliveryTime;

    @TableField("receive_time")
    @Schema(description = "确认收货时间")
    private LocalDateTime receiveTime;

    @TableField("comment_time")
    @Schema(description = "评价时间")
    private LocalDateTime commentTime;

    @TableField("modify_time")
    @Schema(description = "修改时间")
    private LocalDateTime modifyTime;
}
