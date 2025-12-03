package ztt.fixedassetmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query. LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com. baomidou. mybatisplus. extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype. Service;
import org.springframework.util.StringUtils;
import ztt.fixedassetmanagement.common.BusinessException;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.EquipmentArchive;
import ztt.fixedassetmanagement. mapper.EquipmentArchiveMapper;
import ztt.fixedassetmanagement. service.EquipmentService;
import ztt.fixedassetmanagement.util.FeeCalculator;
import ztt.fixedassetmanagement.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java. time.temporal.ChronoUnit;
import java. util. HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl extends ServiceImpl<EquipmentArchiveMapper, EquipmentArchive>
        implements EquipmentService {

    @Override
    public PageResult<EquipmentArchive> getEquipmentList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params. get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<EquipmentArchive> wrapper = new LambdaQueryWrapper<>();

        // 条件查询
        if (StringUtils. hasText((String) params.get("name"))) {
            wrapper.like(EquipmentArchive::getEquipmentName, params.get("name"));
        }
        if (StringUtils.hasText((String) params.get("type"))) {
            wrapper.eq(EquipmentArchive::getEquipmentType, params.get("type"));
        }
        if (StringUtils. hasText((String) params.get("status"))) {
            wrapper.eq(EquipmentArchive::getStatus, params.get("status"));
        }

        wrapper.orderByDesc(EquipmentArchive::getCreatedAt);

        Page<EquipmentArchive> pageResult = page(new Page<>(page, pageSize), wrapper);

        return PageResult.of(pageResult. getRecords(), pageResult. getTotal(), page, pageSize);
    }

    @Override
    public EquipmentArchive getEquipmentDetail(String id) {
        EquipmentArchive equipment = getById(id);
        if (equipment == null) {
            throw new BusinessException("器材不存在");
        }
        return equipment;
    }

    @Override
    public Map<String, Object> getEquipmentNetValue(String id) {
        EquipmentArchive equipment = getById(id);
        if (equipment == null) {
            throw new BusinessException("器材不存在");
        }

        // 计算折旧
        BigDecimal originalValue = equipment.getOriginalValue();
        int depreciationYears = equipment.getDepreciationYears();
        LocalDate purchaseDate = equipment. getPurchaseDate();

        // 已使用年数
        long usedMonths = ChronoUnit. MONTHS.between(purchaseDate, LocalDate.now());
        double usedYears = usedMonths / 12.0;

        // 年折旧额 (残值率5%)
        BigDecimal annualDepreciation = FeeCalculator.calculateAnnualDepreciation(
                originalValue, new BigDecimal("0.05"), depreciationYears);

        // 累计折旧
        BigDecimal accumulatedDepreciation = annualDepreciation
                .multiply(BigDecimal. valueOf(Math.min(usedYears, depreciationYears)));

        // 净值
        BigDecimal netValue = originalValue.subtract(accumulatedDepreciation);
        if (netValue.compareTo(BigDecimal.ZERO) < 0) {
            netValue = originalValue.multiply(new BigDecimal("0.05")); // 残值
        }

        Map<String, Object> result = new HashMap<>();
        result.put("originalValue", originalValue);
        result.put("depreciation", accumulatedDepreciation);
        result. put("netValue", netValue);
        result.put("depreciationYears", depreciationYears);

        return result;
    }

    @Override
    public void createEquipment(EquipmentArchive equipment) {
        // 生成ID
        if (equipment.getEquipmentId() == null || equipment.getEquipmentId().isEmpty()) {
            equipment. setEquipmentId(IdGenerator.generateId("EQ"));
        }
        equipment.setStatus("IDLE");
        save(equipment);
    }

    @Override
    public void updateEquipment(String id, EquipmentArchive equipment) {
        EquipmentArchive existing = getById(id);
        if (existing == null) {
            throw new BusinessException("器材不存在");
        }
        equipment.setEquipmentId(id);
        updateById(equipment);
    }

    @Override
    public void deleteEquipment(String id) {
        EquipmentArchive equipment = getById(id);
        if (equipment == null) {
            throw new BusinessException("器材不存在");
        }
        removeById(id);
    }
}