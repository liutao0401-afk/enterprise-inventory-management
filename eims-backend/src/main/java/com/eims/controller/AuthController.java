package com.eims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eims.config.JwtConfig;
import com.eims.dto.ApiResponse;
import com.eims.dto.LoginRequest;
import com.eims.dto.LoginResponse;
import com.eims.entity.SysRole;
import com.eims.entity.SysUser;
import com.eims.repository.SysRoleRepository;
import com.eims.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private SysRoleRepository roleRepository;

    @Autowired
    private JwtConfig jwtConfig;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        SysUser user = userRepository.selectOne(wrapper);

        if (user == null) {
            return ApiResponse.error("用户不存在");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            return ApiResponse.error("用户已禁用");
        }

        // 验证密码 - 数据库中是加密后的密码
        // 测试环境先用明文123456对比
        String inputPassword = request.getPassword();
        String storedPassword = user.getPassword();

        // 判断是BCrypt加密还是明文
        boolean passwordValid;
        if (storedPassword.startsWith("$2")) {
            passwordValid = passwordEncoder.matches(inputPassword, storedPassword);
        } else {
            passwordValid = storedPassword.equals(inputPassword);
        }

        if (!passwordValid) {
            return ApiResponse.error("密码错误");
        }

        // 获取角色信息
        SysRole role = roleRepository.selectById(user.getRoleId());
        String roleCode = role != null ? role.getRoleCode() : "";
        String roleName = role != null ? role.getRoleName() : "";

        // 生成Token
        String token = jwtConfig.generateToken(user.getId(), user.getUsername(), roleCode);

        LoginResponse response = new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                roleCode,
                roleName
        );

        return ApiResponse.success("登录成功", response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success("退出成功", null);
    }

    @GetMapping("/current")
    public ApiResponse<LoginResponse> getCurrentUser(@RequestAttribute("userId") Long userId) {
        SysUser user = userRepository.selectById(userId);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }

        SysRole role = roleRepository.selectById(user.getRoleId());
        String roleCode = role != null ? role.getRoleCode() : "";
        String roleName = role != null ? role.getRoleName() : "";

        LoginResponse response = new LoginResponse(
                null,
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                roleCode,
                roleName
        );

        return ApiResponse.success(response);
    }
}
