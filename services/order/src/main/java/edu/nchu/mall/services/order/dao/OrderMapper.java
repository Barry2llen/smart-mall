package edu.nchu.mall.services.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.nchu.mall.models.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
