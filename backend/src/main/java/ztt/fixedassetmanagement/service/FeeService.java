package ztt.fixedassetmanagement. service;

import com.baomidou.mybatisplus.extension.service. IService;
import ztt.fixedassetmanagement. common.PageResult;
import ztt. fixedassetmanagement.entity.FeeRecord;

import java.util.Map;

public interface FeeService extends IService<FeeRecord> {

    PageResult<FeeRecord> getFeeRecordList(Map<String, Object> params);

    void createFeeRecord(FeeRecord record);

    void confirmPayment(String id, Map<String, Object> data);

    void processRefund(String id, Map<String, Object> data);

    Map<String, Object> getStatistics(Map<String, Object> params);
}