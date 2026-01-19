package edu.nchu.mall.services.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    @Schema(description = "用户ID")
    @NotBlank
    private Long id;

    @Schema(description = "用户名")
    @Pattern(regexp = "^[0-9a-zA-Z]*$")
    private String username;
}
