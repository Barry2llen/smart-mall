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
@TableName("sms_coupon_spu_category_relation")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("couponSpuCategoryRelation")
    @Schema(description = "优惠券分类关联")
public class CouponSpuCategoryRelation {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("coupon_id")
    @Schema(description = "优惠券id")
    private Long couponId;

    @TableField("category_id")
    @Schema(description = "产品分类id")
    private Long categoryId;

    @TableField("category_name")
    @Schema(description = "产品分类名称")
    private String categoryName;
}
