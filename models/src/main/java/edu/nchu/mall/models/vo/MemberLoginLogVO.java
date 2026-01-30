package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginLogVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "member_id")
    private Long memberId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "ip")
    private String ip;

    @Schema(description = "city")
    private String city;

    @Schema(description = "登录类型[1-web，2-app]")
    private Integer loginType;
}
