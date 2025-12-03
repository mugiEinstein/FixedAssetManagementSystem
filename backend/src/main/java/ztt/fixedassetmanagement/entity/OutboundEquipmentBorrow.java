package ztt.fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java. math.BigDecimal;
import java. time.LocalDate;
import java. time.LocalDateTime;

@Data
@TableName("outbound_equipment_borrow")
public class OutboundEquipmentBorrow {

    @TableId(value = "borrow_id", type = IdType.INPUT)
    private String borrowId;

    private String applicantUnit;

    private String contactPerson;

    private String contactPhone;

    private String equipmentId;

    private Integer borrowDays;

    private String purpose;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate actualReturnDate;

    private String qualificationFiles;

    private BigDecimal depositAmount;

    private BigDecimal rentAmount;

    private BigDecimal penaltyFee;

    private String status;

    private String qualificationApproval;

    private String equipmentApproval;

    private Integer depositPaid;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill. INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;

    // 关联查询
    @TableField(exist = false)
    private String equipmentName;

    @TableField(exist = false)
    private BigDecimal dailyRent;
}