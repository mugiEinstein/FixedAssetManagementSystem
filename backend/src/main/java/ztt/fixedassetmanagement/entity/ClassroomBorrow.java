package ztt. fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("classroom_borrow")
public class ClassroomBorrow {

    @TableId(value = "borrow_id", type = IdType.INPUT)
    private String borrowId;

    private String applicantDept;

    private String classroomId;

    private String borrowPurpose;

    private String borrowTime;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;

    // 以下字段不在数据库中，仅用于业务逻辑和前端字段映射
    private String applicantName;  // 申请人姓名 - 数据库字段

    @TableField(exist = false)
    private LocalDate useDate;

    @TableField(exist = false)
    private String startTime;

    @TableField(exist = false)
    private String endTime;

    @TableField(exist = false)
    private Integer requiredCapacity;

    @TableField(exist = false)
    private String equipmentRequirements;

    @TableField(exist = false)
    private String departmentApproval;

    @TableField(exist = false)
    private String roomCode;

    @TableField(exist = false)
    private String buildingName;

    // 前端字段映射 - 接收前端传来的数据
    @TableField(exist = false)
    private String roomId;  // 前端传 roomId，映射到 classroomId

    @TableField(exist = false)
    private String roomName;  // 教室名称

    @TableField(exist = false)
    private String purpose;  // 前端传 purpose，映射到 borrowPurpose

    private LocalDate borrowDate;  // 借用日期 - 数据库字段

    private Integer expectedAttendees;  // 预计人数 - 数据库字段

    @TableField(exist = false)
    private String borrowDateStr;  // 前端传来的日期字符串

    @TableField(exist = false)
    private String equipmentNeeds;  // 设备需求
}