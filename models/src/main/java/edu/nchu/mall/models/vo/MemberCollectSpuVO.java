package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberCollectSpuVO {

    @Schema(description = "id")
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
