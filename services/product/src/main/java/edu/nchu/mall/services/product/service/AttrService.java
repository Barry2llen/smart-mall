package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.AttrDTO;
import edu.nchu.mall.models.entity.Attr;
import edu.nchu.mall.models.vo.AttrVO;

import java.io.Serializable;
import java.util.List;

public interface AttrService extends IService<Attr> {
    boolean updateById(AttrDTO dto);

    boolean save(AttrDTO dto);

    AttrVO getVoById(Serializable id);

    List<AttrVO> getVosByGroupId(Serializable groupId);

    @Override
    boolean removeById(Serializable id);

    List<AttrVO> list(Integer pageNum, Integer pageSize, String attrName, Integer catelogId);
}
