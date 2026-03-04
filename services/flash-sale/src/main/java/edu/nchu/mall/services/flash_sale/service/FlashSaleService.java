package edu.nchu.mall.services.flash_sale.service;

public interface FlashSaleService {
    void uploadFlashSaleSkusToRedis_3d() throws Exception;

    void cleanFlashSaleSessionCache(Long sessionId);
}
