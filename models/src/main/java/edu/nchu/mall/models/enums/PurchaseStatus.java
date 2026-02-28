package edu.nchu.mall.models.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 采购单状态
 */
@Getter
@Schema(description = "采购单状态")
public enum PurchaseStatus {
    CREATED(0, "新建/未分配"),
    ASSIGNED(1, "已分配"),
    RECEIVED(2, "已领取"),
    FINISHED(3, "已完成"),
    ERROR(4, "有异常");

    private final int code;
    private final String message;

    PurchaseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
