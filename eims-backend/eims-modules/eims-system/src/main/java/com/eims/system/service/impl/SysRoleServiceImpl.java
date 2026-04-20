package com.eims.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.system.domain.SysRole;
import com.eims.system.mapper.SysRoleMapper;
import com.eims.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements ISysRoleService {

    private final SysRoleMapper roleMapper;

    @Override
    public IPage<SysRole> queryPage(SysRole query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getRoleName())) {
            wrapper.like(SysRole::getRoleName, query.getRoleName());
        }
        if (StringUtils.hasText(query.getRoleCode())) {
            wrapper.eq(SysRole::getRoleCode, query.getRoleCode());
        }
        wrapper.orderByDesc(SysRole::getCreateTime);
        return roleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<SysRole> getAllRoles() {
        return roleMapper.selectList(null);
    }

    @Override
    public SysRole getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public void save(SysRole role) {
        roleMapper.insert(role);
    }

    @Override
    public void updateById(SysRole role) {
        roleMapper.updateById(role);
    }

    @Override
    public void removeByIds(List<Long> ids) {
        roleMapper.deleteBatchIds(ids);
    }
}
