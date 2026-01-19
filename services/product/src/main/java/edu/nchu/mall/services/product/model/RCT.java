package edu.nchu.mall.services.product.model;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Result Code Type
 * 状态码类型
 */
@Schema(description = "状态码类型")
public enum RCT {
    @Schema(description = "成功")
    SUCCESS(0),

    @Schema(description = "失败")
    FAIL(1),

    @Schema(description = "缺少参数")
    MISSING_PARAMETER(2),

    @Schema(description = "未知异常")
    UNKNOWN_EXCEPTION(500),

    @Schema(description = "参数校验失败")
    VALIDATION_FAILED(403);

    private final int code;

    RCT(int code) {
        this.code = code;
    }

    public int getInteger() {
        return code;
    }
}
