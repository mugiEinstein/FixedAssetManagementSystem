package ztt.fixedassetmanagement. entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time. LocalDateTime;

@Data
@TableName("vehicle_borrow")
public class VehicleBorrow {

    @TableId(value = "borrow_id", type = IdType.INPUT)
    private String borrowId;

    private String vehicleId;

    private String borrowerId;

    private LocalDate borrowDate;

    private LocalDate returnDate;

    private LocalDate actualReturnDate;

    private String borrowPurpose;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private String plateNo;

    @TableField(exist = false)
    private String borrowerName;
}