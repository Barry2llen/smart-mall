package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.MemberCollectSpu;

import java.io.Serializable;

public interface MemberCollectSpuService extends IService<MemberCollectSpu> {
    @Override
    boolean updateById(MemberCollectSpu entity);

    @Override
    MemberCollectSpu getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
