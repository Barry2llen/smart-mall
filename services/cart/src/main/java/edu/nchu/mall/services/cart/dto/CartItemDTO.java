package edu.nchu.mall.services.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "购物项DTO")
public class CartItemDTO {
    @Schema(description = "skuId")
    @NotNull
    private Long skuId;
    @Schema(description = "数量")
    @Positive(message = "数量必须大于0")
    private Integer count;
}
