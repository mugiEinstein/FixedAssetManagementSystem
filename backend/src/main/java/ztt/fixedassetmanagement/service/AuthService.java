package ztt.fixedassetmanagement.service;

import ztt.fixedassetmanagement.dto.LoginRequest;
import ztt.fixedassetmanagement.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
}