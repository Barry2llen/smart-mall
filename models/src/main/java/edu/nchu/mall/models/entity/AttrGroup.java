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
@TableName("pms_attr_group")
@Schema(description = "属性分组")
public class AttrGroup {

    @TableId(value = "attr_group_id", type = IdType.ASSIGN_ID)
    @Schema(description = "分组id")
    private Long attrGroupId;

    @TableField("attr_group_name")
    @Schema(description = "组名")
    private String attrGroupName;

    @TableField("sort")
    @Schema(description = "排序")
    private Integer sort;

    @TableField("descript")
    @Schema(description = "描述")
    private String descript;

    @TableField("icon")
    @Schema(description = "组图标")
    private String icon;

    @TableField("catelog_id")
    @Schema(description = "所属分类id")
    private Long catelogId;
}
