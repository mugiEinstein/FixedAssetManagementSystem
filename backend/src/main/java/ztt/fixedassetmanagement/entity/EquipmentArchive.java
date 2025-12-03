package ztt.fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("equipment_archive")
public class EquipmentArchive {

    @TableId(value = "equipment_id", type = IdType.INPUT)
    private String equipmentId;

    private String equipmentName;

    private String model;

    private String equipmentType;

    private String manufacturer;

    private String location;

    private String responsiblePerson;

    private BigDecimal originalValue;

    private BigDecimal netValue;

    private LocalDate purchaseDate;

    private Integer depreciationYears;

    private String maintenanceCycle;

    private BigDecimal dailyRent;

    private String storageRequirements;

    private String status;

    @Version
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;
}