package ztt. fixedassetmanagement.controller;

import io.swagger. v3.oas.annotations.Operation;
import io.swagger.v3. oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework. web.bind.annotation.*;
import ztt.fixedassetmanagement.common. PageResult;
import ztt.fixedassetmanagement. common.Result;
import ztt.fixedassetmanagement.entity.RoomResource;
import ztt.fixedassetmanagement.service.RoomService;

import java.util.List;
import java. util.Map;

@Tag(name = "房间管理")
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "获取房间列表")
    @GetMapping
    public Result<PageResult<RoomResource>> getRoomList(@RequestParam Map<String, Object> params) {
        return Result.success(roomService.getRoomList(params));
    }

    @Operation(summary = "获取房间详情")
    @GetMapping("/{id}")
    public Result<RoomResource> getRoomDetail(@PathVariable String id) {
        return Result.success(roomService.getRoomDetail(id));
    }

    @Operation(summary = "新增房间")
    @PostMapping
    public Result<Void> createRoom(@RequestBody RoomResource room) {
        roomService. createRoom(room);
        return Result. success();
    }

    @Operation(summary = "更新房间")
    @PutMapping("/{id}")
    public Result<Void> updateRoom(@PathVariable String id, @RequestBody RoomResource room) {
        roomService. updateRoom(id, room);
        return Result.success();
    }

    @Operation(summary = "删除房间")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRoom(@PathVariable String id) {
        roomService.deleteRoom(id);
        return Result.success();
    }

    @Operation(summary = "获取空闲房间")
    @GetMapping("/idle")
    public Result<List<RoomResource>> getIdleRooms(@RequestParam Map<String, Object> params) {
        return Result.success(roomService.getIdleRooms(params));
    }

    @Operation(summary = "获取教室占用情况")
    @GetMapping("/classroom-occupancy")
    public Result<List<Map<String, Object>>> getClassroomOccupancy(@RequestParam Map<String, Object> params) {
        return Result.success(roomService.getClassroomOccupancy(params));
    }
}