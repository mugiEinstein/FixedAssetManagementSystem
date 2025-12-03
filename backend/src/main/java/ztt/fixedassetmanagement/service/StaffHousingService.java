package ztt. fixedassetmanagement.service;

import com.baomidou.mybatisplus.extension. service.IService;
import ztt. fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.FacultyDormAlloc;

import java.util.Map;

public interface StaffHousingService extends IService<FacultyDormAlloc> {

    PageResult<FacultyDormAlloc> getStaffHousingList(Map<String, Object> params);

    void submitApplication(FacultyDormAlloc alloc);

    void reviewQualification(String id, Map<String, Object> data);

    void reviewFinance(String id, Map<String, Object> data);

    void allocateRoom(String id, Map<String, Object> data);

    void signContract(String id, Map<String, Object> data);
}