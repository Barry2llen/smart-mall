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

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sms_member_price")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("memberPrice")
    @Schema(description = "商品会员价格")
public class MemberPrice {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("sku_id")
    @Schema(description = "sku_id")
    private Long skuId;

    @TableField("member_level_id")
    @Schema(description = "会员等级id")
    private Long memberLevelId;

    @TableField("member_level_name")
    @Schema(description = "会员等级名")
    private String memberLevelName;

    @TableField("member_price")
    @Schema(description = "会员对应价格")
    private BigDecimal memberPrice;

    @TableField("add_other")
    @Schema(description = "可否叠加其他优惠[0-不可叠加优惠，1-可叠加]")
    private Integer addOther;
}
