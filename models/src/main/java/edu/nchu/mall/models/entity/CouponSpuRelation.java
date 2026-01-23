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
@TableName("sms_coupon_spu_relation")
@Schema(description = "优惠券与产品关联")
public class CouponSpuRelation {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("coupon_id")
    @Schema(description = "优惠券id")
    private Long couponId;

    @TableField("spu_id")
    @Schema(description = "spu_id")
    private Long spuId;

    @TableField("spu_name")
    @Schema(description = "spu_name")
    private String spuName;
}
