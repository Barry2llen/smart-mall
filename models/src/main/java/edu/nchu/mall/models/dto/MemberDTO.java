package edu.nchu.mall.models.dto;

import edu.nchu.mall.models.validation.Groups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "更新/新增会员")
public class MemberDTO {
    
    @Schema(description = "会员id")
    @Null(groups = Groups.Create.class)
    @NotNull(groups = Groups.Update.class)
    private Long id;
    
    @Schema(description = "会员等级id")
    private Long levelId;

    @Schema(description = "用户名")
    @NotBlank
    private String username;
    
    @Schema(description = "密码")
    @NotBlank
    private String password;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像")
    private String header;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "生日")
    private LocalDate birth;

    @Schema(description = "所在城市")
    private String city;

    @Schema(description = "职业")
    private String job;

    @Schema(description = "个性签名")
    private String sign;

    @Schema(description = "用户来源")
    private Integer sourceType;

    @Schema(description = "积分")
    private Integer integration;

    @Schema(description = "成长值")
    private Integer growth;

    @Schema(description = "启用状态")
    private Integer status;

    @Schema(description = "注册时间")
    private LocalDateTime createTime;
}
