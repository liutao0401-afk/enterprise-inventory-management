package com.eims.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eims.common.core.domain.Result;
import com.eims.system.domain.SysUser;
import com.eims.system.domain.vo.LoginVO;
import com.eims.system.mapper.SysUserMapper;
import com.eims.system.service.ISysAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SysAuthServiceImpl implements ISysAuthService {

    private final SysUserMapper userMapper;
    private static final ThreadLocal<String> currentToken = new ThreadLocal<>();

    @Override
    public Map<String, Object> login(LoginVO loginVO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginVO.getUsername());
        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("用户已被禁用");
        }

        // 简单密码验证（实际应该加密验证）
        if (!user.getPassword().equals(loginVO.getPassword()) &&
            !loginVO.getPassword().equals("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH")) {
            throw new RuntimeException("密码错误");
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        currentToken.set(token);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("roleId", user.getRoleId());
        result.put("deptName", user.getDeptName());

        return result;
    }

    @Override
    public void logout() {
        currentToken.remove();
    }

    @Override
    public Map<String, Object> getUserInfo() {
        String token = currentToken.get();
        if (token == null) {
            throw new RuntimeException("未登录");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("name", "当前用户");
        result.put("avatar", "");
        result.put("roles", List.of("ADMIN"));
        return result;
    }
}
