package ztt.fixedassetmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.RoomResource;

import java. util.List;
import java. util.Map;

public interface RoomService extends IService<RoomResource> {

    PageResult<RoomResource> getRoomList(Map<String, Object> params);

    RoomResource getRoomDetail(String id);

    void createRoom(RoomResource room);

    void updateRoom(String id, RoomResource room);

    void deleteRoom(String id);

    List<RoomResource> getIdleRooms(Map<String, Object> params);

    List<Map<String, Object>> getClassroomOccupancy(Map<String, Object> params);
}