package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.IntegrationChangeHistory;

import java.io.Serializable;

public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistory> {
    @Override
    boolean updateById(IntegrationChangeHistory entity);

    @Override
    IntegrationChangeHistory getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
