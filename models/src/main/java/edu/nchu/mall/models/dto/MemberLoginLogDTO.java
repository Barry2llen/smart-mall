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
@Schema(description = "更新/新增会员登录记录")
public class MemberLoginLogDTO {

    @Schema(description = "id")
    @Null(groups = Groups.Create.class)
    @NotNull(groups = Groups.Update.class)
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
