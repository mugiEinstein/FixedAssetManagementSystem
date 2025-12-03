package ztt.fixedassetmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ztt.fixedassetmanagement.common.BusinessException;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.EquipmentArchive;
import ztt.fixedassetmanagement.entity.InternalEquipmentBorrow;
import ztt.fixedassetmanagement.entity.OutboundEquipmentBorrow;
import ztt.fixedassetmanagement.mapper.EquipmentArchiveMapper;
import ztt.fixedassetmanagement.mapper.InternalEquipmentBorrowMapper;
import ztt.fixedassetmanagement.mapper.OutboundEquipmentBorrowMapper;
import ztt.fixedassetmanagement.service.EquipmentBorrowService;
import ztt.fixedassetmanagement.util.FeeCalculator;
import ztt.fixedassetmanagement.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EquipmentBorrowServiceImpl implements EquipmentBorrowService {

    private final OutboundEquipmentBorrowMapper outboundMapper;
    private final InternalEquipmentBorrowMapper internalMapper;
    private final EquipmentArchiveMapper equipmentMapper;

    // ==================== 校外借用 ====================

    @Override
    public PageResult<OutboundEquipmentBorrow> getExternalBorrowList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<OutboundEquipmentBorrow> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("applicationNo"))) {
            wrapper.like(OutboundEquipmentBorrow::getBorrowId, params.get("applicationNo"));
        }
        if (StringUtils.hasText((String) params.get("organizationName"))) {
            wrapper.like(OutboundEquipmentBorrow::getApplicantUnit, params.get("organizationName"));
        }
        if (StringUtils.hasText((String) params.get("status"))) {
            wrapper.eq(OutboundEquipmentBorrow::getStatus, params.get("status"));
        }

        wrapper.orderByDesc(OutboundEquipmentBorrow::getCreatedAt);

        Page<OutboundEquipmentBorrow> pageResult = outboundMapper.selectPage(new Page<>(page, pageSize), wrapper);

        // 关联器材信息
        pageResult.getRecords().forEach(borrow -> {
            EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
            if (equipment != null) {
                borrow.setEquipmentName(equipment.getEquipmentName());
                borrow.setDailyRent(equipment.getDailyRent());
            }
        });

        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), page, pageSize);
    }

    @Override
    public OutboundEquipmentBorrow getExternalBorrowDetail(String id) {
        OutboundEquipmentBorrow borrow = outboundMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }
        // 关联器材信息
        EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
        if (equipment != null) {
            borrow.setEquipmentName(equipment.getEquipmentName());
            borrow.setDailyRent(equipment.getDailyRent());
        }
        return borrow;
    }

    @Override
    @Transactional
    public void submitExternalBorrow(OutboundEquipmentBorrow borrow) {
        // 生成借用ID
        borrow.setBorrowId(IdGenerator.generateBorrowId());
        borrow.setStatus("pending");
        borrow.setQualificationApproval("pending");
        borrow.setEquipmentApproval("pending");
        borrow.setDepositPaid(0);

        // 获取器材信息计算费用
        EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
        if (equipment == null) {
            throw new BusinessException("器材不存在");
        }

        // 计算押金：Max(原值×10%, 1000)
        BigDecimal deposit = FeeCalculator.calculateEquipmentDeposit(equipment.getOriginalValue());
        borrow.setDepositAmount(deposit);

        // 计算租金：每日租金 × 天数
        BigDecimal dailyRent = equipment.getDailyRent() != null ? equipment.getDailyRent() : BigDecimal.ZERO;
        BigDecimal rent = FeeCalculator.calculateEquipmentRent(dailyRent, borrow.getBorrowDays());
        borrow.setRentAmount(rent);

        outboundMapper.insert(borrow);
    }

    @Override
    @Transactional
    public void reviewExternalQualification(String id, Map<String, Object> data) {
        OutboundEquipmentBorrow borrow = outboundMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }

        boolean approved = (Boolean) data.get("approved");
        borrow.setQualificationApproval(approved ? "approved" : "rejected");

        if (!approved) {
            borrow.setStatus("rejected");
        }

        outboundMapper.updateById(borrow);
    }

    @Override
    @Transactional
    public void reviewExternalEquipment(String id, Map<String, Object> data) {
        OutboundEquipmentBorrow borrow = outboundMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }

        boolean approved = (Boolean) data.get("approved");
        borrow.setEquipmentApproval(approved ? "approved" : "rejected");

        if (!approved) {
            borrow.setStatus("rejected");
        } else if ("approved".equals(borrow.getQualificationApproval())) {
            // 两个审批都通过，状态变为 approved，同时更新器材状态
            borrow.setStatus("approved");
            borrow.setStartDate(LocalDate.now());
            borrow.setEndDate(LocalDate.now().plusDays(borrow.getBorrowDays()));
            
            // 更新器材状态为使用中
            EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
            if (equipment != null) {
                equipment.setStatus("IN_USE");
                equipmentMapper.updateById(equipment);
            }
        }

        outboundMapper.updateById(borrow);
    }

    @Override
    @Transactional
    public void confirmExternalPayment(String id) {
        OutboundEquipmentBorrow borrow = outboundMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }

        borrow.setDepositPaid(1);
        outboundMapper.updateById(borrow);
    }

    @Override
    @Transactional
    public void confirmExternalPickup(String id) {
        OutboundEquipmentBorrow borrow = outboundMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }

        borrow.setStatus("in_use");
        borrow.setStartDate(LocalDate.now());

        // 计算预计归还日期
        borrow.setEndDate(LocalDate.now().plusDays(borrow.getBorrowDays()));

        outboundMapper.updateById(borrow);

        // 更新器材状态
        EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
        equipment.setStatus("IN_USE");
        equipmentMapper.updateById(equipment);
    }

    @Override
    @Transactional
    public void returnExternalEquipment(String id, Map<String, Object> data) {
        OutboundEquipmentBorrow borrow = outboundMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }

        borrow.setActualReturnDate(LocalDate.now());

        // 计算是否逾期及违约金
        if (borrow.getEndDate() != null && LocalDate.now().isAfter(borrow.getEndDate())) {
            long overdueDays = ChronoUnit.DAYS.between(borrow.getEndDate(), LocalDate.now());
            EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
            BigDecimal dailyRent = equipment.getDailyRent() != null ?  equipment.getDailyRent() : BigDecimal.ZERO;
            // 校外违约金：1. 5 × 每日租金 × 违约天数
            BigDecimal penalty = FeeCalculator.calculateExternalPenalty(dailyRent, (int) overdueDays);
            borrow.setPenaltyFee(penalty);
        }

        // 检查是否需要维修
        Boolean isNormal = (Boolean) data.get("isNormal");
        if (isNormal != null && !isNormal) {
            borrow.setStatus("need_repair");
        } else {
            borrow.setStatus("returned");
        }

        outboundMapper.updateById(borrow);

        // 更新器材状态
        EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
        equipment.setStatus(Boolean.TRUE.equals(isNormal) ? "IDLE" : "MAINTENANCE");
        equipmentMapper.updateById(equipment);
    }

    @Override
    @Transactional
    public void applyExternalRenewal(String id, Map<String, Object> data) {
        OutboundEquipmentBorrow borrow = outboundMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }
        int additionalDays = (Integer) data.get("additionalDays");
        borrow.setBorrowDays(borrow.getBorrowDays() + additionalDays);
        borrow.setEndDate(borrow.getEndDate().plusDays(additionalDays));
        EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
        BigDecimal dailyRent = equipment.getDailyRent() != null ? equipment.getDailyRent() : BigDecimal.ZERO;
        BigDecimal additionalRent = FeeCalculator.calculateEquipmentRent(dailyRent, additionalDays);
        borrow.setRentAmount(borrow.getRentAmount().add(additionalRent));
        outboundMapper.updateById(borrow);
    }

    // ==================== 校内借用 ====================

    @Override
    public PageResult<InternalEquipmentBorrow> getInternalBorrowList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        LambdaQueryWrapper<InternalEquipmentBorrow> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText((String) params.get("applicantName"))) {
            wrapper.like(InternalEquipmentBorrow::getApplicantName, params.get("applicantName"));
        }
        if (StringUtils.hasText((String) params.get("status"))) {
            wrapper.eq(InternalEquipmentBorrow::getStatus, params.get("status"));
        }
        if (params.get("applicantId") != null) {
            wrapper.eq(InternalEquipmentBorrow::getUserId, params.get("applicantId"));
        }
        wrapper.orderByDesc(InternalEquipmentBorrow::getCreatedAt);
        Page<InternalEquipmentBorrow> pageResult = internalMapper.selectPage(new Page<>(page, pageSize), wrapper);
        pageResult.getRecords().forEach(borrow -> {
            EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
            if (equipment != null) {
                borrow.setEquipmentName(equipment.getEquipmentName());
            }
        });
        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), page, pageSize);
    }

    @Override
    public InternalEquipmentBorrow getInternalBorrowDetail(String id) {
        InternalEquipmentBorrow borrow = internalMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }
        EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
        if (equipment != null) {
            borrow.setEquipmentName(equipment.getEquipmentName());
        }
        return borrow;
    }

    @Override
    @Transactional
    public void submitInternalBorrow(InternalEquipmentBorrow borrow) {
        borrow.setBorrowId(IdGenerator.generateBorrowId());
        borrow.setStatus("pending");
        borrow.setPermissionApproval("pending");
        borrow.setEquipmentApproval("pending");
        internalMapper.insert(borrow);
    }

    @Override
    @Transactional
    public void reviewInternalPermission(String id, Map<String, Object> data) {
        InternalEquipmentBorrow borrow = internalMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }
        boolean approved = (Boolean) data.get("approved");
        borrow.setPermissionApproval(approved ? "approved" : "rejected");
        if (!approved) {
            borrow.setStatus("rejected");
        }
        internalMapper.updateById(borrow);
    }

    @Override
    @Transactional
    public void reviewInternalEquipment(String id, Map<String, Object> data) {
        InternalEquipmentBorrow borrow = internalMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }
        boolean approved = (Boolean) data.get("approved");
        borrow.setEquipmentApproval(approved ? "approved" : "rejected");

        if (!approved) {
            borrow.setStatus("rejected");
        } else if ("approved".equals(borrow.getPermissionApproval())) {
            // 两个审批都通过，状态变为 approved，同时更新器材状态
            borrow.setStatus("approved");
            borrow.setBorrowDate(LocalDate.now());
            
            // 更新器材状态为使用中
            EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
            if (equipment != null) {
                equipment.setStatus("IN_USE");
                equipmentMapper.updateById(equipment);
            }
        }

        internalMapper.updateById(borrow);
    }

    @Override
    @Transactional
    public void confirmInternalPickup(String id) {
        InternalEquipmentBorrow borrow = internalMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }

        borrow.setStatus("in_use");
        borrow.setBorrowDate(LocalDate.now());

        internalMapper.updateById(borrow);

        // 更新器材状态
        EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
        equipment.setStatus("IN_USE");
        equipmentMapper.updateById(equipment);
    }

    @Override
    @Transactional
    public void returnInternalEquipment(String id, Map<String, Object> data) {
        InternalEquipmentBorrow borrow = internalMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }

        borrow.setActualReturnDate(LocalDate.now());

        // 计算是否逾期及违约金
        if (borrow.getReturnDate() != null && LocalDate.now().isAfter(borrow.getReturnDate())) {
            long overdueDays = ChronoUnit.DAYS.between(borrow.getReturnDate(), LocalDate.now());
            EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
            BigDecimal dailyRent = equipment.getDailyRent() != null ? equipment.getDailyRent() : BigDecimal.ZERO;
            // 校内违约金：1.2 × 每日租金 × 违约天数
            BigDecimal penalty = FeeCalculator.calculateInternalPenalty(dailyRent, (int) overdueDays);
            borrow.setPenaltyFee(penalty);
        }

        Boolean isNormal = (Boolean) data.get("isNormal");
        borrow.setStatus("returned");

        internalMapper.updateById(borrow);

        // 更新器材状态
        EquipmentArchive equipment = equipmentMapper.selectById(borrow.getEquipmentId());
        equipment.setStatus(Boolean.TRUE.equals(isNormal) ? "IDLE" : "MAINTENANCE");
        equipmentMapper.updateById(equipment);
    }

    @Override
    @Transactional
    public void cancelBorrow(String id) {
        InternalEquipmentBorrow borrow = internalMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }
        if (!"pending".equals(borrow.getStatus())) {
            throw new BusinessException("只能取消待审核的申请");
        }
        borrow.setStatus("cancelled");
        internalMapper.updateById(borrow);
    }
}