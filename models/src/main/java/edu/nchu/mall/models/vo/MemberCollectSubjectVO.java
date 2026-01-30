package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberCollectSubjectVO {

    @Schema(description = "id")
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
