package ztt.fixedassetmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement.entity.StudentDormAlloc;
import ztt.fixedassetmanagement.entity.HousingApplication;
import ztt.fixedassetmanagement.service.StudentHousingService;

import java.util.Map;

@Tag(name = "学生住宿管理")
@RestController
@RequestMapping("/api/student-housing")
@RequiredArgsConstructor
public class StudentHousingController {

    private final StudentHousingService studentHousingService;

    @Operation(summary = "获取学生住宿列表")
    @GetMapping
    public Result<PageResult<StudentDormAlloc>> getStudentHousingList(@RequestParam Map<String, Object> params) {
        return Result.success(studentHousingService.getStudentHousingList(params));
    }

    @Operation(summary = "学生入住")
    @PostMapping("/check-in")
    public Result<Void> checkInStudent(@RequestBody StudentDormAlloc alloc) {
        studentHousingService.checkInStudent(alloc);
        return Result.success();
    }

    @Operation(summary = "学生退宿")
    @PostMapping("/{id}/check-out")
    public Result<Void> checkOutStudent(@PathVariable String id, @RequestBody Map<String, Object> data) {
        studentHousingService.checkOutStudent(id, data);
        return Result.success();
    }

    @Operation(summary = "宿舍调换")
    @PostMapping("/{id}/change")
    public Result<Void> changeDormitory(@PathVariable String id, @RequestBody Map<String, Object> data) {
        studentHousingService.changeDormitory(id, data);
        return Result.success();
    }

    // ==================== 住宿申请相关 ====================

    @Operation(summary = "获取住宿申请列表")
    @GetMapping("/applications")
    public Result<PageResult<HousingApplication>> getApplicationList(@RequestParam Map<String, Object> params) {
        return Result.success(studentHousingService.getApplicationList(params));
    }

    @Operation(summary = "提交调换申请")
    @PostMapping("/transfer-apply")
    public Result<Void> submitTransferApplication(@RequestBody Map<String, Object> data) {
        studentHousingService.submitTransferApplication(data);
        return Result.success();
    }

    @Operation(summary = "提交外宿申请")
    @PostMapping("/leave-apply")
    public Result<Void> submitLeaveApplication(@RequestBody Map<String, Object> data) {
        studentHousingService.submitLeaveApplication(data);
        return Result.success();
    }

    @Operation(summary = "提交退宿申请")
    @PostMapping("/checkout-apply")
    public Result<Void> submitCheckoutApplication(@RequestBody Map<String, Object> data) {
        studentHousingService.submitCheckoutApplication(data);
        return Result.success();
    }

    @Operation(summary = "审核住宿申请")
    @PostMapping("/applications/{id}/review")
    public Result<Void> reviewApplication(@PathVariable String id, @RequestBody Map<String, Object> data) {
        studentHousingService.reviewApplication(id, data);
        return Result.success();
    }

    @Operation(summary = "获取我的住宿信息")
    @GetMapping("/my-info")
    public Result<Map<String, Object>> getMyHousingInfo(@RequestParam String userId) {
        return Result.success(studentHousingService.getMyHousingInfo(userId));
    }
}