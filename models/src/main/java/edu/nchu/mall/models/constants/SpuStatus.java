package edu.nchu.mall.models.constants;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


public enum SpuStatus {
    NEW(0, "新建"),
    UP(1, "上架"),
    DOWN(2, "下架");

    private final int code;
    @Getter
    private final String message;
    SpuStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @JsonValue
    public int getCode() {
        return code;
    }
}
