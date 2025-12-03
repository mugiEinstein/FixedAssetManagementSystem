package ztt.fixedassetmanagement. entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java. math.BigDecimal;
import java. time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("asset_inventory")
public class AssetInventory {

    @TableId(value = "inventory_id", type = IdType.INPUT)
    private String inventoryId;

    private String planId;

    private String assetId;

    private String assetType;

    private String assetName;

    private LocalDate inventoryDate;

    private String physicalStatus;

    private String differenceReason;

    private BigDecimal assessedValue;

    private BigDecimal depreciation;

    private BigDecimal netValue;

    private BigDecimal valueAdjustment;

    private String inventoryStatus;

    @TableField(fill = FieldFill. INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;
}