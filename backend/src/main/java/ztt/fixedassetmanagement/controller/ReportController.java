package ztt.fixedassetmanagement.controller;

import io.swagger.v3.oas. annotations.Operation;
import io.swagger. v3.oas.annotations.tags. Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org. springframework.http.MediaType;
import org.springframework.http. ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ztt. fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement.service.ReportService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Tag(name = "报表管理")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "获取资产统计数据")
    @GetMapping("/asset-statistics")
    public Result<Map<String, Object>> getAssetStatistics(@RequestParam Map<String, Object> params) {
        return Result.success(reportService.getAssetStatistics(params));
    }

    @Operation(summary = "获取财务明细")
    @GetMapping("/finance-detail")
    public Result<Map<String, Object>> getFinanceDetail(@RequestParam Map<String, Object> params) {
        return Result.success(reportService.getFinanceDetail(params));
    }

    @Operation(summary = "导出资产报表")
    @GetMapping("/export-asset")
    public ResponseEntity<byte[]> exportAssetReport(@RequestParam Map<String, Object> params) {
        byte[] data = reportService. exportAssetReport(params);

        String filename = "资产统计报表. xlsx";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets. UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers. setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers. setContentDispositionFormData("attachment", encodedFilename);

        return ResponseEntity.ok(). headers(headers).body(data);
    }

    @Operation(summary = "导出财务报表")
    @GetMapping("/export-finance")
    public ResponseEntity<byte[]> exportFinanceReport(@RequestParam Map<String, Object> params) {
        byte[] data = reportService.exportFinanceReport(params);

        String filename = "财务明细报表.xlsx";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets. UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers. setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers. setContentDispositionFormData("attachment", encodedFilename);

        return ResponseEntity.ok(). headers(headers).body(data);
    }
}