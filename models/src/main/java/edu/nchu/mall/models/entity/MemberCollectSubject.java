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
@TableName("ums_member_collect_subject")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("memberCollectSubject")
@Schema(description = "会员收藏的专题活动")
public class MemberCollectSubject {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("subject_id")
    @Schema(description = "subject_id")
    private Long subjectId;

    @TableField("subject_name")
    @Schema(description = "subject_name")
    private String subjectName;

    @TableField("subject_img")
    @Schema(description = "subject_img")
    private String subjectImg;

    @TableField("subject_urll")
    @Schema(description = "活动url")
    private String subjectUrll;
}
