package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.MemberLoginLogDTO;
import edu.nchu.mall.models.entity.MemberLoginLog;
import edu.nchu.mall.models.vo.MemberLoginLogVO;

import java.io.Serializable;
import java.util.List;

public interface MemberLoginLogService extends IService<MemberLoginLog> {

    boolean updateById(MemberLoginLogDTO dto);

    MemberLoginLogVO getMemberLoginLogById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    boolean save(MemberLoginLogDTO dto);

    List<MemberLoginLogVO> getMemberLoginLogs(Integer pageNum, Integer pageSize);
}
