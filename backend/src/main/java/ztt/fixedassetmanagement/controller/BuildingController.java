package ztt.fixedassetmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas. annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ztt. fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement. entity.BuildingArchive;
import ztt.fixedassetmanagement.service.BuildingService;

import java.util.Map;

@Tag(name = "建筑管理")
@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @Operation(summary = "获取建筑列表")
    @GetMapping
    public Result<PageResult<BuildingArchive>> getBuildingList(@RequestParam Map<String, Object> params) {
        return Result.success(buildingService.getBuildingList(params));
    }

    @Operation(summary = "获取建筑详情")
    @GetMapping("/{id}")
    public Result<BuildingArchive> getBuildingDetail(@PathVariable String id) {
        return Result. success(buildingService.getBuildingDetail(id));
    }

    @Operation(summary = "新增建筑")
    @PostMapping
    public Result<Void> createBuilding(@RequestBody BuildingArchive building) {
        buildingService.createBuilding(building);
        return Result.success();
    }

    @Operation(summary = "更新建筑")
    @PutMapping("/{id}")
    public Result<Void> updateBuilding(@PathVariable String id, @RequestBody BuildingArchive building) {
        buildingService.updateBuilding(id, building);
        return Result.success();
    }

    @Operation(summary = "删除建筑")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBuilding(@PathVariable String id) {
        buildingService. deleteBuilding(id);
        return Result.success();
    }
}