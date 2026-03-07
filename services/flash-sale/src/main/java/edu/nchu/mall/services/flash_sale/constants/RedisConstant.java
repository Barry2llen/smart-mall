package edu.nchu.mall.services.flash_sale.constants;

public class RedisConstant {
    public static final String FLASH_SALE_SESSIONS_KEY = "flash_sale:sessions";
    public static final String FLASH_SALE_SKU_SESSIONS_KEY_PREFIX = "flash_sale:sku_sessions:"; // 后面跟skuId，存储某个SKU参与的所有场次ID列表
    public static final String FLASH_SALE_SESSIONS_INFO_KEY = "flash_sale:sessions_info"; // 存储sessionId到FlashSaleSession的映射，方便查询
    public static final String FLASH_SALE_SESSION_SKUS_KEY_PREFIX = "flash_sale:session_skus:"; // 后面跟sessionId
    public static final String FLASH_SALE_SKU_SEMAPHORE_KEY_PREFIX = "flash_sale:semaphore:"; // 后面跟随机码，存储每个SKU的库存信号量
    public static final String FLASH_SALE_SESSION_DIGEST_KEY = "flash_sale:session_digest"; // field: sessionId, value: session内容摘要
    public static final String FLASH_SALE_CLEANUP_META_KEY = "flash_sale:cleanup_meta"; // field: sessionId, value: FlashSaleCleanupMessage

    public static final String FLASH_SALE_USER_PURCHASE_KEY_PREFIX = "flash_sale:user_purchase:"; // 后面跟userId:sessionId:skuId，存储用户购买记录，防止重复购买，value是已购买数量
    public static final String FLASH_SALE_USER_PURCHASE_LOCK_KEY_PREFIX = "lock:flash_sale:user:"; // 后面跟userId:seesionId:skuId，存储用户购买锁，防止单个用户的大量请求

    public static final String FLASH_SALE_USER_INFO_KEY_PREFIX = "flash_sale:user_info:"; // 后面跟userId，用于提前存储用户信息（如地址）

    public static String getUserPurchaseKey(Long userId, Long sessionId, Long skuId) {
        return FLASH_SALE_USER_PURCHASE_KEY_PREFIX + userId + ":" + sessionId + ":" + skuId;
    }

    public static String getUserLockKey(Long userId, Long sessionId, Long skuId) {
        return FLASH_SALE_USER_PURCHASE_LOCK_KEY_PREFIX + userId + ":" + sessionId + ":" + skuId;
    }
}
