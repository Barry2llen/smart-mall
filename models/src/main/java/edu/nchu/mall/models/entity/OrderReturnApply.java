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
@TableName("oms_order_return_apply")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("ora")
@Schema(description = "订单退货申请")
public class OrderReturnApply {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("order_id")
    @Schema(description = "order_id")
    private Long orderId;

    @TableField("sku_id")
    @Schema(description = "退货商品id")
    private Long skuId;

    @TableField("order_sn")
    @Schema(description = "订单编号")
    private String orderSn;

    @TableField("create_time")
    @Schema(description = "申请时间")
    private LocalDateTime createTime;

    @TableField("member_username")
    @Schema(description = "会员用户名")
    private String memberUsername;

    @TableField("return_amount")
    @Schema(description = "退款金额")
    private BigDecimal returnAmount;

    @TableField("return_name")
    @Schema(description = "退货人姓名")
    private String returnName;

    @TableField("return_phone")
    @Schema(description = "退货人电话")
    private String returnPhone;

    @TableField("status")
    @Schema(description = "申请状态[0->待处理；1->退货中；2->已完成；3->已拒绝]")
    private Integer status;

    @TableField("handle_time")
    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @TableField("sku_img")
    @Schema(description = "商品图片")
    private String skuImg;

    @TableField("sku_name")
    @Schema(description = "商品名称")
    private String skuName;

    @TableField("sku_brand")
    @Schema(description = "商品品牌")
    private String skuBrand;

    @TableField("sku_attrs_vals")
    @Schema(description = "商品销售属性(JSON)")
    private String skuAttrsVals;

    @TableField("sku_count")
    @Schema(description = "退货数量")
    private Integer skuCount;

    @TableField("sku_price")
    @Schema(description = "商品单价")
    private BigDecimal skuPrice;

    @TableField("sku_real_price")
    @Schema(description = "商品实际支付单价")
    private BigDecimal skuRealPrice;

    @TableField("reason")
    @Schema(description = "原因")
    private String reason;

    @TableField("description")
    @Schema(description = "description")
    private String description;

    @TableField("desc_pics")
    @Schema(description = "凭证图片，以逗号隔开")
    private String descPics;

    @TableField("handle_note")
    @Schema(description = "处理备注")
    private String handleNote;

    @TableField("handle_man")
    @Schema(description = "处理人员")
    private String handleMan;

    @TableField("receive_man")
    @Schema(description = "收货人")
    private String receiveMan;

    @TableField("receive_time")
    @Schema(description = "收货时间")
    private LocalDateTime receiveTime;

    @TableField("receive_note")
    @Schema(description = "收货备注")
    private String receiveNote;

    @TableField("receive_phone")
    @Schema(description = "收货电话")
    private String receivePhone;

    @TableField("company_address")
    @Schema(description = "公司收货地址")
    private String companyAddress;
}
