package ztt.fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("internal_event_booking")
public class InternalEventBooking {

    @TableId(value = "event_id", type = IdType.INPUT)
    private String eventId;

    private String organizerDept;

    private String venueId;

    private LocalDate eventDate;

    private BigDecimal discountFee;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;

    // 以下字段不在数据库中，仅用于业务逻辑
    @TableField(exist = false)
    private String applicantName;

    @TableField(exist = false)
    private String eventName;

    @TableField(exist = false)
    private Integer expectedAttendees;

    @TableField(exist = false)
    private String startTime;

    @TableField(exist = false)
    private String endTime;

    @TableField(exist = false)
    private BigDecimal duration;

    @TableField(exist = false)
    private BigDecimal venueRent;

    @TableField(exist = false)
    private String departmentApproval;

    @TableField(exist = false)
    private String venueName;
}