package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrowthChangeHistoryVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "member_id")
    private Long memberId;

    @Schema(description = "create_time")
    private LocalDateTime createTime;

    @Schema(description = "改变的值（正负计数）")
    private Integer changeCount;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "积分来源[0-购物，1-管理员修改]")
    private Integer sourceType;
}
