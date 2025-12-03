package ztt.fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java. math.BigDecimal;
import java.time.LocalDate;
import java. time.LocalDateTime;

@Data
@TableName("maintenance_record")
public class MaintenanceRecord {

    @TableId(value = "maintenance_id", type = IdType.INPUT)
    private String maintenanceId;

    private String assetId;

    private String assetType;

    private String assetName;

    private String location;

    private String faultDescription;

    private String faultReason;

    private String reporterId;

    private String reporterName;

    private String reporterDepartment;

    private LocalDateTime reportTime;

    private String maintenancePerson;

    private LocalDate maintenanceDate;

    private String maintenanceReason;

    private String maintenanceDescription;

    private String partsReplaced;

    private BigDecimal cost;

    private String maintenanceType;

    private String status;

    private LocalDateTime completionTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;

    // 前端字段映射
    @TableField(exist = false)
    private String reporterDept;  // 前端传 reporterDept，映射到 reporterDepartment

    // 提供 setter 方法来自动映射
    public void setReporterDept(String reporterDept) {
        this.reporterDept = reporterDept;
        if (this.reporterDepartment == null || this.reporterDepartment.isEmpty()) {
            this.reporterDepartment = reporterDept;
        }
    }
}