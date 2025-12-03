package ztt.fixedassetmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ztt. fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.BuildingArchive;

import java.util. Map;

public interface BuildingService extends IService<BuildingArchive> {

    PageResult<BuildingArchive> getBuildingList(Map<String, Object> params);

    BuildingArchive getBuildingDetail(String id);

    void createBuilding(BuildingArchive building);

    void updateBuilding(String id, BuildingArchive building);

    void deleteBuilding(String id);
}