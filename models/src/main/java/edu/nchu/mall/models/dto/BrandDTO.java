package edu.nchu.mall.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "新增/修改品牌")
public class BrandDTO {

    @Schema(description = "品牌名")
    private String name;

    @Schema(description = "介绍")
    private String descript;

    @Schema(description = "显示状态[0-不显示；1-显示]")
    private Integer showStatus;

    @Schema(description = "检索首字母")
    private String firstLetter;

    @Schema(description = "排序")
    private Integer sort;
}
