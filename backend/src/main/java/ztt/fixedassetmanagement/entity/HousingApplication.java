package ztt.fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("housing_application")
public class HousingApplication {

    @TableId(value = "application_id", type = IdType.INPUT)
    private String applicationId;

    private String studentId;

    private String studentNo;

    private String studentName;

    private String department;

    private String grade;

    /**
     * 申请类型: transfer(调换)/leave(外宿)/checkout(退宿)
     */
    private String applicationType;

    private String currentDorm;

    private String reason;

    private String reasonDetail;

    private String preferredDorm;

    private LocalDate leaveStartDate;

    private LocalDate leaveEndDate;

    private LocalDate exitDate;

    /**
     * 状态: pending/approved/rejected
     */
    private String status;

    private LocalDateTime applyTime;

    private LocalDateTime reviewTime;

    private String reviewer;

    private String reviewReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;
}
