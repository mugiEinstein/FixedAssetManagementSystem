package ztt.fixedassetmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ztt.fixedassetmanagement.common.BusinessException;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.StudentDormAlloc;
import ztt.fixedassetmanagement.entity.HousingApplication;
import ztt.fixedassetmanagement.mapper.StudentDormAllocMapper;
import ztt.fixedassetmanagement.mapper.HousingApplicationMapper;
import ztt.fixedassetmanagement.service.StudentHousingService;
import ztt.fixedassetmanagement.util.IdGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentHousingServiceImpl extends ServiceImpl<StudentDormAllocMapper, StudentDormAlloc>
        implements StudentHousingService {

    private final HousingApplicationMapper housingApplicationMapper;

    @Override
    public PageResult<StudentDormAlloc> getStudentHousingList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<StudentDormAlloc> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("studentId"))) {
            wrapper.like(StudentDormAlloc::getStudentId, params.get("studentId"));
        }
        if (StringUtils.hasText((String) params.get("studentName"))) {
            wrapper. like(StudentDormAlloc::getStudentName, params. get("studentName"));
        }
        if (StringUtils. hasText((String) params.get("college"))) {
            wrapper.eq(StudentDormAlloc::getCollege, params.get("college"));
        }
        if (StringUtils.hasText((String) params.get("building"))) {
            wrapper.eq(StudentDormAlloc::getBuildingName, params.get("building"));
        }
        if (StringUtils.hasText((String) params.get("status"))) {
            wrapper.eq(StudentDormAlloc::getStatus, params. get("status"));
        }

        wrapper.orderByDesc(StudentDormAlloc::getCreatedAt);

        Page<StudentDormAlloc> pageResult = page(new Page<>(page, pageSize), wrapper);

        return PageResult.of(pageResult.getRecords(), pageResult. getTotal(), page, pageSize);
    }

    @Override
    public void checkInStudent(StudentDormAlloc alloc) {
        alloc.setAllocationId(IdGenerator.generateAllocationId());
        alloc.setStatus("active");
        if (alloc.getCheckInDate() == null) {
            alloc.setCheckInDate(LocalDate.now());
        }
        save(alloc);
    }

    @Override
    public void checkOutStudent(String id, Map<String, Object> data) {
        StudentDormAlloc alloc = getById(id);
        if (alloc == null) {
            throw new BusinessException("住宿记录不存在");
        }

        alloc. setStatus("checkout");
        alloc.setCheckOutDate(LocalDate.now());

        updateById(alloc);
    }

    @Override
    public void changeDormitory(String id, Map<String, Object> data) {
        StudentDormAlloc alloc = getById(id);
        if (alloc == null) {
            throw new BusinessException("住宿记录不存在");
        }

        // 更新新宿舍信息
        if (data.get("targetDormitoryId") != null) {
            alloc. setDormId((String) data.get("targetDormitoryId"));
        }
        if (data.get("buildingName") != null) {
            alloc.setBuildingName((String) data.get("buildingName"));
        }
        if (data.get("dormitoryCode") != null) {
            alloc.setDormitoryCode((String) data.get("dormitoryCode"));
        }
        if (data.get("bedNumber") != null) {
            alloc.setBedNumber(Integer.parseInt(data.get("bedNumber").toString()));
        }

        updateById(alloc);
    }

    @Override
    public PageResult<HousingApplication> getApplicationList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<HousingApplication> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("status"))) {
            wrapper.eq(HousingApplication::getStatus, params.get("status"));
        }
        if (StringUtils.hasText((String) params.get("applicationType"))) {
            wrapper.eq(HousingApplication::getApplicationType, params.get("applicationType"));
        }
        if (StringUtils.hasText((String) params.get("studentName"))) {
            wrapper.like(HousingApplication::getStudentName, params.get("studentName"));
        }

        wrapper.orderByDesc(HousingApplication::getApplyTime);

        Page<HousingApplication> pageObj = new Page<>(page, pageSize);
        Page<HousingApplication> result = housingApplicationMapper.selectPage(pageObj, wrapper);

        return PageResult.of(result.getRecords(), result.getTotal(), page, pageSize);
    }

    @Override
    public void submitTransferApplication(Map<String, Object> data) {
        HousingApplication app = new HousingApplication();
        app.setApplicationId("HA-" + System.currentTimeMillis());
        app.setStudentId((String) data.get("studentId"));
        app.setStudentNo((String) data.get("studentNo"));
        app.setStudentName((String) data.get("studentName"));
        app.setDepartment((String) data.get("department"));
        app.setGrade((String) data.get("grade"));
        app.setApplicationType("transfer");
        app.setCurrentDorm((String) data.get("currentDorm"));
        app.setReason((String) data.get("reasonType"));
        app.setReasonDetail((String) data.get("reason"));
        app.setPreferredDorm((String) data.get("preferredDorm"));
        app.setStatus("pending");
        app.setApplyTime(LocalDateTime.now());

        housingApplicationMapper.insert(app);
    }

    @Override
    public void submitLeaveApplication(Map<String, Object> data) {
        HousingApplication app = new HousingApplication();
        app.setApplicationId("HA-" + System.currentTimeMillis());
        app.setStudentId((String) data.get("studentId"));
        app.setStudentNo((String) data.get("studentNo"));
        app.setStudentName((String) data.get("studentName"));
        app.setDepartment((String) data.get("department"));
        app.setGrade((String) data.get("grade"));
        app.setApplicationType("leave");
        app.setCurrentDorm((String) data.get("currentDorm"));
        app.setReason((String) data.get("leaveType"));
        app.setReasonDetail((String) data.get("reason"));
        if (data.get("startDate") != null) {
            app.setLeaveStartDate(LocalDate.parse((String) data.get("startDate")));
        }
        if (data.get("endDate") != null) {
            app.setLeaveEndDate(LocalDate.parse((String) data.get("endDate")));
        }
        app.setStatus("pending");
        app.setApplyTime(LocalDateTime.now());

        housingApplicationMapper.insert(app);
    }

    @Override
    public void submitCheckoutApplication(Map<String, Object> data) {
        HousingApplication app = new HousingApplication();
        app.setApplicationId("HA-" + System.currentTimeMillis());
        app.setStudentId((String) data.get("studentId"));
        app.setStudentNo((String) data.get("studentNo"));
        app.setStudentName((String) data.get("studentName"));
        app.setDepartment((String) data.get("department"));
        app.setGrade((String) data.get("grade"));
        app.setApplicationType("checkout");
        app.setCurrentDorm((String) data.get("currentDorm"));
        app.setReason((String) data.get("reason"));
        app.setReasonDetail((String) data.get("description"));
        if (data.get("exitDate") != null) {
            app.setExitDate(LocalDate.parse((String) data.get("exitDate")));
        }
        app.setStatus("pending");
        app.setApplyTime(LocalDateTime.now());

        housingApplicationMapper.insert(app);
    }

    @Override
    public void reviewApplication(String id, Map<String, Object> data) {
        HousingApplication app = housingApplicationMapper.selectById(id);
        if (app == null) {
            throw new BusinessException("申请记录不存在");
        }

        Boolean approved = (Boolean) data.get("approved");
        app.setStatus(approved ? "approved" : "rejected");
        app.setReviewTime(LocalDateTime.now());
        app.setReviewer("管理员");
        app.setReviewReason((String) data.get("reason"));

        housingApplicationMapper.updateById(app);
    }

    @Override
    public Map<String, Object> getMyHousingInfo(String userId) {
        LambdaQueryWrapper<StudentDormAlloc> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentDormAlloc::getStudentId, userId)
               .in(StudentDormAlloc::getStatus, "入住中", "在住", "active");
        StudentDormAlloc alloc = getOne(wrapper);

        Map<String, Object> result = new java.util.HashMap<>();
        if (alloc != null) {
            result.put("hasHousing", true);
            result.put("data", alloc);
        } else {
            result.put("hasHousing", false);
        }
        return result;
    }
}