package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.MemberReceiveAddressDTO;
import edu.nchu.mall.models.entity.MemberReceiveAddress;
import edu.nchu.mall.models.vo.MemberReceiveAddressVO;

import java.io.Serializable;
import java.util.List;

public interface MemberReceiveAddressService extends IService<MemberReceiveAddress> {

    boolean updateById(MemberReceiveAddressDTO dto);

    MemberReceiveAddressVO getMemberReceiveAddressById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    boolean save(MemberReceiveAddressDTO dto);

    List<MemberReceiveAddress> getMemberReceiveAddresses(Long memberId);
}
