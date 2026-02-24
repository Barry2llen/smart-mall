package edu.nchu.mall.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "订单来源")
public enum OrderSource {
    PC(0),
    APP(1);

    @JsonValue
    @Getter
    private final int value;

    OrderSource(int value) {
        this.value = value;
    }
}
