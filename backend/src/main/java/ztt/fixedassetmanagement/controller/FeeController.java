package ztt.fixedassetmanagement.controller;

import io. swagger.v3. oas.annotations. Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org. springframework.web.bind.annotation.*;
import ztt.fixedassetmanagement. common.PageResult;
import ztt. fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement.entity. FeeRecord;
import ztt.fixedassetmanagement.service.FeeService;

import java.util.Map;

@Tag(name = "费用管理")
@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;

    @Operation(summary = "获取费用记录列表")
    @GetMapping
    public Result<PageResult<FeeRecord>> getFeeRecordList(@RequestParam Map<String, Object> params) {
        return Result.success(feeService.getFeeRecordList(params));
    }

    @Operation(summary = "确认收款")
    @PostMapping("/{id}/confirm-payment")
    public Result<Void> confirmPayment(@PathVariable String id, @RequestBody Map<String, Object> data) {
        feeService.confirmPayment(id, data);
        return Result. success();
    }

    @Operation(summary = "处理退款")
    @PostMapping("/{id}/refund")
    public Result<Void> processRefund(@PathVariable String id, @RequestBody Map<String, Object> data) {
        feeService.processRefund(id, data);
        return Result. success();
    }

    @Operation(summary = "获取费用统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestParam Map<String, Object> params) {
        return Result.success(feeService.getStatistics(params));
    }
}