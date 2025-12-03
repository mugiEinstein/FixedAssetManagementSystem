package ztt.fixedassetmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity. MaintenanceRecord;

import java.util.List;
import java.util. Map;

public interface MaintenanceService extends IService<MaintenanceRecord> {

    PageResult<MaintenanceRecord> getMaintenanceList(Map<String, Object> params);

    void submitMaintenanceRequest(MaintenanceRecord record);

    void assignMaintainer(String id, Map<String, Object> data);

    void startMaintenance(String id);

    void completeMaintenance(String id, Map<String, Object> data);

    List<Map<String, Object>> getMaintainerList();
}