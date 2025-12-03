package ztt.fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("faculty_office_alloc")
public class FacultyOfficeAlloc {

    @TableId(value = "allocation_id", type = IdType.INPUT)
    private String allocationId;

    private String userId;

    private String roomId;

    private BigDecimal standardArea;  // 基准面积

    private BigDecimal actualArea;    // 实际面积

    private BigDecimal overAreaFee;   // 超面积费用

    private String status;            // 状态（需执行SQL添加）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;

    // ========== 以下字段通过JOIN sys_user获取，不在本表中 ==========
    @TableField(exist = false)
    private String applicantName;     // 关联sys_user.real_name

    @TableField(exist = false)
    private String department;        // 关联sys_user.dept

    @TableField(exist = false)
    private String staffLevel;        // 关联sys_user.staff_level

    @TableField(exist = false)
    private String roomCode;          // 关联room_resource
}