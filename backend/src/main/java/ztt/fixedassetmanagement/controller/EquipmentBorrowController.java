package ztt. fixedassetmanagement.controller;

import io.swagger.v3. oas.annotations.Operation;
import io. swagger.v3. oas.annotations. tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ztt. fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.common. Result;
import ztt.fixedassetmanagement.entity.InternalEquipmentBorrow;
import ztt. fixedassetmanagement.entity.OutboundEquipmentBorrow;
import ztt.fixedassetmanagement.service.EquipmentBorrowService;

import java.util.Map;

@Tag(name = "器材借用管理")
@RestController
@RequestMapping("/api/equipment-borrow")
@RequiredArgsConstructor
public class EquipmentBorrowController {

    private final EquipmentBorrowService borrowService;

    // ==================== 校外借用 ====================

    @Operation(summary = "获取校外借用列表")
    @GetMapping("/external")
    public Result<PageResult<OutboundEquipmentBorrow>> getExternalBorrowList(@RequestParam Map<String, Object> params) {
        return Result.success(borrowService.getExternalBorrowList(params));
    }

    @Operation(summary = "获取校外借用详情")
    @GetMapping("/external/{id}")
    public Result<OutboundEquipmentBorrow> getExternalBorrowDetail(@PathVariable String id) {
        return Result.success(borrowService.getExternalBorrowDetail(id));
    }

    @Operation(summary = "提交校外借用申请")
    @PostMapping("/external")
    public Result<Void> submitExternalBorrow(@RequestBody OutboundEquipmentBorrow borrow) {
        borrowService.submitExternalBorrow(borrow);
        return Result.success();
    }

    @Operation(summary = "校外借用-资质审核")
    @PostMapping("/external/{id}/qualification-review")
    public Result<Void> reviewExternalQualification(@PathVariable String id, @RequestBody Map<String, Object> data) {
        borrowService.reviewExternalQualification(id, data);
        return Result.success();
    }

    @Operation(summary = "校外借用-器材审核")
    @PostMapping("/external/{id}/equipment-review")
    public Result<Void> reviewExternalEquipment(@PathVariable String id, @RequestBody Map<String, Object> data) {
        borrowService.reviewExternalEquipment(id, data);
        return Result.success();
    }

    @Operation(summary = "校外借用-确认缴费")
    @PostMapping("/external/{id}/confirm-payment")
    public Result<Void> confirmExternalPayment(@PathVariable String id) {
        borrowService.confirmExternalPayment(id);
        return Result.success();
    }

    @Operation(summary = "校外借用-确认领用")
    @PostMapping("/external/{id}/confirm-pickup")
    public Result<Void> confirmExternalPickup(@PathVariable String id) {
        borrowService.confirmExternalPickup(id);
        return Result.success();
    }

    @Operation(summary = "校外借用-归还验收")
    @PostMapping("/external/{id}/return")
    public Result<Void> returnExternalEquipment(@PathVariable String id, @RequestBody Map<String, Object> data) {
        borrowService.returnExternalEquipment(id, data);
        return Result.success();
    }

    @Operation(summary = "校外借用-申请续借")
    @PostMapping("/external/{id}/renewal")
    public Result<Void> applyExternalRenewal(@PathVariable String id, @RequestBody Map<String, Object> data) {
        borrowService.applyExternalRenewal(id, data);
        return Result.success();
    }

    // ==================== 校内借用 ====================

    @Operation(summary = "获取校内借用列表")
    @GetMapping("/internal")
    public Result<PageResult<InternalEquipmentBorrow>> getInternalBorrowList(@RequestParam Map<String, Object> params) {
        return Result.success(borrowService.getInternalBorrowList(params));
    }

    @Operation(summary = "获取校内借用详情")
    @GetMapping("/internal/{id}")
    public Result<InternalEquipmentBorrow> getInternalBorrowDetail(@PathVariable String id) {
        return Result.success(borrowService.getInternalBorrowDetail(id));
    }

    @Operation(summary = "提交校内借用申请")
    @PostMapping("/internal")
    public Result<Void> submitInternalBorrow(@RequestBody InternalEquipmentBorrow borrow) {
        borrowService. submitInternalBorrow(borrow);
        return Result.success();
    }

    @Operation(summary = "校内借用-权限审核")
    @PostMapping("/internal/{id}/permission-review")
    public Result<Void> reviewInternalPermission(@PathVariable String id, @RequestBody Map<String, Object> data) {
        borrowService.reviewInternalPermission(id, data);
        return Result.success();
    }

    @Operation(summary = "校内借用-器材审核")
    @PostMapping("/internal/{id}/equipment-review")
    public Result<Void> reviewInternalEquipment(@PathVariable String id, @RequestBody Map<String, Object> data) {
        borrowService. reviewInternalEquipment(id, data);
        return Result.success();
    }

    @Operation(summary = "校内借用-确认领用")
    @PostMapping("/internal/{id}/confirm-pickup")
    public Result<Void> confirmInternalPickup(@PathVariable String id) {
        borrowService.confirmInternalPickup(id);
        return Result.success();
    }

    @Operation(summary = "校内借用-归还验收")
    @PostMapping("/internal/{id}/return")
    public Result<Void> returnInternalEquipment(@PathVariable String id, @RequestBody Map<String, Object> data) {
        borrowService.returnInternalEquipment(id, data);
        return Result.success();
    }

    // ==================== 我的借用 ====================

    @Operation(summary = "获取我的借用列表")
    @GetMapping("/my")
    public Result<PageResult<InternalEquipmentBorrow>> getMyBorrowList(@RequestParam Map<String, Object> params) {
        // 从请求中获取当前用户ID，实际应从安全上下文获取
        return Result.success(borrowService.getInternalBorrowList(params));
    }

    @Operation(summary = "取消我的借用申请")
    @PostMapping("/my/{id}/cancel")
    public Result<Void> cancelMyBorrow(@PathVariable String id) {
        borrowService.cancelBorrow(id);
        return Result.success();
    }
}