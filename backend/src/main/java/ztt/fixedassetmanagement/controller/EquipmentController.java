package ztt.fixedassetmanagement. controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas. annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ztt. fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement. entity.EquipmentArchive;
import ztt.fixedassetmanagement.service.EquipmentService;

import java.util.List;
import java. util.Map;

@Tag(name = "器材管理")
@RestController
@RequestMapping("/api/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Operation(summary = "获取器材列表")
    @GetMapping
    public Result<PageResult<EquipmentArchive>> getEquipmentList(@RequestParam Map<String, Object> params) {
        PageResult<EquipmentArchive> result = equipmentService.getEquipmentList(params);
        return Result.success(result);
    }

    @Operation(summary = "获取器材详情")
    @GetMapping("/{id}")
    public Result<EquipmentArchive> getEquipmentDetail(@PathVariable String id) {
        EquipmentArchive equipment = equipmentService.getEquipmentDetail(id);
        return Result.success(equipment);
    }

    @Operation(summary = "获取器材净值")
    @GetMapping("/{id}/net-value")
    public Result<Map<String, Object>> getEquipmentNetValue(@PathVariable String id) {
        Map<String, Object> netValue = equipmentService.getEquipmentNetValue(id);
        return Result.success(netValue);
    }

    @Operation(summary = "新增器材")
    @PostMapping
    public Result<Void> createEquipment(@RequestBody EquipmentArchive equipment) {
        equipmentService.createEquipment(equipment);
        return Result.success();
    }

    @Operation(summary = "更新器材")
    @PutMapping("/{id}")
    public Result<Void> updateEquipment(@PathVariable String id, @RequestBody EquipmentArchive equipment) {
        equipmentService.updateEquipment(id, equipment);
        return Result.success();
    }

    @Operation(summary = "删除器材")
    @DeleteMapping("/{id}")
    public Result<Void> deleteEquipment(@PathVariable String id) {
        equipmentService.deleteEquipment(id);
        return Result.success();
    }

    @Operation(summary = "获取可借用器材")
    @GetMapping("/idle")
    public Result<List<EquipmentArchive>> getIdleEquipments(@RequestParam(required = false) String name) {
        // 返回空闲和使用中的器材供借用选择(排除维修中和已报废)
        List<EquipmentArchive> list = equipmentService.lambdaQuery()
                .notIn(EquipmentArchive::getStatus, "维修中", "已报废", "MAINTENANCE", "SCRAPPED")
                .like(name != null, EquipmentArchive::getEquipmentName, name)
                .list();
        return Result.success(list);
    }
}