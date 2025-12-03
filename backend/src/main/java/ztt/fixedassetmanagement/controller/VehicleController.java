package ztt. fixedassetmanagement.controller;

import io.swagger. v3.oas.annotations.Operation;
import io.swagger.v3. oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework. web.bind.annotation.*;
import ztt.fixedassetmanagement.common. PageResult;
import ztt.fixedassetmanagement. common.Result;
import ztt.fixedassetmanagement.entity.Vehicle;
import ztt. fixedassetmanagement.entity.VehicleBorrow;
import ztt. fixedassetmanagement.service.VehicleService;

import java.util. List;
import java.util. Map;

@Tag(name = "车辆管理")
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "获取车辆列表")
    @GetMapping
    public Result<PageResult<Vehicle>> getVehicleList(@RequestParam Map<String, Object> params) {
        return Result.success(vehicleService.getVehicleList(params));
    }

    @Operation(summary = "获取车辆详情")
    @GetMapping("/{id}")
    public Result<Vehicle> getVehicleDetail(@PathVariable String id) {
        return Result.success(vehicleService. getVehicleDetail(id));
    }

    @Operation(summary = "新增车辆")
    @PostMapping
    public Result<Void> createVehicle(@RequestBody Vehicle vehicle) {
        vehicleService.createVehicle(vehicle);
        return Result.success();
    }

    @Operation(summary = "更新车辆")
    @PutMapping("/{id}")
    public Result<Void> updateVehicle(@PathVariable String id, @RequestBody Vehicle vehicle) {
        vehicleService.updateVehicle(id, vehicle);
        return Result.success();
    }

    @Operation(summary = "删除车辆")
    @DeleteMapping("/{id}")
    public Result<Void> deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        return Result.success();
    }

    @Operation(summary = "获取空闲车辆")
    @GetMapping("/idle")
    public Result<List<Vehicle>> getIdleVehicles() {
        return Result.success(vehicleService.getIdleVehicles());
    }

    @Operation(summary = "获取车辆借用列表")
    @GetMapping("/borrows")
    public Result<PageResult<VehicleBorrow>> getVehicleBorrowList(@RequestParam Map<String, Object> params) {
        return Result.success(vehicleService.getVehicleBorrowList(params));
    }

    @Operation(summary = "提交车辆借用")
    @PostMapping("/borrows")
    public Result<Void> submitVehicleBorrow(@RequestBody VehicleBorrow borrow) {
        vehicleService. submitVehicleBorrow(borrow);
        return Result. success();
    }

    @Operation(summary = "归还车辆")
    @PostMapping("/borrows/{id}/return")
    public Result<Void> returnVehicle(@PathVariable String id, @RequestBody Map<String, Object> data) {
        vehicleService.returnVehicle(id, data);
        return Result.success();
    }
}