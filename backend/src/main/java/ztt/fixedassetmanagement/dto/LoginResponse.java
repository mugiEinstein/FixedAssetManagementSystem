package ztt. fixedassetmanagement. dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class LoginResponse {
    private String id;
    private String username;
    private String realName;
    private String role;
    private String department;
    private String title;
    private String token;
}