package ztt.fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("vehicle")
public class Vehicle {

    @TableId(value = "vehicle_id", type = IdType.INPUT)
    private String vehicleId;

    private String plateNo;

    // 前端兼容字段
    @TableField(exist = false)
    private String plateNumber;

    @TableField(exist = false)
    private String vehicleType;

    @TableField(exist = false)
    private String brand;

    private String model;

    private Integer seatNum;

    private BigDecimal originalValue;

    private BigDecimal netValue;

    private Integer depreciationYears;

    private LocalDate purchaseDate;

    private Integer status;

    private BigDecimal standardFuel;

    private String responsiblePerson;

    private Integer mileAge;

    @Version
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;
}