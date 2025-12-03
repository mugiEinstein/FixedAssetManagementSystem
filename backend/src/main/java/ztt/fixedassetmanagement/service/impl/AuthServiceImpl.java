package ztt.fixedassetmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query. LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype. Service;
import ztt.fixedassetmanagement.common.BusinessException;
import ztt.fixedassetmanagement.dto. LoginRequest;
import ztt.fixedassetmanagement.dto.LoginResponse;
import ztt.fixedassetmanagement.entity.SysUser;
import ztt.fixedassetmanagement. mapper.SysUserMapper;
import ztt.fixedassetmanagement.security.JwtTokenProvider;
import ztt.fixedassetmanagement.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final JwtTokenProvider tokenProvider;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserId, request.getUsername())
               .or()
               .eq(SysUser::getUsername, request.getUsername());
        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码（简化处理，实际应使用加密比对）
        if (! user.getPassword(). equals(request.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 使用数据库中的角色，不允许前端覆盖（安全性）
        String role = user.getRole();

        // 生成Token
        String token = tokenProvider.generateToken(user. getUserId(), user.getUsername(), role);

        return LoginResponse.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(role)
                .department(user.getDept())
                .title(user.getTitle())
                .token(token)
                .build();
    }

    @Override
    public void logout(String token) {
        // 可以将token加入黑名单（Redis实现）
        // 简化处理：前端删除token即可
    }
}