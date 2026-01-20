package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.MemberCollectSubject;

import java.io.Serializable;

public interface MemberCollectSubjectService extends IService<MemberCollectSubject> {
    @Override
    boolean updateById(MemberCollectSubject entity);

    @Override
    MemberCollectSubject getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
