package edu.nchu.mall.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "积分传输对象")
public class BoundsDTO {
    @Schema(description = "spu id")
    private Long spuId;
    @Schema(description = "成长积分")
    private Integer growBounds;
    @Schema(description = "购物积分")
    private Integer buyBounds;
}
