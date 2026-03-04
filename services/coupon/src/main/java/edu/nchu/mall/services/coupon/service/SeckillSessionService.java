package edu.nchu.mall.services.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SeckillSession;
import edu.nchu.mall.models.vo.SeckillSessionVO;

import java.io.Serializable;
import java.util.List;

public interface SeckillSessionService extends IService<SeckillSession> {
    @Override
    boolean updateById(SeckillSession entity);

    @Override
    SeckillSession getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    List<SeckillSession> list(Integer pageNum, Integer pageSize, String name);

    List<SeckillSessionVO> getLatest3DaysSessions();

}
