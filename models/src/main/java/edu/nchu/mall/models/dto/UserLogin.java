package edu.nchu.mall.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "用户登录信息")
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
}
