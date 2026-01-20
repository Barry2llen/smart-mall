package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.MemberStatisticsInfo;

import java.io.Serializable;

public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfo> {
    @Override
    boolean updateById(MemberStatisticsInfo entity);

    @Override
    MemberStatisticsInfo getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
