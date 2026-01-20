package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.MemberLevel;

import java.io.Serializable;

public interface MemberLevelService extends IService<MemberLevel> {
    @Override
    boolean updateById(MemberLevel entity);

    @Override
    MemberLevel getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
