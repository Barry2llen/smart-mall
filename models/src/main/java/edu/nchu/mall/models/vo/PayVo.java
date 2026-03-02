package edu.nchu.mall.models.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayVo {
    private String out_trade_no; // 商户订单号 必填
    private String subject; // 订单名称 必填
    private String total_amount;  // 付款金额 必填
    private String body; // 商品描述 可空
    private String time_expire; // 订单绝对超时时间 可空 yyyy-MM-dd HH:mm:ss
    private String timeout_express; // 订单相对超时时间 可空 30m 1h 1d
    private final String product_code = "FAST_INSTANT_TRADE_PAY"; // 销售产品码 必填
}
