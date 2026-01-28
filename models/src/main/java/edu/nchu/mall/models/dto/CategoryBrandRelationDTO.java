package edu.nchu.mall.models.dto;

import edu.nchu.mall.models.validation.Groups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBrandRelationDTO {
    @NotNull(groups = {Groups.Update.class}, message = "品牌id不能为空")
    @Schema(description = "品牌id")
    private Long brandId;

    @NotNull(groups = {Groups.Update.class}, message = "分类id不能为空")
    @Schema(description = "分类id")
    private Long catelogId;
}
