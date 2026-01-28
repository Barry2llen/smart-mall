package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttrGroupVO {
    @Schema(description = "分组id")
    private Long attrGroupId;

    @Schema(description = "组名")
    private String attrGroupName;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "描述")
    private String descript;

    @Schema(description = "组图标")
    private String icon;
}
