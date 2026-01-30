package edu.nchu.mall.services.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.dto.MemberCollectSubjectDTO;
import edu.nchu.mall.models.entity.MemberCollectSubject;
import edu.nchu.mall.models.vo.MemberCollectSubjectVO;

import java.io.Serializable;
import java.util.List;

public interface MemberCollectSubjectService extends IService<MemberCollectSubject> {

    boolean updateById(MemberCollectSubjectDTO dto);

    MemberCollectSubjectVO getMemberCollectSubjectById(Serializable id);

    @Override
    boolean removeById(Serializable id);

    boolean save(MemberCollectSubjectDTO dto);

    List<MemberCollectSubjectVO> getMemberCollectSubjects(Integer pageNum, Integer pageSize);
}
