package edu.nchu.mall.services.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.nchu.mall.models.entity.MemberLoginLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberLoginLogMapper extends BaseMapper<MemberLoginLog> {

}
