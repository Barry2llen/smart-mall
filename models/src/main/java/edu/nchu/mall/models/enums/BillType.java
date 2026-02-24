package edu.nchu.mall.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "发票类型")
public enum BillType {
    // [0->不开发票；1->电子发票；2->纸质发票]
    @Schema(description = "不开发票")
    NONE(0),
    @Schema(description = "电子发票")
    ELECTRONIC(1),
    @Schema(description = "纸质发票")
    PAPER(2);

    @Getter
    @JsonValue
    private final int value;
    BillType(int value) {
        this.value = value;
    }
}
