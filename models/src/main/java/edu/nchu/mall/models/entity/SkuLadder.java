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
@TableName("sms_sku_ladder")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("skuLadder")
    @Schema(description = "商品阶梯价格")
public class SkuLadder {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("sku_id")
    @Schema(description = "spu_id")
    private Long skuId;

    @TableField("full_count")
    @Schema(description = "满几件")
    private Integer fullCount;

    @TableField("discount")
    @Schema(description = "打几折")
    private BigDecimal discount;

    @TableField("price")
    @Schema(description = "折后价")
    private BigDecimal price;

    @TableField("add_other")
    @Schema(description = "是否叠加其他优惠[0-不可叠加，1-可叠加]")
    private Integer addOther;
}
