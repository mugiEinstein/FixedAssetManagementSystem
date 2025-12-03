package ztt.fixedassetmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ztt.fixedassetmanagement.entity.*;
import ztt. fixedassetmanagement.mapper.*;
import ztt. fixedassetmanagement.service.ReportService;

import java.math. BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final BuildingArchiveMapper buildingMapper;
    private final EquipmentArchiveMapper equipmentMapper;
    private final VehicleMapper vehicleMapper;
    private final FeeRecordMapper feeRecordMapper;

    @Override
    public Map<String, Object> getAssetStatistics(Map<String, Object> params) {
        Map<String, Object> statistics = new HashMap<>();

        // 建筑统计
        List<BuildingArchive> buildings = buildingMapper.selectList(new LambdaQueryWrapper<>());
        int buildingCount = buildings. size();
        BigDecimal buildingValue = buildings.stream()
                .map(b -> b.getOriginalValue() != null ? b.getOriginalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal. ZERO, BigDecimal::add);
        statistics.put("buildingCount", buildingCount);
        statistics.put("buildingValue", buildingValue);

        // 器材统计
        List<EquipmentArchive> equipments = equipmentMapper.selectList(new LambdaQueryWrapper<>());
        int equipmentCount = equipments.size();
        BigDecimal equipmentValue = equipments.stream()
                .map(e -> e.getOriginalValue() != null ? e.getOriginalValue() : BigDecimal. ZERO)
                . reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("equipmentCount", equipmentCount);
        statistics.put("equipmentValue", equipmentValue);

        // 车辆统计
        List<Vehicle> vehicles = vehicleMapper.selectList(new LambdaQueryWrapper<>());
        int vehicleCount = vehicles.size();
        BigDecimal vehicleValue = vehicles.stream()
                .map(v -> v.getOriginalValue() != null ? v.getOriginalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal. ZERO, BigDecimal::add);
        statistics.put("vehicleCount", vehicleCount);
        statistics.put("vehicleValue", vehicleValue);

        // 总计
        statistics.put("totalCount", buildingCount + equipmentCount + vehicleCount);
        statistics.put("totalValue", buildingValue. add(equipmentValue).add(vehicleValue));

        // 器材状态分布
        long inUseCount = equipments.stream(). filter(e -> "IN_USE".equals(e.getStatus())).count();
        long idleCount = equipments.stream().filter(e -> "IDLE".equals(e. getStatus())).count();
        long maintenanceCount = equipments.stream().filter(e -> "MAINTENANCE".equals(e.getStatus())). count();

        List<Map<String, Object>> statusDistribution = new ArrayList<>();
        statusDistribution.add(Map.of("name", "在用", "value", inUseCount));
        statusDistribution.add(Map.of("name", "闲置", "value", idleCount));
        statusDistribution.add(Map.of("name", "维修中", "value", maintenanceCount));
        statistics.put("statusDistribution", statusDistribution);

        // 类型分布
        List<Map<String, Object>> typeDistribution = new ArrayList<>();
        typeDistribution.add(Map. of("name", "建筑", "value", buildingCount));
        typeDistribution.add(Map. of("name", "器材", "value", equipmentCount));
        typeDistribution.add(Map. of("name", "车辆", "value", vehicleCount));
        statistics.put("typeDistribution", typeDistribution);

        return statistics;
    }

    @Override
    public Map<String, Object> getFinanceDetail(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        // 统计总收入
        LambdaQueryWrapper<FeeRecord> incomeWrapper = new LambdaQueryWrapper<>();
        incomeWrapper.eq(FeeRecord::getStatus, "paid");
        List<FeeRecord> paidRecords = feeRecordMapper. selectList(incomeWrapper);
        BigDecimal totalIncome = paidRecords.stream()
                .map(FeeRecord::getAmount)
                .reduce(BigDecimal. ZERO, BigDecimal::add);
        result.put("totalIncome", totalIncome);

        // 统计总退款
        LambdaQueryWrapper<FeeRecord> refundWrapper = new LambdaQueryWrapper<>();
        refundWrapper.in(FeeRecord::getStatus, "refunded", "partial_refund");
        List<FeeRecord> refundRecords = feeRecordMapper.selectList(refundWrapper);
        BigDecimal totalRefund = refundRecords.stream()
                .map(r -> r.getRefundAmount() != null ? r. getRefundAmount() : BigDecimal. ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result. put("totalRefund", totalRefund);

        // 净收入
        result.put("netIncome", totalIncome.subtract(totalRefund));

        // 待收款
        LambdaQueryWrapper<FeeRecord> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(FeeRecord::getStatus, "pending");
        List<FeeRecord> pendingRecords = feeRecordMapper.selectList(pendingWrapper);
        BigDecimal pendingAmount = pendingRecords.stream()
                .map(FeeRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result. put("pendingAmount", pendingAmount);

        // 按费用类型统计
        Map<String, BigDecimal> feeTypeStats = new HashMap<>();
        for (FeeRecord record : paidRecords) {
            String feeType = record. getFeeType();
            BigDecimal amount = record.getAmount();
            feeTypeStats.merge(feeType, amount, BigDecimal::add);
        }
        result. put("feeTypeStats", feeTypeStats);

        return result;
    }

    @Override
    public byte[] exportAssetReport(Map<String, Object> params) {
        // 简化处理，实际应使用EasyExcel生成Excel文件
        return "Asset Report Data".getBytes();
    }

    @Override
    public byte[] exportFinanceReport(Map<String, Object> params) {
        // 简化处理，实际应使用EasyExcel生成Excel文件
        return "Finance Report Data".getBytes();
    }
}