package ztt.fixedassetmanagement.service;

import com. baomidou. mybatisplus. extension.service.IService;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.ClassroomBorrow;

import java.util. List;
import java.util.Map;

public interface ClassroomService extends IService<ClassroomBorrow> {

    PageResult<ClassroomBorrow> getClassroomBorrowList(Map<String, Object> params);

    void submitClassroomBorrow(ClassroomBorrow borrow);

    void reviewClassroomBorrow(String id, Map<String, Object> data);

    List<Map<String, Object>> getClassroomOccupancy(Map<String, Object> params);
}