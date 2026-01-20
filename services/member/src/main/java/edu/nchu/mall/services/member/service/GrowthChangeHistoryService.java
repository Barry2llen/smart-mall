package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.GrowthChangeHistory;

import java.io.Serializable;

public interface GrowthChangeHistoryService extends IService<GrowthChangeHistory> {
    @Override
    boolean updateById(GrowthChangeHistory entity);

    @Override
    GrowthChangeHistory getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
