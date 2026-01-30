package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.MemberCollectSpuDTO;
import edu.nchu.mall.models.entity.MemberCollectSpu;
import edu.nchu.mall.models.vo.MemberCollectSpuVO;

import java.io.Serializable;
import java.util.List;

public interface MemberCollectSpuService extends IService<MemberCollectSpu> {

    boolean updateById(MemberCollectSpuDTO dto);

    MemberCollectSpuVO getMemberCollectSpuById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    boolean save(MemberCollectSpuDTO dto);

    List<MemberCollectSpuVO> getMemberCollectSpus(Integer pageNum, Integer pageSize);
}
