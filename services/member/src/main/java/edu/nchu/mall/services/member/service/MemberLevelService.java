package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.MemberLevelDTO;
import edu.nchu.mall.models.entity.MemberLevel;
import edu.nchu.mall.models.vo.MemberLevelVO;

import java.io.Serializable;
import java.util.List;

public interface MemberLevelService extends IService<MemberLevel> {

    boolean updateById(MemberLevelDTO dto);

    MemberLevelVO getMemberLevelById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    boolean save(MemberLevelDTO dto);

    List<MemberLevelVO> getMemberLevels(Integer pageNum, Integer pageSize);
}
