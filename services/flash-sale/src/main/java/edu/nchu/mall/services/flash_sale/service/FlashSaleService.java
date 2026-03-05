package edu.nchu.mall.services.flash_sale.service;

import edu.nchu.mall.services.flash_sale.vo.SessionVO;

import java.util.List;

public interface FlashSaleService {
    void uploadFlashSaleSkusToRedis_3d() throws Exception;

    void cleanFlashSaleSessionCache(String sessionId);

    List<SessionVO> getSession(Boolean withExpired, Boolean withProducts, Integer pageNum, Integer pageSize);
}
