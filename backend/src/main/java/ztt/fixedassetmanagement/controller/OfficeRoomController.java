package ztt.fixedassetmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement.entity.FacultyOfficeAlloc;
import ztt.fixedassetmanagement.service.OfficeRoomService;

import java.util.Map;

@Tag(name = "办公用房管理")
@RestController
@RequestMapping("/api/office-room")
@RequiredArgsConstructor
public class OfficeRoomController {

    private final OfficeRoomService officeRoomService;

    @Operation(summary = "获取办公用房申请列表")
    @GetMapping
    public Result<PageResult<Map<String, Object>>> getOfficeRoomList(@RequestParam Map<String, Object> params) {
        return Result.success(officeRoomService.getOfficeRoomList(params));
    }

    @Operation(summary = "提交办公用房申请")
    @PostMapping
    public Result<Void> submitApplication(@RequestBody FacultyOfficeAlloc alloc) {
        officeRoomService.submitApplication(alloc);
        return Result.success();
    }

    @Operation(summary = "审核办公用房申请")
    @PostMapping("/{id}/review")
    public Result<Void> reviewApplication(@PathVariable String id, @RequestBody Map<String, Object> data) {
        officeRoomService.reviewApplication(id, data);
        return Result.success();
    }

    @Operation(summary = "退还办公用房")
    @PostMapping("/{id}/return")
    public Result<Void> returnOfficeRoom(@PathVariable String id, @RequestBody(required = false) Map<String, Object> data) {
        officeRoomService.returnOfficeRoom(id, data);
        return Result.success();
    }
}
