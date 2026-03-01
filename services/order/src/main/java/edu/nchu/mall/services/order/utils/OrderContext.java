package edu.nchu.mall.services.order.utils;

//import java.lang.ScopedValue;

public class OrderContext {
    public static final ThreadLocal<String> ORDER_SN = new ThreadLocal<>();

    //public static final ScopedValue<String> ORDER_SN = ScopedValue.newInstance();
}
