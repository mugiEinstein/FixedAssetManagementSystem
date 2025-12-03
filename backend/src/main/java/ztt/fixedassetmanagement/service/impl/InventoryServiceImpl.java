package ztt.fixedassetmanagement. service.impl;

import com.baomidou.mybatisplus.core. conditions.query.LambdaQueryWrapper;
import com. baomidou. mybatisplus. extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok. RequiredArgsConstructor;
import org.springframework.stereotype. Service;
import org.springframework.util. StringUtils;
import ztt.fixedassetmanagement. common.BusinessException;
import ztt. fixedassetmanagement.common. PageResult;
import ztt.fixedassetmanagement. entity.AssetInventory;
import ztt.fixedassetmanagement.entity.BuildingArchive;
import ztt.fixedassetmanagement.entity. EquipmentArchive;
import ztt.fixedassetmanagement.mapper.AssetInventoryMapper;
import ztt.fixedassetmanagement.mapper.BuildingArchiveMapper;
import ztt.fixedassetmanagement.mapper.EquipmentArchiveMapper;
import ztt.fixedassetmanagement.service.InventoryService;
import ztt.fixedassetmanagement. util.FeeCalculator;
import ztt.fixedassetmanagement.util.IdGenerator;

import java.math. BigDecimal;
import java.math.RoundingMode;
import java.time. LocalDate;
import java.time.temporal.ChronoUnit;
import java. util.ArrayList;
import java.util. HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl extends ServiceImpl<AssetInventoryMapper, AssetInventory>
        implements InventoryService {

    private final EquipmentArchiveMapper equipmentMapper;
    private final BuildingArchiveMapper buildingMapper;

    @Override
    public PageResult<AssetInventory> getInventoryList(Map<String, Object> params) {
        int page = params. get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params. get("pageSize"). toString()) : 10;

        LambdaQueryWrapper<AssetInventory> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("assetType"))) {
            wrapper. eq(AssetInventory::getAssetType, params.get("assetType"));
        }
        if (StringUtils. hasText((String) params.get("inventoryStatus"))) {
            wrapper.eq(AssetInventory::getInventoryStatus, params.get("inventoryStatus"));
        }

        wrapper.orderByDesc(AssetInventory::getCreatedAt);

        Page<AssetInventory> pageResult = page(new Page<>(page, pageSize), wrapper);

        return PageResult.of(pageResult. getRecords(), pageResult. getTotal(), page, pageSize);
    }

    @Override
    public void createInventoryRecord(AssetInventory record) {
        record.setInventoryId(IdGenerator.generateId("IV"));
        record. setInventoryStatus("pending");
        record. setInventoryDate(LocalDate.now());
        save(record);
    }

    @Override
    public void updateInventoryRecord(String id, AssetInventory record) {
        AssetInventory existing = getById(id);
        if (existing == null) {
            throw new BusinessException("盘点记录不存在");
        }
        record.setInventoryId(id);
        updateById(record);
    }

    @Override
    public Map<String, Object> generateDifferenceReport(Map<String, Object> params) {
        Map<String, Object> report = new HashMap<>();

        // 统计盘点结果
        LambdaQueryWrapper<AssetInventory> wrapper = new LambdaQueryWrapper<>();
        if (params.get("planId") != null) {
            wrapper.eq(AssetInventory::getPlanId, params.get("planId"));
        }

        List<AssetInventory> inventories = list(wrapper);

        int totalAssets = inventories. size();
        int normalCount = 0;
        int differenceCount = 0;
        BigDecimal totalValueAdjustment = BigDecimal.ZERO;

        List<Map<String, Object>> differenceList = new ArrayList<>();

        for (AssetInventory inventory : inventories) {
            if ("正常".equals(inventory.getPhysicalStatus())) {
                normalCount++;
            } else {
                differenceCount++;
                Map<String, Object> diff = new HashMap<>();
                diff.put("assetId", inventory.getAssetId());
                diff.put("assetName", inventory.getAssetName());
                diff.put("assetType", inventory. getAssetType());
                diff.put("physicalStatus", inventory. getPhysicalStatus());
                diff. put("differenceReason", inventory.getDifferenceReason());
                diff.put("valueAdjustment", inventory.getValueAdjustment());
                differenceList.add(diff);

                if (inventory.getValueAdjustment() != null) {
                    totalValueAdjustment = totalValueAdjustment.add(inventory. getValueAdjustment());
                }
            }
        }

        report.put("totalAssets", totalAssets);
        report.put("normalCount", normalCount);
        report.put("differenceCount", differenceCount);
        report.put("totalValueAdjustment", totalValueAdjustment);
        report.put("differenceList", differenceList);

        return report;
    }

    @Override
    public Map<String, Object> getValueCalculationList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params. get("pageSize") != null ? Integer. parseInt(params.get("pageSize").toString()) : 10;

        List<Map<String, Object>> list = new ArrayList<>();
        BigDecimal totalOriginalValue = BigDecimal.ZERO;
        BigDecimal totalDepreciation = BigDecimal. ZERO;
        BigDecimal totalNetValue = BigDecimal.ZERO;

        String assetType = (String) params.get("assetType");

        // 查询器材
        if (assetType == null || "equipment".equals(assetType)) {
            List<EquipmentArchive> equipments = equipmentMapper. selectList(new LambdaQueryWrapper<>());
            for (EquipmentArchive eq : equipments) {
                Map<String, Object> item = calculateEquipmentValue(eq);
                list. add(item);

                totalOriginalValue = totalOriginalValue.add(eq.getOriginalValue() != null ? eq.getOriginalValue() : BigDecimal. ZERO);
                totalDepreciation = totalDepreciation.add((BigDecimal) item. get("accumulatedDepreciation"));
                totalNetValue = totalNetValue. add((BigDecimal) item.get("netValue"));
            }
        }

        // 查询建筑
        if (assetType == null || "building". equals(assetType)) {
            List<BuildingArchive> buildings = buildingMapper.selectList(new LambdaQueryWrapper<>());
            for (BuildingArchive bd : buildings) {
                Map<String, Object> item = calculateBuildingValue(bd);
                list.add(item);

                totalOriginalValue = totalOriginalValue.add(bd.getOriginalValue() != null ? bd.getOriginalValue() : BigDecimal.ZERO);
                totalDepreciation = totalDepreciation.add((BigDecimal) item.get("accumulatedDepreciation"));
                totalNetValue = totalNetValue. add((BigDecimal) item.get("netValue"));
            }
        }

        // 分页处理
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        List<Map<String, Object>> pageList = start < list.size() ?  list.subList(start, end) : new ArrayList<>();

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageList);
        result.put("total", list.size());
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalOriginalValue", totalOriginalValue);
        result.put("totalDepreciation", totalDepreciation);
        result.put("totalNetValue", totalNetValue);

        return result;
    }

    private Map<String, Object> calculateEquipmentValue(EquipmentArchive equipment) {
        Map<String, Object> item = new HashMap<>();
        item.put("assetCode", equipment.getEquipmentId());
        item. put("assetName", equipment.getEquipmentName());
        item.put("assetType", "equipment");
        item.put("originalValue", equipment.getOriginalValue());
        item.put("depreciationYears", equipment.getDepreciationYears());
        item. put("residualRate", new BigDecimal("0.05"));

        // 计算折旧
        BigDecimal originalValue = equipment.getOriginalValue() != null ? equipment. getOriginalValue() : BigDecimal. ZERO;
        int depreciationYears = equipment.getDepreciationYears() != null ? equipment.getDepreciationYears() : 10;
        LocalDate purchaseDate = equipment. getPurchaseDate() != null ? equipment. getPurchaseDate() : LocalDate.now();

        // 年折旧额
        BigDecimal annualDepreciation = FeeCalculator.calculateAnnualDepreciation(
                originalValue, new BigDecimal("0.05"), depreciationYears);
        item.put("annualDepreciation", annualDepreciation);

        // 已使用年数
        long usedMonths = ChronoUnit. MONTHS.between(purchaseDate, LocalDate.now());
        double usedYears = usedMonths / 12.0;

        // 累计折旧
        BigDecimal accumulatedDepreciation = annualDepreciation
                .multiply(BigDecimal.valueOf(Math.min(usedYears, depreciationYears)))
                . setScale(2, RoundingMode.HALF_UP);

        // 残值
        BigDecimal residualValue = originalValue.multiply(new BigDecimal("0.05"));

        // 累计折旧不能超过可折旧额
        BigDecimal maxDepreciation = originalValue.subtract(residualValue);
        if (accumulatedDepreciation.compareTo(maxDepreciation) > 0) {
            accumulatedDepreciation = maxDepreciation;
        }
        item.put("accumulatedDepreciation", accumulatedDepreciation);

        // 净值
        BigDecimal netValue = originalValue.subtract(accumulatedDepreciation);
        item.put("netValue", netValue);

        item.put("calculationDate", LocalDate.now(). toString());

        return item;
    }

    private Map<String, Object> calculateBuildingValue(BuildingArchive building) {
        Map<String, Object> item = new HashMap<>();
        item.put("assetCode", building.getBuildingId());
        item.put("assetName", building.getBuildingName());
        item. put("assetType", "building");
        item.put("originalValue", building. getOriginalValue());
        item.put("depreciationYears", building.getDepreciationYears());
        item. put("residualRate", new BigDecimal("0.05"));

        BigDecimal originalValue = building.getOriginalValue() != null ?  building.getOriginalValue() : BigDecimal.ZERO;
        int depreciationYears = building.getDepreciationYears() != null ? building.getDepreciationYears() : 50;
        int buildYear = building.getBuildYear() != null ? building. getBuildYear() : LocalDate.now(). getYear();

        // 年折旧额
        BigDecimal annualDepreciation = FeeCalculator.calculateAnnualDepreciation(
                originalValue, new BigDecimal("0.05"), depreciationYears);
        item.put("annualDepreciation", annualDepreciation);

        // 已使用年数
        int usedYears = LocalDate.now().getYear() - buildYear;

        // 累计折旧
        BigDecimal accumulatedDepreciation = annualDepreciation
                .multiply(BigDecimal.valueOf(Math.min(usedYears, depreciationYears)))
                .setScale(2, RoundingMode. HALF_UP);

        // 残值
        BigDecimal residualValue = originalValue.multiply(new BigDecimal("0.05"));

        // 累计折旧不能超过可折旧额
        BigDecimal maxDepreciation = originalValue.subtract(residualValue);
        if (accumulatedDepreciation. compareTo(maxDepreciation) > 0) {
            accumulatedDepreciation = maxDepreciation;
        }
        item.put("accumulatedDepreciation", accumulatedDepreciation);

        // 净值
        BigDecimal netValue = originalValue.subtract(accumulatedDepreciation);
        item.put("netValue", netValue);

        item.put("calculationDate", LocalDate. now().toString());

        return item;
    }

    @Override
    public void adjustAssetValue(Map<String, Object> data) {
        String assetType = (String) data. get("assetType");
        String assetId = (String) data.get("assetId");
        String adjustmentType = (String) data.get("adjustmentType");
        BigDecimal adjustmentAmount = new BigDecimal(data.get("adjustmentAmount").toString());

        if ("equipment".equals(assetType)) {
            EquipmentArchive equipment = equipmentMapper.selectById(assetId);
            if (equipment == null) {
                throw new BusinessException("器材不存在");
            }

            BigDecimal originalValue = equipment.getOriginalValue() != null ? equipment. getOriginalValue() : BigDecimal. ZERO;
            BigDecimal newValue;

            switch (adjustmentType) {
                case "addition":
                    newValue = originalValue. add(adjustmentAmount);
                    break;
                case "reduction":
                    newValue = originalValue. subtract(adjustmentAmount);
                    if (newValue.compareTo(BigDecimal.ZERO) < 0) {
                        newValue = BigDecimal. ZERO;
                    }
                    break;
                case "revaluation":
                    newValue = adjustmentAmount;
                    break;
                default:
                    throw new BusinessException("无效的调整类型");
            }

            equipment.setOriginalValue(newValue);
            equipmentMapper.updateById(equipment);

        } else if ("building".equals(assetType)) {
            BuildingArchive building = buildingMapper. selectById(assetId);
            if (building == null) {
                throw new BusinessException("建筑不存在");
            }

            BigDecimal originalValue = building. getOriginalValue() != null ? building. getOriginalValue() : BigDecimal. ZERO;
            BigDecimal newValue;

            switch (adjustmentType) {
                case "addition":
                    newValue = originalValue.add(adjustmentAmount);
                    break;
                case "reduction":
                    newValue = originalValue.subtract(adjustmentAmount);
                    if (newValue.compareTo(BigDecimal. ZERO) < 0) {
                        newValue = BigDecimal.ZERO;
                    }
                    break;
                case "revaluation":
                    newValue = adjustmentAmount;
                    break;
                default:
                    throw new BusinessException("无效的调整类型");
            }

            building.setOriginalValue(newValue);
            buildingMapper. updateById(building);
        }
    }
}