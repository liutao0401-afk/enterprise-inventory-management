package com.eims.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.system.domain.SysUser;
import com.eims.system.mapper.SysUserMapper;
import com.eims.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper userMapper;

    @Override
    public IPage<SysUser> queryPage(SysUser query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getUsername())) {
            wrapper.like(SysUser::getUsername, query.getUsername());
        }
        if (StringUtils.hasText(query.getRealName())) {
            wrapper.like(SysUser::getRealName, query.getRealName());
        }
        if (query.getRoleId() != null) {
            wrapper.eq(SysUser::getRoleId, query.getRoleId());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public SysUser getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void save(SysUser user) {
        userMapper.insert(user);
    }

    @Override
    public void updateById(SysUser user) {
        userMapper.updateById(user);
    }

    @Override
    public void removeByIds(List<Long> ids) {
        userMapper.deleteBatchIds(ids);
    }

    @Override
    public void resetPwd(Long userId, String password) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(password);
        userMapper.updateById(user);
    }

    @Override
    public List<Map<String, Object>> getAllRoles() {
        return List.of(
            Map.of("id", 1L, "roleName", "系统管理员", "roleCode", "ADMIN"),
            Map.of("id", 2L, "roleName", "采购主管", "roleCode", "PURCHASE_MANAGER"),
            Map.of("id", 3L, "roleName", "采购员", "roleCode", "PURCHASER"),
            Map.of("id", 4L, "roleName", "仓库管理员", "roleCode", "WAREHOUSE_ADMIN"),
            Map.of("id", 5L, "roleName", "质检员", "roleCode", "QC_INSPECTOR"),
            Map.of("id", 6L, "roleName", "财务", "roleCode", "FINANCE"),
            Map.of("id", 7L, "roleName", "需求部门", "roleCode", "DEPARTMENT")
        );
    }
}
