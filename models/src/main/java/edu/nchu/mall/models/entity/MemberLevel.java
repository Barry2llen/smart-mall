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
@TableName("ums_member_level")
@Schema(description = "会员等级")
public class MemberLevel {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("name")
    @Schema(description = "等级名称")
    private String name;

    @TableField("growth_point")
    @Schema(description = "等级需要的成长值")
    private Integer growthPoint;

    @TableField("default_status")
    @Schema(description = "是否为默认等级[0->不是；1->是]")
    private Integer defaultStatus;

    @TableField("free_freight_point")
    @Schema(description = "免运费标准")
    private BigDecimal freeFreightPoint;

    @TableField("comment_growth_point")
    @Schema(description = "每次评价获取的成长值")
    private Integer commentGrowthPoint;

    @TableField("priviledge_free_freight")
    @Schema(description = "是否有免邮特权")
    private Integer priviledgeFreeFreight;

    @TableField("priviledge_member_price")
    @Schema(description = "是否有会员价格特权")
    private Integer priviledgeMemberPrice;

    @TableField("priviledge_birthday")
    @Schema(description = "是否有生日特权")
    private Integer priviledgeBirthday;

    @TableField("note")
    @Schema(description = "备注")
    private String note;
}
