package edu.nchu.mall.services.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.nchu.mall.models.entity.OrderSetting;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderSettingMapper extends BaseMapper<OrderSetting> {
}
