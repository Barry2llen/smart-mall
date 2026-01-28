package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.AttrGroup;
import edu.nchu.mall.models.vo.AttrGroupVO;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface AttrGroupService extends IService<AttrGroup> {
    @Override
    boolean updateById(AttrGroup entity);

    @Override
    AttrGroup getById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    @Override
    boolean removeByIds(Collection<?> ids);

    @Override
    boolean save(AttrGroup entity);

    List<AttrGroup> list(Integer pageNum, Integer pageSize, String attrGroupName, Integer catelogId);

    List<AttrGroupVO> getAttrGroupByCatelogId(long catelogId);
}
