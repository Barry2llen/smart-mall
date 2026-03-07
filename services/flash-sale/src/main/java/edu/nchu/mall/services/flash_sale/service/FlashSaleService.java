package edu.nchu.mall.services.flash_sale.service;

import edu.nchu.mall.models.to.mq.FlashSaleOrder;
import edu.nchu.mall.services.flash_sale.dto.Kill;
import edu.nchu.mall.services.flash_sale.rentity.FlashSaleSession;
import edu.nchu.mall.services.flash_sale.vo.OrderConfirm;
import edu.nchu.mall.services.flash_sale.vo.SessionVO;
import lombok.Getter;

import java.util.List;

public interface FlashSaleService {

    ThreadLocal<FlashSaleOrder> ORDER = new ThreadLocal<>();

    void uploadFlashSaleSkusToRedis_3d() throws Exception;

    void cleanFlashSaleSessionCache(String sessionId);

    List<SessionVO> getSession(Boolean withExpired, Boolean withProducts, Integer pageNum, Integer pageSize);

    /**
     * 判断某个商品是否参与了当前正在进行的秒杀活动。
     * 此外，将访问该接口的用户的信息记录到redis，以便在确认订单时快速获取用户的信息。
     * @param userId 用户id
     * @param skuId 商品id
     * @return true如果该商品参与了当前正在进行的秒杀活动，false否则
     */
    boolean isSkuInFlashSale(Long userId, Long skuId);

    List<FlashSaleSession> getFlashSaleSessionsBySkuId(Long skuId);

    SessionVO getSessionById(Long sessionId, Boolean withProducts);

    OrderConfirm confirmOrder(Long userId, Long sessionId, Long skuId, int num);

    KillStatus kill(Long userId, Kill dto);

    /**
     * 删除用户购买记录以及占用的信号量，主要用于异常情况时的补偿操作以及订单取消后的回滚操作
     * @param userId 用户id
     * @param sessionId 场次id
     * @param skuId 商品id
     * @param randomCode 随机码
     * @param num 购买数量
     */
    void deleteUserPurchaseRecord(Long userId, Long sessionId, Long skuId, String randomCode, int num);

    /**
     * 提前缓存用户信息到Redis，减少秒杀请求时的远程调用开销
     * @param userId 用户ID
     * @return 一个线程对象，调用者可以选择等待该线程完成以确保用户信息已缓存，或者直接让它在后台执行
     */
    Thread preCacheUserInfo(Long userId);

    /**
     * 重新缓存用户信息到Redis，适用于用户信息发生变化时的场景
     * @param userId 用户ID
     * @return 一个线程对象，调用者可以选择等待该线程完成以确保用户信息已更新，或者直接让它在后台执行
     */
    Thread reCacheUserInfo(Long userId);

    enum KillStatus {
        SUCCEEDED("秒杀成功"),
        LIMIT_EXCEEDED("超过购买限制"),
        STOCK_NOT_ENOUGH("库存不足"),
        NOT_STARTED("活动未开始"),
        ENDED("活动已结束"),
        INVALID("无效请求"),
        OPERATION_TOO_FREQUENT("操作过于频繁"),
        ERROR("错误");

        @Getter
        private final String message;

        KillStatus(String message) {
            this.message = message;
        }
    }
}
