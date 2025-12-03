package ztt.fixedassetmanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement.entity.BuildingArchive;
import ztt.fixedassetmanagement.entity.EquipmentArchive;
import ztt.fixedassetmanagement.entity.RoomResource;
import ztt.fixedassetmanagement.entity.Vehicle;
import ztt.fixedassetmanagement.mapper.BuildingArchiveMapper;
import ztt.fixedassetmanagement.mapper.EquipmentArchiveMapper;
import ztt.fixedassetmanagement.mapper.RoomResourceMapper;
import ztt.fixedassetmanagement.mapper.VehicleMapper;
import ztt.fixedassetmanagement.util.IdGenerator;

import java.util.Map;

@Tag(name = "档案管理")
@RestController
@RequestMapping("/api/archive")
@RequiredArgsConstructor
public class ArchiveController {

    private final EquipmentArchiveMapper equipmentArchiveMapper;
    private final BuildingArchiveMapper buildingArchiveMapper;
    private final RoomResourceMapper roomResourceMapper;
    private final VehicleMapper vehicleMapper;

    // ==================== 器材档案 ====================

    @Operation(summary = "获取器材档案列表")
    @GetMapping("/equipment")
    public Result<PageResult<EquipmentArchive>> getEquipmentArchiveList(@RequestParam Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<EquipmentArchive> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText((String) params.get("equipmentName"))) {
            wrapper.like(EquipmentArchive::getEquipmentName, params.get("equipmentName"));
        }
        if (StringUtils.hasText((String) params.get("equipmentType"))) {
            wrapper.eq(EquipmentArchive::getEquipmentType, params.get("equipmentType"));
        }
        wrapper.orderByDesc(EquipmentArchive::getCreatedAt);

