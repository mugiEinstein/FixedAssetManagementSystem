package ztt. fixedassetmanagement.service.impl;

import com. baomidou. mybatisplus. core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype. Service;
import org.springframework.transaction. annotation.Transactional;
import org.springframework.util. StringUtils;
import ztt.fixedassetmanagement. common.BusinessException;
import ztt. fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.FacultyDormAlloc;
import ztt.fixedassetmanagement.entity. RoomResource;
import ztt.fixedassetmanagement. mapper.FacultyDormAllocMapper;
import ztt.fixedassetmanagement. mapper.RoomResourceMapper;
import ztt.fixedassetmanagement.service.StaffHousingService;
import ztt.fixedassetmanagement. util.FeeCalculator;
import ztt.fixedassetmanagement.util.IdGenerator;

import java.math. BigDecimal;
import java.time. LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StaffHousingServiceImpl extends ServiceImpl<FacultyDormAllocMapper, FacultyDormAlloc>
        implements StaffHousingService {

    private final RoomResourceMapper roomMapper;

    @Override
    public PageResult<FacultyDormAlloc> getStaffHousingList(Map<String, Object> params) {
        int page = params. get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params. get("pageSize"). toString()) : 10;

        LambdaQueryWrapper<FacultyDormAlloc> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("applicationNo"))) {
            wrapper.like(FacultyDormAlloc::getAllocationId, params.get("applicationNo"));
        }
        if (StringUtils.hasText((String) params.get("staffName"))) {
            wrapper.like(FacultyDormAlloc::getStaffName, params. get("staffName"));
        }
        if (StringUtils. hasText((String) params.get("status"))) {
            wrapper.eq(FacultyDormAlloc::getStatus, params.get("status"));
        }

        wrapper.orderByDesc(FacultyDormAlloc::getCreatedAt);

        Page<FacultyDormAlloc> pageResult = page(new Page<>(page, pageSize), wrapper);

        return PageResult.of(pageResult.getRecords(), pageResult. getTotal(), page, pageSize);
    }

    @Override
    public void submitApplication(FacultyDormAlloc alloc) {
        alloc.setAllocationId(IdGenerator.generateAllocationId());
        alloc.setStatus("pending");
        alloc.setQualificationApproval("pending");
        alloc.setFinanceApproval("pending");

        save(alloc);
    }

    @Override
    public void reviewQualification(String id, Map<String, Object> data) {
        FacultyDormAlloc alloc = getById(id);
        if (alloc == null) {
            throw new BusinessException("申请记录不存在");
        }

        boolean approved = (Boolean) data.get("approved");
        alloc.setQualificationApproval(approved ? "approved" : "rejected");

        if (!approved) {
            alloc.setStatus("rejected");
        }

        updateById(alloc);
    }

    @Override
    public void reviewFinance(String id, Map<String, Object> data) {
        FacultyDormAlloc alloc = getById(id);
        if (alloc == null) {
            throw new BusinessException("申请记录不存在");
        }

        boolean approved = (Boolean) data.get("approved");
        alloc. setFinanceApproval(approved ? "approved" : "rejected");

        if (!approved) {
            alloc. setStatus("rejected");
        }

        updateById(alloc);
    }

    @Override
    @Transactional
    public void allocateRoom(String id, Map<String, Object> data) {
        FacultyDormAlloc alloc = getById(id);
        if (alloc == null) {
            throw new BusinessException("申请记录不存在");
        }

        String roomId = (String) data.get("roomId");

        // 获取房间信息
        RoomResource room = roomMapper.selectById(roomId);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }

        alloc.setDormId(roomId);
        alloc.setArea(room.getArea());

        // 计算租金：面积 × 90元/㎡
        BigDecimal rent = FeeCalculator.calculateStaffHousingRent(room.getArea());
        alloc. setRentAmount(rent);

        updateById(alloc);

        // 更新房间状态
        room.setStatus("IN_USE");
        room.setCurrentUser(alloc.getStaffName());
        roomMapper.updateById(room);
    }

    @Override
    @Transactional
    public void signContract(String id, Map<String, Object> data) {
        FacultyDormAlloc alloc = getById(id);
        if (alloc == null) {
            throw new BusinessException("申请记录不存在");
        }

        // 设置合同期限
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate. plusYears(1); // 默认一年

        if (data.get("contractStart") != null) {
            startDate = LocalDate. parse((String) data.get("contractStart"));
        }
        if (data. get("contractEnd") != null) {
            endDate = LocalDate.parse((String) data.get("contractEnd"));
        }

        alloc.setContractStart(startDate);
        alloc.setContractEnd(endDate);
        alloc.setStatus("approved");

        updateById(alloc);
    }
}