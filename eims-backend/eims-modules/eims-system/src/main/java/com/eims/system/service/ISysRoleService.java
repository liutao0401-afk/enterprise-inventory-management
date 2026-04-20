package com.eims.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eims.system.domain.SysRole;
import java.util.List;

public interface ISysRoleService {
    IPage<SysRole> queryPage(SysRole query, Integer pageNum, Integer pageSize);
    List<SysRole> getAllRoles();
    SysRole getById(Long id);
    void save(SysRole role);
    void updateById(SysRole role);
    void removeByIds(List<Long> ids);
}
