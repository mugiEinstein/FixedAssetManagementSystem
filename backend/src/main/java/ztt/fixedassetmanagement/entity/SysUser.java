package ztt. fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(value = "user_id", type = IdType. ASSIGN_UUID)
    private String userId;

    private String username;

    private String password;

    private String realName;

    private String role;

    private String staffLevel;

    private String title;

    private String phone;

    private String email;

    private String dept;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}