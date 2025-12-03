package ztt.fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java. math.BigDecimal;
import java. time.LocalDateTime;

@Data
@TableName("fee_record")
public class FeeRecord {

    @TableId(value = "fee_id", type = IdType. INPUT)
    private String feeId;

    private String feeType;

    private String businessType;

    private String businessId;

    private String payerId;

    private String payerName;

    private String payerType;

    private BigDecimal amount;

    private String status;

    private String paymentMethod;

    private LocalDateTime paymentTime;

    private BigDecimal refundAmount;

    private LocalDateTime refundTime;

    private String remarks;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;
}