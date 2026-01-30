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
@Schema(description = "更新/新增会员收藏的商品")
public class MemberCollectSpuDTO {

    @Schema(description = "id")
    @Null(groups = Groups.Create.class)
    @NotNull(groups = Groups.Update.class)
    private Long id;

    @Schema(description = "会员id")
    private Long memberId;

    @Schema(description = "spu_id")
    private Long spuId;

    @Schema(description = "spu_name")
    private String spuName;

    @Schema(description = "spu_img")
    private String spuImg;

    @Schema(description = "create_time")
    private LocalDateTime createTime;
}
