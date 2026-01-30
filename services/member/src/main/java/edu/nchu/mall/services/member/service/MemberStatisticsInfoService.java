package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.MemberStatisticsInfoDTO;
import edu.nchu.mall.models.entity.MemberStatisticsInfo;
import edu.nchu.mall.models.vo.MemberStatisticsInfoVO;

import java.io.Serializable;
import java.util.List;

public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfo> {

    boolean updateById(MemberStatisticsInfoDTO dto);

    MemberStatisticsInfoVO getMemberStatisticsInfoById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    boolean save(MemberStatisticsInfoDTO dto);

    List<MemberStatisticsInfoVO> getMemberStatisticsInfos(Integer pageNum, Integer pageSize);
}
