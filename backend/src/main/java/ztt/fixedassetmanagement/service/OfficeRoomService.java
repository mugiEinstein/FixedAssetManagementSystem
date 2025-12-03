package ztt.fixedassetmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.FacultyOfficeAlloc;

import java.util.Map;

public interface OfficeRoomService extends IService<FacultyOfficeAlloc> {

    PageResult<Map<String, Object>> getApplicationList(Map<String, Object> params);

    // 新增别名方法供Controller使用
    default PageResult<Map<String, Object>> getOfficeRoomList(Map<String, Object> params) {
        return getApplicationList(params);
    }

    void submitApplication(FacultyOfficeAlloc alloc);

    void reviewByDepartment(String id, Map<String, Object> data);

    void verifyTitle(String id, Map<String, Object> data);

    void reviewMaterial(String id, Map<String, Object> data);

    void allocateRoom(String id, Map<String, Object> data);

    // 审核申请（简化版）
    void reviewApplication(String id, Map<String, Object> data);

    // 退还办公用房
    void returnOfficeRoom(String id, Map<String, Object> data);
}