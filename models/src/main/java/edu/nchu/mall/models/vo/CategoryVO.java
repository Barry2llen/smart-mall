package edu.nchu.mall.models.vo;

import edu.nchu.mall.models.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategoryVO extends Category {
    @Schema(description = "当前分类的子分类")
    private List<CategoryVO> children;
}
