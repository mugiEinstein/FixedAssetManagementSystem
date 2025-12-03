package ztt.fixedassetmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query. LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins. pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util. StringUtils;
import ztt.fixedassetmanagement. common.BusinessException;
import ztt. fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.BuildingArchive;
import ztt.fixedassetmanagement.entity.ExternalEventBooking;
import ztt.fixedassetmanagement.entity.InternalEventBooking;
import ztt.fixedassetmanagement.mapper.BuildingArchiveMapper;
import ztt. fixedassetmanagement.mapper.ExternalEventBookingMapper;
import ztt.fixedassetmanagement.mapper. InternalEventBookingMapper;
import ztt.fixedassetmanagement.service.ActivityVenueService;
import ztt.fixedassetmanagement.util.FeeCalculator;
import ztt.fixedassetmanagement.util. IdGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java. util.HashMap;
import java. util.List;
import java.util. Map;

@Service
@RequiredArgsConstructor
public class ActivityVenueServiceImpl implements ActivityVenueService {

    private final ExternalEventBookingMapper externalMapper;
    private final InternalEventBookingMapper internalMapper;
    private final BuildingArchiveMapper buildingMapper;

    // ==================== 校外活动 ====================

    @Override
    public PageResult<ExternalEventBooking> getExternalEventList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params. get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<ExternalEventBooking> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("applicationNo"))) {
            wrapper.like(ExternalEventBooking::getEventId, params.get("applicationNo"));
        }
        if (StringUtils.hasText((String) params.get("organizationName"))) {
            wrapper.like(ExternalEventBooking::getOrganizer, params.get("organizationName"));
        }
        if (StringUtils.hasText((String) params.get("status"))) {
            wrapper.eq(ExternalEventBooking::getStatus, params.get("status"));
        }

        wrapper.orderByDesc(ExternalEventBooking::getCreatedAt);

        Page<ExternalEventBooking> pageResult = externalMapper.selectPage(new Page<>(page, pageSize), wrapper);

        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), page, pageSize);
    }

    @Override
    @Transactional
    public void submitExternalEvent(ExternalEventBooking event) {
        event.setEventId(IdGenerator.generateEventId());
        event.setStatus("pending");
        event.setQualificationApproval("pending");
        event.setVenueApproval("pending");
        event.setDepositPaid(0);

        // 计算费用
        BigDecimal venueRent = event.getVenueRent() != null ? event.getVenueRent() : BigDecimal.ZERO;
        BigDecimal equipmentFee = event.getEquipmentFee() != null ? event.getEquipmentFee() : BigDecimal.ZERO;

        // 计算安保费：每100人200元
        int attendees = event.getExpectedAttendees() != null ? event.getExpectedAttendees() : 0;
        BigDecimal securityFee = new BigDecimal((int) Math.ceil(attendees / 100.0) * 200);
        event.setSecurityFee(securityFee);

        // 总费用
        BigDecimal totalFee = venueRent.add(equipmentFee).add(securityFee);
        event.setTotalFee(totalFee);

        externalMapper.insert(event);
    }

    @Override
    public void reviewExternalQualification(String id, Map<String, Object> data) {
        ExternalEventBooking event = externalMapper.selectById(id);
        if (event == null) {
            throw new BusinessException("活动记录不存在");
        }

        boolean approved = (Boolean) data.get("approved");
        event.setQualificationApproval(approved ? "approved" : "rejected");

        if (!approved) {
            event.setStatus("rejected");
        }

        externalMapper.updateById(event);
    }

    @Override
    public void reviewExternalVenue(String id, Map<String, Object> data) {
        ExternalEventBooking event = externalMapper.selectById(id);
        if (event == null) {
            throw new BusinessException("活动记录不存在");
        }

        boolean approved = (Boolean) data.get("approved");
        event.setVenueApproval(approved ? "approved" : "rejected");

        if (!approved) {
            event.setStatus("rejected");
        } else if ("approved".equals(event.getQualificationApproval())) {
            event.setStatus("approved");
        }

        externalMapper.updateById(event);
    }

    @Override
    public void confirmExternalPayment(String id) {
        ExternalEventBooking event = externalMapper.selectById(id);
        if (event == null) {
            throw new BusinessException("活动记录不存在");
        }

        event.setDepositPaid(1);
        externalMapper.updateById(event);
    }

    @Override
    public void signExternalContract(String id) {
        ExternalEventBooking event = externalMapper.selectById(id);
        if (event == null) {
            throw new BusinessException("活动记录不存在");
        }

        event.setStatus("contracted");
        externalMapper.updateById(event);
    }

    // ==================== 校内活动 ====================

    @Override
    public PageResult<InternalEventBooking> getInternalEventList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<InternalEventBooking> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("applicationNo"))) {
            wrapper.like(InternalEventBooking::getEventId, params.get("applicationNo"));
        }
        // activityName字段不在数据库中，使用organizerDept代替
        if (StringUtils.hasText((String) params.get("activityName"))) {
            wrapper.like(InternalEventBooking::getOrganizerDept, params.get("activityName"));
        }
        if (StringUtils.hasText((String) params.get("status"))) {
            wrapper.eq(InternalEventBooking::getStatus, params.get("status"));
        }

        wrapper.orderByDesc(InternalEventBooking::getCreatedAt);

        Page<InternalEventBooking> pageResult = internalMapper.selectPage(new Page<>(page, pageSize), wrapper);

        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), page, pageSize);
    }

    @Override
    @Transactional
    public void submitInternalEvent(InternalEventBooking event) {
        event.setEventId(IdGenerator.generateEventId());
        event.setStatus("pending");
        event. setDepartmentApproval("pending");

        // 校内活动费用 = 校外价 × 60%
        BigDecimal originalRent = event.getVenueRent() != null ? event. getVenueRent() : BigDecimal.ZERO;
        BigDecimal duration = event.getDuration() != null ? event.getDuration() : BigDecimal.ONE;
        BigDecimal discountFee = FeeCalculator.calculateInternalActivityFee(originalRent, duration);
        event.setDiscountFee(discountFee);

        internalMapper.insert(event);
    }

    @Override
    public void reviewInternalEvent(String id, Map<String, Object> data) {
        InternalEventBooking event = internalMapper. selectById(id);
        if (event == null) {
            throw new BusinessException("活动记录不存在");
        }

        boolean approved = (Boolean) data.get("approved");
        event. setDepartmentApproval(approved ?  "approved" : "rejected");
        event.setStatus(approved ? "approved" : "rejected");

        internalMapper.updateById(event);
    }

    @Override
    public void confirmInternalVenue(String id) {
        InternalEventBooking event = internalMapper.selectById(id);
        if (event == null) {
            throw new BusinessException("活动记录不存在");
        }

        event.setStatus("confirmed");
        internalMapper.updateById(event);
    }

    // ==================== 场地查询 ====================

    @Override
    public List<Map<String, Object>> getIdleVenues(Map<String, Object> params) {
        // 查询可用场地（简化处理，实际应根据日期和时间过滤已预订的场地）
        LambdaQueryWrapper<BuildingArchive> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuildingArchive::getStatus, "正常");

        if (StringUtils.hasText((String) params.get("type"))) {
            wrapper.eq(BuildingArchive::getBuildingType, params.get("type"));
        }

        List<BuildingArchive> buildings = buildingMapper. selectList(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (BuildingArchive building : buildings) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", building.getBuildingId());
            item.put("name", building.getBuildingName());
            item.put("type", building.getBuildingType());
            item.put("capacity", 500); // 模拟容量
            item. put("hourlyRate", new BigDecimal("1000")); // 模拟每小时租金
            item. put("equipmentList", List.of("音响", "投影", "空调"));
            result.add(item);
        }

        return result;
    }
}