package edu.nchu.mall.models.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "支付方式")
public enum Payment implements IEnum<Integer> {
    ALIPAY(0, "支付宝"),
    WECHAT(1, "微信");

    @Getter
    @JsonValue
    private final int code;
    @Getter
    private final String name;

    Payment(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.getCode();
    }
}
