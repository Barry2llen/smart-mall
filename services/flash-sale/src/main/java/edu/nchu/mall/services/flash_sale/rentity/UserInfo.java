package edu.nchu.mall.services.flash_sale.rentity;

import edu.nchu.mall.models.entity.MemberReceiveAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户信息")
public class UserInfo {
    @Schema(description = "收货地址")
    private List<MemberReceiveAddress> addresses;
}
