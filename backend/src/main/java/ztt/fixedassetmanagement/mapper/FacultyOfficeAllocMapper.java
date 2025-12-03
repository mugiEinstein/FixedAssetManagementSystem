package ztt.fixedassetmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import ztt.fixedassetmanagement.entity.FacultyOfficeAlloc;

import java.util.List;
import java.util.Map;

@Mapper
public interface FacultyOfficeAllocMapper extends BaseMapper<FacultyOfficeAlloc> {

    @Select("SELECT a.allocation_id, a.user_id, a.room_id, " +
            "a.standard_area, a.actual_area, a.over_area_fee, a.status, " +
            "a.created_at, a.updated_at, " +
            "u.real_name as applicantName, u.dept as department, u.staff_level as staffLevel " +
            "FROM faculty_office_alloc a " +
            "LEFT JOIN sys_user u ON a.user_id = u.user_id " +
            "WHERE a.is_deleted = 0 " +
            "ORDER BY a.created_at DESC")
    List<Map<String, Object>> selectListWithUserInfo();

    @Select("SELECT a.allocation_id as allocationId, a.user_id as userId, a.room_id as roomId, " +
            "a.standard_area as standardArea, a.actual_area as actualArea, " +
            "a.over_area_fee as overAreaFee, a.status, " +
            "u.real_name as applicantName, u.dept as department, u.staff_level as staffLevel " +
            "FROM faculty_office_alloc a " +
            "LEFT JOIN sys_user u ON a.user_id = u.user_id " +
            "WHERE a.is_deleted = 0 " +
            "ORDER BY a.created_at DESC " +
            "LIMIT #{offset}, #{size}")
    List<Map<String, Object>> selectPageWithUserInfo(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM faculty_office_alloc WHERE is_deleted = 0")
    long countAll();
}