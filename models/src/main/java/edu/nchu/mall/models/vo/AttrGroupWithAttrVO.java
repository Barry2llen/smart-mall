package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "包含属性的属性分组视图对象")
public class AttrGroupWithAttrVO {
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "属性信息")
    public static class AttrInfo {
        @Schema(description = "属性id")
        private Long attrId;

        @Schema(description = "属性名")
        private String attrName;

        @Schema(description = "值类型[0-为单个值，1-可以选择多个值]")
        private Integer valueType;

        @Schema(description = "属性图标")
        private String icon;

        @Schema(description = "可选值列表[用逗号分隔]")
        private String valueSelect;

        @Schema(description = "属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]")
        private Integer attrType;

        @Schema(description = "启用状态[0 - 禁用，1 - 启用]")
        private Long enable;
    }

    @Schema(description = "属性列表")
    private List<AttrInfo> attrs;
}
