package ztt.fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("building_archive")
public class BuildingArchive {

    @TableId(value = "building_id", type = IdType.INPUT)
    private String buildingId;

    private String buildingName;

    private String buildingType;

    private BigDecimal area;

    private Integer buildYear;

    private Integer depreciationYears;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;

    // ===== 以下字段数据库中不存在，仅用于前端数据传递 =====
    @TableField(exist = false)
    private String address;

    @TableField(exist = false)
    private BigDecimal totalArea;  // 前端使用，映射到area

    @TableField(exist = false)
    private Integer floors;

    @TableField(exist = false)
    private BigDecimal originalValue;

    @TableField(exist = false)
    private BigDecimal netValue;

    @TableField(exist = false)
    private String ownership;

    @TableField(exist = false)
    private String remarks;

    @TableField(exist = false)
    private String archiveStatus;

    // 设置totalArea时同步设置area
    public void setTotalArea(BigDecimal totalArea) {
        this.totalArea = totalArea;
        this.area = totalArea;
    }
}