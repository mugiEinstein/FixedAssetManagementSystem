package ztt.fixedassetmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.StudentDormAlloc;
import ztt.fixedassetmanagement.entity.HousingApplication;

import java.util.Map;

public interface StudentHousingService extends IService<StudentDormAlloc> {

    PageResult<StudentDormAlloc> getStudentHousingList(Map<String, Object> params);

    void checkInStudent(StudentDormAlloc alloc);

    void checkOutStudent(String id, Map<String, Object> data);

    void changeDormitory(String id, Map<String, Object> data);

    // 住宿申请相关
    PageResult<HousingApplication> getApplicationList(Map<String, Object> params);

    void submitTransferApplication(Map<String, Object> data);

    void submitLeaveApplication(Map<String, Object> data);

    void submitCheckoutApplication(Map<String, Object> data);

    void reviewApplication(String id, Map<String, Object> data);

    Map<String, Object> getMyHousingInfo(String userId);
}