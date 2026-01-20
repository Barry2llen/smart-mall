package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.Member;

import java.io.Serializable;

public interface MemberService extends IService<Member> {
    @Override
    boolean updateById(Member entity);

    @Override
    Member getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}
