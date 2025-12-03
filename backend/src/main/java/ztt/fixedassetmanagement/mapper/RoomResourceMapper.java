package ztt.fixedassetmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org. apache.ibatis. annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import ztt.fixedassetmanagement.entity.RoomResource;
import java.util. List;

@Mapper
public interface RoomResourceMapper extends BaseMapper<RoomResource> {

    @Select("SELECT r.*, b.building_name FROM room_resource r " +
            "LEFT JOIN building_archive b ON r. building_id = b.building_id " +
            "WHERE r. is_deleted = 0")
    List<RoomResource> selectWithBuilding();
}