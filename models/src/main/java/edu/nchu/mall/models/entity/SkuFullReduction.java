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
@TableName("sms_sku_full_reduction")
@Schema(description = "商品满减信息")
public class SkuFullReduction {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("sku_id")
    @Schema(description = "spu_id")
    private Long skuId;

    @TableField("full_price")
    @Schema(description = "满多少")
    private BigDecimal fullPrice;

    @TableField("reduce_price")
    @Schema(description = "减多少")
    private BigDecimal reducePrice;

    @TableField("add_other")
    @Schema(description = "是否参与其他优惠")
    private Integer addOther;
}
