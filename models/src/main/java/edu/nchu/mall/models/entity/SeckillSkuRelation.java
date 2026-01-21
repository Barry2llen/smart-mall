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
@TableName("sms_seckill_sku_relation")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("seckillSkuRelation")
@Schema(description = "秒杀活动商品关联")
public class SeckillSkuRelation {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("promotion_id")
    @Schema(description = "活动id")
    private Long promotionId;

    @TableField("promotion_session_id")
    @Schema(description = "活动场次id")
    private Long promotionSessionId;

    @TableField("sku_id")
    @Schema(description = "商品id")
    private Long skuId;

    @TableField("seckill_price")
    @Schema(description = "秒杀价格")
    private BigDecimal seckillPrice;

    @TableField("seckill_count")
    @Schema(description = "秒杀总量")
    private BigDecimal seckillCount;

    @TableField("seckill_limit")
    @Schema(description = "每人限购数量")
    private BigDecimal seckillLimit;

    @TableField("seckill_sort")
    @Schema(description = "排序")
    private Integer seckillSort;
}
