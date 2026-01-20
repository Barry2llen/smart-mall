package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.MemberReceiveAddress;

import java.io.Serializable;

public interface MemberReceiveAddressService extends IService<MemberReceiveAddress> {
    @Override
    boolean updateById(MemberReceiveAddress entity);

    @Override
    MemberReceiveAddress getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
