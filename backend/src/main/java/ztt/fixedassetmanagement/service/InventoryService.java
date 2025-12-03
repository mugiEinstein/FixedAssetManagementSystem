package ztt.fixedassetmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ztt.fixedassetmanagement.common. PageResult;
import ztt.fixedassetmanagement. entity.AssetInventory;

import java.util. Map;

public interface InventoryService extends IService<AssetInventory> {

    PageResult<AssetInventory> getInventoryList(Map<String, Object> params);

    void createInventoryRecord(AssetInventory record);

    void updateInventoryRecord(String id, AssetInventory record);

    Map<String, Object> generateDifferenceReport(Map<String, Object> params);

    Map<String, Object> getValueCalculationList(Map<String, Object> params);

    void adjustAssetValue(Map<String, Object> data);
}