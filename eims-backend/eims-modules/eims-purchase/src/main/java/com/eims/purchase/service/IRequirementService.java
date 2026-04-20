package com.eims.purchase.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eims.purchase.domain.Requirement;
import com.eims.purchase.domain.vo.DuplicateCheckVO;
import java.util.List;
import java.util.Map;

public interface IRequirementService {
    IPage<Requirement> queryPage(Requirement query, Integer pageNum, Integer pageSize);
    Requirement getById(Long id);
    void saveRequirement(Requirement requirement);
    void updateById(Requirement requirement);
    void removeByIds(List<Long> ids);
    Map<String, Object> checkDuplicate(DuplicateCheckVO checkVO);
    void approve(Long id);
    void reject(Long id, String comment);
}
