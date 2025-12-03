package ztt.fixedassetmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas. annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ztt. fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement.entity. AssetInventory;
import ztt. fixedassetmanagement.service.InventoryService;

import java.util.Map;

@Tag(name = "盘点与价值核算")
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "获取盘点记录列表")
    @GetMapping("/records")
    public Result<PageResult<AssetInventory>> getInventoryList(@RequestParam Map<String, Object> params) {
        return Result.success(inventoryService.getInventoryList(params));
    }

    @Operation(summary = "创建盘点记录")
    @PostMapping("/records")
    public Result<Void> createInventoryRecord(@RequestBody AssetInventory record) {
        inventoryService.createInventoryRecord(record);
        return Result. success();
    }

    @Operation(summary = "更新盘点记录")
    @PutMapping("/records/{id}")
    public Result<Void> updateInventoryRecord(@PathVariable String id, @RequestBody AssetInventory record) {
        inventoryService.updateInventoryRecord(id, record);
        return Result. success();
    }

    @Operation(summary = "生成差异报告")
    @GetMapping("/difference-report")
    public Result<Map<String, Object>> generateDifferenceReport(@RequestParam Map<String, Object> params) {
        return Result. success(inventoryService.generateDifferenceReport(params));
    }

    @Operation(summary = "获取价值核算列表")
    @GetMapping("/value-calculation")
    public Result<Map<String, Object>> getValueCalculationList(@RequestParam Map<String, Object> params) {
        return Result.success(inventoryService.getValueCalculationList(params));
    }

    @Operation(summary = "调整资产价值")
    @PostMapping("/adjust-value")
    public Result<Void> adjustAssetValue(@RequestBody Map<String, Object> data) {
        inventoryService.adjustAssetValue(data);
        return Result.success();
    }
}