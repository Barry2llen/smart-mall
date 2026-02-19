package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.MemberDTO;
import edu.nchu.mall.models.entity.Member;
import edu.nchu.mall.models.vo.MemberVO;

import java.io.Serializable;
import java.util.List;

public interface MemberService extends IService<Member> {

    boolean updateById(MemberDTO dto);

    MemberVO getMemberById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    boolean save(MemberDTO dto);

    List<MemberVO> getMembers(Integer pageNum, Integer pageSize);

    String getSaltedPassword(String key);

    Member putByEmail(String email, String username);

    Long getMemberIdByUsernameOrEmail(String key);
}
