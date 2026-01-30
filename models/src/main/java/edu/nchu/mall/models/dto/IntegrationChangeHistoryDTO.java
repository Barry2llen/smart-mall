package edu.nchu.mall.models.dto;

import edu.nchu.mall.models.validation.Groups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "更新/新增积分变化历史记录")
public class IntegrationChangeHistoryDTO {

    @Schema(description = "id")
    @Null(groups = Groups.Create.class)
    @NotNull(groups = Groups.Update.class)
    private Long id;

    @Schema(description = "member_id")
    private Long memberId;

    @Schema(description = "create_time")
    private LocalDateTime createTime;

    @Schema(description = "变化的值")
    private Integer changeCount;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "来源[0->购物；1->管理员修改;2->活动]")
    private Integer sourceTyoe;
}
