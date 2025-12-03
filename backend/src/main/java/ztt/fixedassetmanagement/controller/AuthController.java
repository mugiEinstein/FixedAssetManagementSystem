package ztt. fixedassetmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas. annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ztt. fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement.dto. LoginRequest;
import ztt.fixedassetmanagement. dto.LoginResponse;
import ztt. fixedassetmanagement.service.AuthService;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService. login(request);
        return Result.success(response);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            authService.logout(token. substring(7));
        }
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/user/info")
    public Result<LoginResponse> getUserInfo() {
        // 从SecurityContext获取当前用户信息
        // 简化实现，实际应从token解析
        return Result.success();
    }
}