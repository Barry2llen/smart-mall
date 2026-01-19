package edu.nchu.mall.services.product.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户创建或更新")
public class UserUpdate {
    @Schema(description = "用户名")
    @Pattern(regexp = "^[0-9a-zA-Z]*$")
    private String username;

    @Schema(description = "密码")
    @Size(min = 6, max = 20)
    private String password;
}
