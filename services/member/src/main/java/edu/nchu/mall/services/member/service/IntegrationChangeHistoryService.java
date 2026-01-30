package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.IntegrationChangeHistoryDTO;
import edu.nchu.mall.models.entity.IntegrationChangeHistory;
import edu.nchu.mall.models.vo.IntegrationChangeHistoryVO;

import java.io.Serializable;
import java.util.List;

public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistory> {

    boolean updateById(IntegrationChangeHistoryDTO dto);

    IntegrationChangeHistoryVO getIntegrationChangeHistoryById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    boolean save(IntegrationChangeHistoryDTO dto);

    List<IntegrationChangeHistoryVO> getIntegrationChangeHistories(Integer pageNum, Integer pageSize);
}
