package ztt.fixedassetmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas. annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ztt. fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement.entity. FacultyDormAlloc;
import ztt.fixedassetmanagement. service.StaffHousingService;

import java.util.Map;

@Tag(name = "教职工住宿管理")
@RestController
@RequestMapping("/api/staff-housing")
@RequiredArgsConstructor
public class StaffHousingController {

    private final StaffHousingService staffHousingService;

    @Operation(summary = "获取教职工住宿列表")
    @GetMapping
    public Result<PageResult<FacultyDormAlloc>> getStaffHousingList(@RequestParam Map<String, Object> params) {
        return Result.success(staffHousingService.getStaffHousingList(params));
    }

    @Operation(summary = "提交住宿申请")
    @PostMapping("/applications")
    public Result<Void> submitApplication(@RequestBody FacultyDormAlloc alloc) {
        staffHousingService.submitApplication(alloc);
        return Result.success();
    }

    @Operation(summary = "资格审核")
    @PostMapping("/applications/{id}/qualification-review")
    public Result<Void> reviewQualification(@PathVariable String id, @RequestBody Map<String, Object> data) {
        staffHousingService.reviewQualification(id, data);
        return Result.success();
    }

    @Operation(summary = "财务审核")
    @PostMapping("/applications/{id}/finance-review")
    public Result<Void> reviewFinance(@PathVariable String id, @RequestBody Map<String, Object> data) {
        staffHousingService. reviewFinance(id, data);
        return Result.success();
    }

    @Operation(summary = "分配房源")
    @PostMapping("/applications/{id}/allocate")
    public Result<Void> allocateRoom(@PathVariable String id, @RequestBody Map<String, Object> data) {
        staffHousingService.allocateRoom(id, data);
        return Result. success();
    }

    @Operation(summary = "签订合同")
    @PostMapping("/applications/{id}/sign-contract")
    public Result<Void> signContract(@PathVariable String id, @RequestBody Map<String, Object> data) {
        staffHousingService.signContract(id, data);
        return Result.success();
    }
}