        Page<EquipmentArchive> pageResult = equipmentArchiveMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return Result.success(PageResult.of(pageResult.getRecords(), pageResult.getTotal(), page, pageSize));
    }

    @Operation(summary = "新增器材档案")
    @PostMapping("/equipment")
    public Result<Void> createEquipmentArchive(@RequestBody EquipmentArchive archive) {
        if (archive.getEquipmentId() == null || archive.getEquipmentId().isEmpty()) {
            archive.setEquipmentId(IdGenerator.generateEquipmentId());
        }
        // 设置必填字段默认值
        if (archive.getEquipmentType() == null || archive.getEquipmentType().isEmpty()) {
            archive.setEquipmentType("通用设备");
        }
        if (archive.getOriginalValue() == null) {
            archive.setOriginalValue(java.math.BigDecimal.ZERO);
        }
        if (archive.getPurchaseDate() == null) {
            archive.setPurchaseDate(java.time.LocalDate.now());
        }
        if (archive.getDepreciationYears() == null) {
            archive.setDepreciationYears(5);
        }
        if (archive.getStatus() == null || archive.getStatus().isEmpty()) {
            archive.setStatus("正常");
        }
        equipmentArchiveMapper.insert(archive);
        return Result.success();
    }

    @Operation(summary = "更新器材档案")
    @PutMapping("/equipment/{id}")
    public Result<Void> updateEquipmentArchive(@PathVariable String id, @RequestBody EquipmentArchive archive) {
        archive.setEquipmentId(id);
        equipmentArchiveMapper.updateById(archive);
        return Result.success();
    }

    @Operation(summary = "删除器材档案")
    @DeleteMapping("/equipment/{id}")
    public Result<Void> deleteEquipmentArchive(@PathVariable String id) {
        equipmentArchiveMapper.deleteById(id);
        return Result.success();
    }

    // ==================== 建筑档案 ====================

    @Operation(summary = "获取所有建筑(下拉框用)")
    @GetMapping("/building/all")
    public Result<java.util.List<BuildingArchive>> getAllBuildings() {
        LambdaQueryWrapper<BuildingArchive> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(BuildingArchive::getBuildingId, BuildingArchive::getBuildingName);
        wrapper.orderByAsc(BuildingArchive::getBuildingName);
        return Result.success(buildingArchiveMapper.selectList(wrapper));
    }

    @Operation(summary = "获取建筑档案列表")
    @GetMapping("/building")
    public Result<PageResult<BuildingArchive>> getBuildingArchiveList(@RequestParam Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<BuildingArchive> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText((String) params.get("buildingName"))) {
            wrapper.like(BuildingArchive::getBuildingName, params.get("buildingName"));
        }
        if (StringUtils.hasText((String) params.get("buildingType"))) {
            wrapper.eq(BuildingArchive::getBuildingType, params.get("buildingType"));
        }
        wrapper.orderByDesc(BuildingArchive::getCreatedAt);

        Page<BuildingArchive> pageResult = buildingArchiveMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return Result.success(PageResult.of(pageResult.getRecords(), pageResult.getTotal(), page, pageSize));
    }

    @Operation(summary = "新增建筑档案")
    @PostMapping("/building")
    public Result<Void> createBuildingArchive(@RequestBody BuildingArchive archive) {
        if (archive.getBuildingId() == null || archive.getBuildingId().isEmpty()) {
            archive.setBuildingId(IdGenerator.generateBuildingId());
        }
        // 处理前端totalArea到数据库area的映射
        if (archive.getArea() == null && archive.getTotalArea() != null) {
            archive.setArea(archive.getTotalArea());
        }
        // 设置必填字段默认值
        if (archive.getBuildingType() == null || archive.getBuildingType().isEmpty()) {
            archive.setBuildingType("其他");
        }
        if (archive.getArea() == null) {
            archive.setArea(java.math.BigDecimal.ZERO);
        }
        if (archive.getBuildYear() == null) {
            archive.setBuildYear(java.time.LocalDate.now().getYear());
        }
        if (archive.getDepreciationYears() == null) {
            archive.setDepreciationYears(20);
        }
        if (archive.getStatus() == null || archive.getStatus().isEmpty()) {
            archive.setStatus("正常");
        }
        buildingArchiveMapper.insert(archive);
        return Result.success();
    }

    @Operation(summary = "更新建筑档案")
    @PutMapping("/building/{id}")
    public Result<Void> updateBuildingArchive(@PathVariable String id, @RequestBody BuildingArchive archive) {
        archive.setBuildingId(id);
        buildingArchiveMapper.updateById(archive);
        return Result.success();
    }

    @Operation(summary = "删除建筑档案")
    @DeleteMapping("/building/{id}")
    public Result<Void> deleteBuildingArchive(@PathVariable String id) {
        buildingArchiveMapper.deleteById(id);
        return Result.success();
    }

    // ==================== 房间档案 ====================

    @Operation(summary = "获取房间档案列表")
    @GetMapping("/room")
    public Result<PageResult<RoomResource>> getRoomArchiveList(@RequestParam Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<RoomResource> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText((String) params.get("roomName"))) {
            wrapper.like(RoomResource::getRoomName, params.get("roomName"));
        }
        if (StringUtils.hasText((String) params.get("buildingId"))) {
            wrapper.eq(RoomResource::getBuildingId, params.get("buildingId"));
        }
        wrapper.orderByDesc(RoomResource::getCreatedAt);

        Page<RoomResource> pageResult = roomResourceMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return Result.success(PageResult.of(pageResult.getRecords(), pageResult.getTotal(), page, pageSize));
    }

    @Operation(summary = "新增房间档案")
    @PostMapping("/room")
    public Result<Void> createRoomArchive(@RequestBody RoomResource room) {
        if (room.getRoomId() == null || room.getRoomId().isEmpty()) {
            room.setRoomId(IdGenerator.generateRoomId());
        }
        // roomNumber是必填字段，从roomName或roomId生成
        if (room.getRoomNumber() == null || room.getRoomNumber().isEmpty()) {
            room.setRoomNumber(room.getRoomName() != null ? room.getRoomName() : room.getRoomId());
        }
        if (room.getStatus() == null || room.getStatus().isEmpty()) {
            room.setStatus("正常");
        }
        roomResourceMapper.insert(room);
        return Result.success();
    }

    @Operation(summary = "更新房间档案")
    @PutMapping("/room/{id}")
    public Result<Void> updateRoomArchive(@PathVariable String id, @RequestBody RoomResource room) {
        room.setRoomId(id);
        roomResourceMapper.updateById(room);
        return Result.success();
    }

    @Operation(summary = "删除房间档案")
    @DeleteMapping("/room/{id}")
    public Result<Void> deleteRoomArchive(@PathVariable String id) {
        roomResourceMapper.deleteById(id);
        return Result.success();
    }

    // ==================== 车辆档案 ====================

    @Operation(summary = "获取车辆档案列表")
    @GetMapping("/vehicle")
    public Result<PageResult<Vehicle>> getVehicleArchiveList(@RequestParam Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText((String) params.get("plateNumber"))) {
            wrapper.like(Vehicle::getPlateNo, params.get("plateNumber"));
        }
        // vehicleType字段在数据库中可能不存在，跳过该条件
        wrapper.orderByDesc(Vehicle::getCreatedAt);

        Page<Vehicle> pageResult = vehicleMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return Result.success(PageResult.of(pageResult.getRecords(), pageResult.getTotal(), page, pageSize));
    }

    @Operation(summary = "新增车辆档案")
    @PostMapping("/vehicle")
    public Result<Void> createVehicleArchive(@RequestBody Vehicle vehicle) {
        if (vehicle.getVehicleId() == null || vehicle.getVehicleId().isEmpty()) {
            vehicle.setVehicleId(IdGenerator.generateVehicleId());
        }
        // 处理前端plateNumber到数据库plateNo的映射
        if (vehicle.getPlateNo() == null && vehicle.getPlateNumber() != null) {
            vehicle.setPlateNo(vehicle.getPlateNumber());
        }
        // 设置默认状态 (status在数据库中是int类型)
        if (vehicle.getStatus() == null) {
            vehicle.setStatus(1);  // 1表示正常
        }
        vehicleMapper.insert(vehicle);
        return Result.success();
    }

    @Operation(summary = "更新车辆档案")
    @PutMapping("/vehicle/{id}")
    public Result<Void> updateVehicleArchive(@PathVariable String id, @RequestBody Vehicle vehicle) {
        vehicle.setVehicleId(id);
        vehicleMapper.updateById(vehicle);
        return Result.success();
    }

    @Operation(summary = "删除车辆档案")
    @DeleteMapping("/vehicle/{id}")
    public Result<Void> deleteVehicleArchive(@PathVariable String id) {
        vehicleMapper.deleteById(id);
        return Result.success();
    }
}
