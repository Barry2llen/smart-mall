package edu.nchu.mall.services.flash_sale.service;

import edu.nchu.mall.models.to.mq.FlashSaleOrder;
import edu.nchu.mall.services.flash_sale.rentity.FlashSaleSession;
import edu.nchu.mall.services.flash_sale.vo.SessionVO;
import lombok.Getter;

import java.util.List;

public interface FlashSaleService {

    ThreadLocal<FlashSaleOrder> ORDER = new ThreadLocal<>();

    void uploadFlashSaleSkusToRedis_3d() throws Exception;

    void cleanFlashSaleSessionCache(String sessionId);

    List<SessionVO> getSession(Boolean withExpired, Boolean withProducts, Integer pageNum, Integer pageSize);

    boolean isSkuInFlashSale(Long skuId);

    List<FlashSaleSession> getFlashSaleSessionsBySkuId(Long skuId);

    SessionVO getSessionById(Long sessionId, Boolean withProducts);

    KillStatus kill(Long userId, Long skuId, String randomCode, Long sessionId, Integer num);

    /**
     * 删除用户购买记录以及占用的信号量，主要用于异常情况时的补偿操作以及订单取消后的回滚操作
     * @param userId 用户id
     * @param sessionId 场次id
     * @param skuId 商品id
     * @param randomCode 随机码
     * @param num 购买数量
     */
    void deleteUserPurchaseRecord(Long userId, Long sessionId, Long skuId, String randomCode, int num);

    enum KillStatus {
        SUCCEEDED("秒杀成功"),
        LIMIT_EXCEEDED("超过购买限制"),
        STOCK_NOT_ENOUGH("库存不足"),
        NOT_STARTED("活动未开始"),
        ENDED("活动已结束"),
        INVALID("无效请求"),
        ERROR("错误");

        @Getter
        private final String message;

        KillStatus(String message) {
            this.message = message;
        }
    }
}
