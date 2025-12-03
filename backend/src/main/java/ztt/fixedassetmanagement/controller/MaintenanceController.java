package ztt.fixedassetmanagement.controller;

import io. swagger.v3. oas.annotations. Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org. springframework.web.bind.annotation.*;
import ztt.fixedassetmanagement. common.PageResult;
import ztt. fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement.entity. MaintenanceRecord;
import ztt. fixedassetmanagement.service.MaintenanceService;

import java.util. List;
import java. util.Map;

@Tag(name = "维保管理")
@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @Operation(summary = "获取维保列表")
    @GetMapping
    public Result<PageResult<MaintenanceRecord>> getMaintenanceList(@RequestParam Map<String, Object> params) {
        return Result.success(maintenanceService.getMaintenanceList(params));
    }

    @Operation(summary = "提交维保申请")
    @PostMapping
    public Result<Void> submitMaintenanceRequest(@RequestBody MaintenanceRecord record) {
        maintenanceService.submitMaintenanceRequest(record);
        return Result. success();
    }

    @Operation(summary = "指派维护人员")
    @PostMapping("/{id}/assign")
    public Result<Void> assignMaintainer(@PathVariable String id, @RequestBody Map<String, Object> data) {
        maintenanceService.assignMaintainer(id, data);
        return Result.success();
    }

    @Operation(summary = "开始维修")
    @PostMapping("/{id}/start")
    public Result<Void> startMaintenance(@PathVariable String id) {
        maintenanceService.startMaintenance(id);
        return Result. success();
    }

    @Operation(summary = "完成维修")
    @PostMapping("/{id}/complete")
    public Result<Void> completeMaintenance(@PathVariable String id, @RequestBody Map<String, Object> data) {
        maintenanceService.completeMaintenance(id, data);
        return Result.success();
    }

    @Operation(summary = "获取维护人员列表")
    @GetMapping("/maintainers")
    public Result<List<Map<String, Object>>> getMaintainerList() {
        return Result.success(maintenanceService.getMaintainerList());
    }
}