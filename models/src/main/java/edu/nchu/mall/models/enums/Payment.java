package edu.nchu.mall.models.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "支付方式")
public enum Payment {
    ALIPAY(0, "支付宝"),
    WECHAT(1, "微信");

    @Getter
    private final int code;
    @Getter
    private final String name;

    Payment(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
