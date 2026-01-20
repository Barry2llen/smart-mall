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
@TableName("sms_home_subject")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("homeSubject")
    @Schema(description = "首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】")
public class HomeSubject {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("name")
    @Schema(description = "专题名字")
    private String name;

    @TableField("title")
    @Schema(description = "专题标题")
    private String title;

    @TableField("sub_title")
    @Schema(description = "专题副标题")
    private String subTitle;

    @TableField("status")
    @Schema(description = "显示状态")
    private Integer status;

    @TableField("url")
    @Schema(description = "详情连接")
    private String url;

    @TableField("sort")
    @Schema(description = "排序")
    private Integer sort;

    @TableField("img")
    @Schema(description = "专题图片地址")
    private String img;
}
