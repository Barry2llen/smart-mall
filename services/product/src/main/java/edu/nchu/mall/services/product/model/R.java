package edu.nchu.mall.services.product.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一封装 http请求 返回格式
 * @param <T> 数据类型（可选）
 */

@Schema(description = "统一http请求返回格式")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class R<T>{

    @Schema(description = "状态码")
    RCT code;

    @Schema(description = "消息")
    String msg;

    @Schema(description = "数据")
    T data;

    public static <T> R<T> success(T data) {
        return new R<>(RCT.SUCCESS, "success", data);
    }

    public static <T> R<T> success(String msg, T data) {
        return new R<>(RCT.SUCCESS, msg, data);
    }

    public static <T> R<T> fail(String msg) {
        return new R<>(RCT.FAIL, msg, null);
    }
}
