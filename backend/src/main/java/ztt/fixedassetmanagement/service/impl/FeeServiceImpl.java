package ztt.fixedassetmanagement. service.impl;

import com.baomidou.mybatisplus.core. conditions.query.LambdaQueryWrapper;
import com. baomidou. mybatisplus. extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok. RequiredArgsConstructor;
import org.springframework.stereotype. Service;
import org.springframework.util. StringUtils;
import ztt.fixedassetmanagement. common.BusinessException;
import ztt. fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.FeeRecord;
import ztt. fixedassetmanagement.mapper.FeeRecordMapper;
import ztt. fixedassetmanagement.service.FeeService;
import ztt.fixedassetmanagement.util.IdGenerator;

import java. math.BigDecimal;
import java. time.LocalDateTime;
import java. util. HashMap;
import java. util.Map;

@Service
@RequiredArgsConstructor
public class FeeServiceImpl extends ServiceImpl<FeeRecordMapper, FeeRecord>
        implements FeeService {

    @Override
    public PageResult<FeeRecord> getFeeRecordList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params. get("pageSize") != null ? Integer. parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("feeType"))) {
            wrapper.eq(FeeRecord::getFeeType, params.get("feeType"));
        }
        if (StringUtils.hasText((String) params.get("businessType"))) {
            wrapper.eq(FeeRecord::getBusinessType, params. get("businessType"));
        }
        if (StringUtils. hasText((String) params.get("status"))) {
            wrapper.eq(FeeRecord::getStatus, params.get("status"));
        }

        wrapper.orderByDesc(FeeRecord::getCreatedAt);

        Page<FeeRecord> pageResult = page(new Page<>(page, pageSize), wrapper);

        return PageResult.of(pageResult.getRecords(), pageResult. getTotal(), page, pageSize);
    }

    @Override
    public void createFeeRecord(FeeRecord record) {
        record.setFeeId(IdGenerator.generateFeeId());
        record. setStatus("pending");
        save(record);
    }

    @Override
    public void confirmPayment(String id, Map<String, Object> data) {
        FeeRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("费用记录不存在");
        }

        record.setStatus("paid");
        record. setPaymentMethod((String) data.get("paymentMethod"));
        record.setPaymentTime(LocalDateTime.now());
        if (data.get("remarks") != null) {
            record.setRemarks((String) data.get("remarks"));
        }

        updateById(record);
    }

    @Override
    public void processRefund(String id, Map<String, Object> data) {
        FeeRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("费用记录不存在");
        }

        BigDecimal refundAmount = new BigDecimal(data.get("refundAmount").toString());
        record.setRefundAmount(refundAmount);
        record.setRefundTime(LocalDateTime.now());

        if (refundAmount. compareTo(record.getAmount()) >= 0) {
            record.setStatus("refunded");
        } else {
            record.setStatus("partial_refund");
        }

        updateById(record);
    }

    @Override
    public Map<String, Object> getStatistics(Map<String, Object> params) {
        Map<String, Object> stats = new HashMap<>();

        // 统计总收入
        LambdaQueryWrapper<FeeRecord> incomeWrapper = new LambdaQueryWrapper<>();
        incomeWrapper.eq(FeeRecord::getStatus, "paid");
        BigDecimal totalIncome = list(incomeWrapper). stream()
                .map(FeeRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats. put("totalIncome", totalIncome);

        // 统计总退款
        LambdaQueryWrapper<FeeRecord> refundWrapper = new LambdaQueryWrapper<>();
        refundWrapper.in(FeeRecord::getStatus, "refunded", "partial_refund");
        BigDecimal totalRefund = list(refundWrapper).stream()
                .map(r -> r.getRefundAmount() != null ? r. getRefundAmount() : BigDecimal. ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats. put("totalRefund", totalRefund);

        // 净收入
        stats.put("netIncome", totalIncome.subtract(totalRefund));

        // 待收款
        LambdaQueryWrapper<FeeRecord> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(FeeRecord::getStatus, "pending");
        BigDecimal pendingAmount = list(pendingWrapper).stream()
                .map(FeeRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats. put("pendingAmount", pendingAmount);

        return stats;
    }
}