package ztt.fixedassetmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query. LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins. pagination.Page;
import com.baomidou.mybatisplus.extension. service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ztt.fixedassetmanagement.common.BusinessException;
import ztt.fixedassetmanagement.common. PageResult;
import ztt.fixedassetmanagement. entity.MaintenanceRecord;
import ztt.fixedassetmanagement.mapper.MaintenanceRecordMapper;
import ztt.fixedassetmanagement. service.MaintenanceService;
import ztt.fixedassetmanagement.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java. time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl extends ServiceImpl<MaintenanceRecordMapper, MaintenanceRecord>
        implements MaintenanceService {

    @Override
    public PageResult<MaintenanceRecord> getMaintenanceList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params. get("pageSize") != null ? Integer. parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<MaintenanceRecord> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("workOrderNo"))) {
            wrapper.like(MaintenanceRecord::getMaintenanceId, params.get("workOrderNo"));
        }
        if (StringUtils.hasText((String) params.get("assetType"))) {
            wrapper.eq(MaintenanceRecord::getAssetType, params.get("assetType"));
        }
        if (StringUtils.hasText((String) params.get("status"))) {
            wrapper.eq(MaintenanceRecord::getStatus, params.get("status"));
        }
        // 按申请人过滤（用于普通用户只查看自己的记录）
        if (StringUtils.hasText((String) params.get("reporterName"))) {
            wrapper.eq(MaintenanceRecord::getReporterName, params.get("reporterName"));
        }

        wrapper.orderByDesc(MaintenanceRecord::getCreatedAt);

        Page<MaintenanceRecord> pageResult = page(new Page<>(page, pageSize), wrapper);

        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), page, pageSize);
    }

    @Override
    public void submitMaintenanceRequest(MaintenanceRecord record) {
        record.setMaintenanceId(IdGenerator.generateMaintenanceId());
        record.setStatus("pending");
        record.setReportTime(LocalDateTime.now());
        
        // 前端字段映射: reporterDept -> reporterDepartment
        if (!StringUtils.hasText(record.getReporterDepartment()) && StringUtils.hasText(record.getReporterDept())) {
            record.setReporterDepartment(record.getReporterDept());
        }
        
        // 数据库中 maintenance_person 和 maintenance_date 是 NOT NULL
        // 新提交的申请还没有指派维修人员，设置默认值
        if (!StringUtils.hasText(record.getMaintenancePerson())) {
            record.setMaintenancePerson("待分配");
        }
        if (record.getMaintenanceDate() == null) {
            record.setMaintenanceDate(LocalDate.now());
        }
        
        // 设置默认维修类型
        if (!StringUtils.hasText(record.getMaintenanceType())) {
            record.setMaintenanceType("维修");
        }
        
        save(record);
    }

    @Override
    public void assignMaintainer(String id, Map<String, Object> data) {
        MaintenanceRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("维修记录不存在");
        }

        record.setMaintenancePerson((String) data.get("maintainerName"));
        record. setStatus("assigned");
        updateById(record);
    }

    @Override
    public void startMaintenance(String id) {
        MaintenanceRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("维修记录不存在");
        }

        record.setStatus("in_progress");
        record.setMaintenanceDate(LocalDate.now());
        updateById(record);
    }

    @Override
    public void completeMaintenance(String id, Map<String, Object> data) {
        MaintenanceRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("维修记录不存在");
        }

        record.setFaultReason((String) data.get("faultReason"));
        record. setMaintenanceDescription((String) data. get("maintenanceDescription"));

        if (data. get("partsReplaced") != null) {
            record.setPartsReplaced(data.get("partsReplaced").toString());
        }
        if (data.get("maintenanceCost") != null) {
            record. setCost(new BigDecimal(data.get("maintenanceCost").toString()));
        }

        record.setStatus("completed");
        record. setCompletionTime(LocalDateTime. now());
        updateById(record);
    }

    @Override
    public List<Map<String, Object>> getMaintainerList() {
        // 模拟维护人员列表（实际应从用户表查询）
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("id", 1, "name", "张师傅", "speciality", Arrays.asList("电器", "空调"), "currentTasks", 2));
        list. add(Map.of("id", 2, "name", "李师傅", "speciality", Arrays.asList("管道", "水电"), "currentTasks", 1));
        list.add(Map.of("id", 3, "name", "王师傅", "speciality", Arrays.asList("电脑", "网络"), "currentTasks", 3));
        return list;
    }
}