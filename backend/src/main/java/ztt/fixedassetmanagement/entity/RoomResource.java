package ztt.fixedassetmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("room_resource")
public class RoomResource {

    @TableId(value = "room_id", type = IdType.INPUT)
    private String roomId;

    private String buildingId;

    private String roomNumber;

    private String roomName;

    private String roomType;

    private String usageType;

    private BigDecimal area;

    private Integer capacity;

    private String status;

    @Version
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;

    // ===== 以下字段数据库中不存在，仅用于前端数据传递 =====
    @TableField(exist = false)
    private Integer floor;

    @TableField(exist = false)
    private String equipment;

    @TableField(exist = false)
    private String currentUser;

    @TableField(exist = false)
    private BigDecimal hourlyRate;

    @TableField(exist = false)
    private String buildingName;

    @TableField(exist = false)
    private String roomCode;  // 前端可能使用的别名
}