package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.GrowthChangeHistoryDTO;
import edu.nchu.mall.models.entity.GrowthChangeHistory;
import edu.nchu.mall.models.vo.GrowthChangeHistoryVO;

import java.io.Serializable;
import java.util.List;

public interface GrowthChangeHistoryService extends IService<GrowthChangeHistory> {

    boolean updateById(GrowthChangeHistoryDTO dto);

    GrowthChangeHistoryVO getGrowthChangeHistoryById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    boolean save(GrowthChangeHistoryDTO dto);

    List<GrowthChangeHistoryVO> getGrowthChangeHistories(Integer pageNum, Integer pageSize);
}
