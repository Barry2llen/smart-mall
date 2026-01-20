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
@TableName("sms_home_subject_spu")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("homeSubjectSpu")
    @Schema(description = "专题商品")
public class HomeSubjectSpu {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("name")
    @Schema(description = "专题名字")
    private String name;

    @TableField("subject_id")
    @Schema(description = "专题id")
    private Long subjectId;

    @TableField("spu_id")
    @Schema(description = "spu_id")
    private Long spuId;

    @TableField("sort")
    @Schema(description = "排序")
    private Integer sort;
}
