package edu.nchu.mall.models.dto;

import edu.nchu.mall.models.validation.Groups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "更新/新增会员收藏的专题活动")
public class MemberCollectSubjectDTO {

    @Schema(description = "id")
    @Null(groups = Groups.Create.class)
    @NotNull(groups = Groups.Update.class)
    private Long id;

    @Schema(description = "subject_id")
    private Long subjectId;

    @Schema(description = "subject_name")
    private String subjectName;

    @Schema(description = "subject_img")
    private String subjectImg;

    @Schema(description = "活动url")
    private String subjectUrll;
}
