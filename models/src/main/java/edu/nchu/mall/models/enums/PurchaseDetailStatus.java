package edu.nchu.mall.models.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "采购需求状态")
public enum PurchaseDetailStatus {
    CREATED(0, "新建"),
    ASSIGNED(1, "已分配"),
    RECEIVED(2, "正在采购"),
    FINISHED(3, "已完成"),
    CANCELED(4, "失败");

    private final int code;
    private final String message;

    PurchaseDetailStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
