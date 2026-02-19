package edu.nchu.mall.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "用户登录信息")
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin {
    @Schema(description = "用户名")
    @NotBlank
    private String username;
    @Schema(description = "密码")
    @NotBlank
    private String password;
}
