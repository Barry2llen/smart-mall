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
@TableName("sms_spu_bounds")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("spuBounds")
@Schema(description = "商品spu积分设置")
public class SpuBounds {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("spu_id")
    @Schema(description = "spu_id")
    private Long spuId;

    @TableField("grow_bounds")
    @Schema(description = "成长积分")
    private BigDecimal growBounds;

    @TableField("buy_bounds")
    @Schema(description = "购物积分")
    private BigDecimal buyBounds;

    @TableField("work")
    @Schema(description = "优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]")
    private Integer work;
}
