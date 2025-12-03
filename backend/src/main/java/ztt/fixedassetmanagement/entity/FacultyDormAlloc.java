package ztt.fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time. LocalDateTime;

@Data
@TableName("faculty_dorm_alloc")
public class FacultyDormAlloc {

    @TableId(value = "allocation_id", type = IdType.INPUT)
    private String allocationId;

    private String userId;

    private String staffName;

    private String department;

    private String dormId;

    private String roomType;

    private BigDecimal area;

    private BigDecimal rentAmount;

    private LocalDate contractStart;

    private LocalDate contractEnd;

    private String status;

    private String qualificationApproval;

    private String financeApproval;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;
}