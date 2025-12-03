package ztt.fixedassetmanagement.service;

import java.util.Map;

public interface ReportService {

    Map<String, Object> getAssetStatistics(Map<String, Object> params);

    Map<String, Object> getFinanceDetail(Map<String, Object> params);

    byte[] exportAssetReport(Map<String, Object> params);

    byte[] exportFinanceReport(Map<String, Object> params);
}