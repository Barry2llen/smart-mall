package edu.nchu.mall.services.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.nchu.mall.models.entity.OrderReturnReason;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderReturnReasonMapper extends BaseMapper<OrderReturnReason> {
}
