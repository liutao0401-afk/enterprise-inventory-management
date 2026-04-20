package com.eims.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.eims.system.domain.SysUser;
import java.util.List;
import java.util.Map;

public interface ISysUserService {
    IPage<SysUser> queryPage(SysUser query, Integer pageNum, Integer pageSize);
    SysUser getById(Long id);
    void save(SysUser user);
    void updateById(SysUser user);
    void removeByIds(List<Long> ids);
    void resetPwd(Long userId, String password);
    List<Map<String, Object>> getAllRoles();
}
