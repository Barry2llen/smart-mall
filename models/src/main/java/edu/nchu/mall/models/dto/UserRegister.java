package edu.nchu.mall.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "用户注册信息")
@AllArgsConstructor
@NoArgsConstructor
public class UserRegister {
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 20, message = "用户名长度必须在1-20内")
    private String username;
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度必须在8-32内")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    private String password;
    @Schema(description = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")
    private String email;
    @Schema(description = "验证码")
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d+$", message = "验证码错误")
    private String code;
}
