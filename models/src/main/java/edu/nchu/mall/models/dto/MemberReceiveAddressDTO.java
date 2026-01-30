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
@Schema(description = "更新/新增会员收货地址")
public class MemberReceiveAddressDTO {

    @Schema(description = "id")
    @Null(groups = Groups.Create.class)
    @NotNull(groups = Groups.Update.class)
    private Long id;

    @Schema(description = "member_id")
    private Long memberId;

    @Schema(description = "收货人姓名")
    private String name;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "邮政编码")
    private String postCode;

    @Schema(description = "省份/直辖市")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区")
    private String region;

    @Schema(description = "详细地址(街道)")
    private String detailAddress;

    @Schema(description = "省市区代码")
    private String areacode;

    @Schema(description = "是否默认")
    private Integer defaultStatus;
}
