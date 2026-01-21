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
@TableName("pms_attr_attrgroup_relation")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("attrAttrgroupRelation")
@Schema(description = "属性&属性分组关联")
public class AttrAttrgroupRelation {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("attr_id")
    @Schema(description = "属性id")
    private Long attrId;

    @TableField("attr_group_id")
    @Schema(description = "属性分组id")
    private Long attrGroupId;

    @TableField("attr_sort")
    @Schema(description = "属性组内排序")
    private Integer attrSort;
}
