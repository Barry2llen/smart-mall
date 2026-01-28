package edu.nchu.mall.models.dto;

import edu.nchu.mall.models.validation.Groups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "新增/修改属性分组")
public class AttrGroupDTO {

    @Schema(description = "分组id")
    @NotNull(groups = Groups.Update.class, message = "分组ID不能为空")
    @Null(groups = Groups.Create.class)
    private String attrGroupId;

    @NotBlank
    @Schema(description = "组名")
    private String attrGroupName;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "描述")
    private String descript;

    @Schema(description = "组图标")
    private String icon;

    @Schema(description = "所属分类id")
    private Long catelogId;
}
