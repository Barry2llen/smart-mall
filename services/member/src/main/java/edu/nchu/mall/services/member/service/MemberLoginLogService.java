package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.MemberLoginLog;

import java.io.Serializable;

public interface MemberLoginLogService extends IService<MemberLoginLog> {
    @Override
    boolean updateById(MemberLoginLog entity);

    @Override
    MemberLoginLog getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
