package ztt. fixedassetmanagement.service.impl;

import com. baomidou. mybatisplus. core.conditions.query. LambdaQueryWrapper;
import com. baomidou. mybatisplus. extension.plugins.pagination.Page;
import com. baomidou. mybatisplus. extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ztt.fixedassetmanagement.common.BusinessException;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.BuildingArchive;
import ztt.fixedassetmanagement.mapper.BuildingArchiveMapper;
import ztt.fixedassetmanagement.service.BuildingService;
import ztt.fixedassetmanagement.util.IdGenerator;

import java. util.Map;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl extends ServiceImpl<BuildingArchiveMapper, BuildingArchive>
        implements BuildingService {

    @Override
    public PageResult<BuildingArchive> getBuildingList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params. get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<BuildingArchive> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("name"))) {
            wrapper.like(BuildingArchive::getBuildingName, params.get("name"));
        }
        if (StringUtils.hasText((String) params.get("type"))) {
            wrapper.eq(BuildingArchive::getBuildingType, params.get("type"));
        }
        if (StringUtils.hasText((String) params.get("status"))) {
            wrapper.eq(BuildingArchive::getStatus, params.get("status"));
        }

        wrapper.orderByDesc(BuildingArchive::getCreatedAt);

        Page<BuildingArchive> pageResult = page(new Page<>(page, pageSize), wrapper);

        return PageResult.of(pageResult. getRecords(), pageResult. getTotal(), page, pageSize);
    }

    @Override
    public BuildingArchive getBuildingDetail(String id) {
        BuildingArchive building = getById(id);
        if (building == null) {
            throw new BusinessException("建筑不存在");
        }
        return building;
    }

    @Override
    public void createBuilding(BuildingArchive building) {
        if (building.getBuildingId() == null || building.getBuildingId().isEmpty()) {
            building.setBuildingId(IdGenerator.generateId("BD"));
        }
        if (building.getStatus() == null) {
            building.setStatus("正常");
        }
        save(building);
    }

    @Override
    public void updateBuilding(String id, BuildingArchive building) {
        BuildingArchive existing = getById(id);
        if (existing == null) {
            throw new BusinessException("建筑不存在");
        }
        building.setBuildingId(id);
        updateById(building);
    }

    @Override
    public void deleteBuilding(String id) {
        BuildingArchive building = getById(id);
        if (building == null) {
            throw new BusinessException("建筑不存在");
        }
        removeById(id);
    }
}