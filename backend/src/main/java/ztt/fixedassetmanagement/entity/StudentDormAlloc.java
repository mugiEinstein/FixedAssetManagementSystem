package ztt.fixedassetmanagement. entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java. time.LocalDate;
import java. time.LocalDateTime;

@Data
@TableName("student_dorm_alloc")
public class StudentDormAlloc {

    @TableId(value = "allocation_id", type = IdType.INPUT)
    private String allocationId;

    private String studentId;

    private String studentName;

    private String gender;

    private String college;

    private String major;

    private String grade;

    private String dormId;

    private String buildingName;

    private String dormitoryCode;

    private Integer bedNumber;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String status;

    @TableField(fill = FieldFill. INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;
}