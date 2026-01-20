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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("ums_member")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("member")
    @Schema(description = "会员")
public class Member {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("level_id")
    @Schema(description = "会员等级id")
    private Long levelId;

    @TableField("username")
    @Schema(description = "用户名")
    private String username;

    @TableField("password")
    @Schema(description = "密码")
    private String password;

    @TableField("nickname")
    @Schema(description = "昵称")
    private String nickname;

    @TableField("mobile")
    @Schema(description = "手机号码")
    private String mobile;

    @TableField("email")
    @Schema(description = "邮箱")
    private String email;

    @TableField("header")
    @Schema(description = "头像")
    private String header;

    @TableField("gender")
    @Schema(description = "性别")
    private Integer gender;

    @TableField("birth")
    @Schema(description = "生日")
    private LocalDate birth;

    @TableField("city")
    @Schema(description = "所在城市")
    private String city;

    @TableField("job")
    @Schema(description = "职业")
    private String job;

    @TableField("sign")
    @Schema(description = "个性签名")
    private String sign;

    @TableField("source_type")
    @Schema(description = "用户来源")
    private Integer sourceType;

    @TableField("integration")
    @Schema(description = "积分")
    private Integer integration;

    @TableField("growth")
    @Schema(description = "成长值")
    private Integer growth;

    @TableField("status")
    @Schema(description = "启用状态")
    private Integer status;

    @TableField("create_time")
    @Schema(description = "注册时间")
    private LocalDateTime createTime;
}
