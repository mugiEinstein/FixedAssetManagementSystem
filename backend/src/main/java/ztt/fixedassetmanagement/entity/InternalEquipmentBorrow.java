package ztt.fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java. math.BigDecimal;
import java. time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("internal_equipment_borrow")
public class InternalEquipmentBorrow {

    @TableId(value = "borrow_id", type = IdType.INPUT)
    private String borrowId;

    private String userId;

    private String applicantName;

    private String applicantType;

    private String department;

    private String equipmentId;

    private String purpose;

    private LocalDate borrowDate;

    private LocalDate returnDate;

    private LocalDate actualReturnDate;

    private BigDecimal rentAmount;

    private BigDecimal penaltyFee;

    private String status;

    private String permissionApproval;

    private String equipmentApproval;

    @TableField(fill = FieldFill. INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private String equipmentName;
}