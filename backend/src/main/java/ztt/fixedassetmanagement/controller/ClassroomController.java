package ztt.fixedassetmanagement.controller;

import io.swagger.v3.oas. annotations.Operation;
import io.swagger. v3.oas.annotations.tags. Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ztt. fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement. entity.ClassroomBorrow;
import ztt.fixedassetmanagement. service.ClassroomService;

import java.util.List;
import java.util.Map;

@Tag(name = "教室借用管理")
@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @Operation(summary = "获取教室借用列表")
    @GetMapping("/borrows")
    public Result<PageResult<ClassroomBorrow>> getClassroomBorrowList(@RequestParam Map<String, Object> params) {
        return Result.success(classroomService.getClassroomBorrowList(params));
    }

    @Operation(summary = "提交教室借用申请")
    @PostMapping("/borrows")
    public Result<Void> submitClassroomBorrow(@RequestBody ClassroomBorrow borrow) {
        classroomService.submitClassroomBorrow(borrow);
        return Result.success();
    }

    @Operation(summary = "审核教室借用")
    @PostMapping("/borrows/{id}/review")
    public Result<Void> reviewClassroomBorrow(@PathVariable String id, @RequestBody Map<String, Object> data) {
        classroomService.reviewClassroomBorrow(id, data);
        return Result.success();
    }

    @Operation(summary = "获取教室占用情况")
    @GetMapping("/occupancy")
    public Result<List<Map<String, Object>>> getClassroomOccupancy(@RequestParam Map<String, Object> params) {
        return Result.success(classroomService.getClassroomOccupancy(params));
    }
}