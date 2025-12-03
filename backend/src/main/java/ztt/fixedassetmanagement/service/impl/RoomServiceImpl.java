package ztt.fixedassetmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query. LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins. pagination.Page;
import com.baomidou.mybatisplus.extension. service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ztt.fixedassetmanagement.common.BusinessException;
import ztt.fixedassetmanagement.common. PageResult;
import ztt.fixedassetmanagement. entity.RoomResource;
import ztt.fixedassetmanagement.mapper. RoomResourceMapper;
import ztt. fixedassetmanagement.service.RoomService;
import ztt.fixedassetmanagement. util.IdGenerator;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl extends ServiceImpl<RoomResourceMapper, RoomResource>
        implements RoomService {

    @Override
    public PageResult<RoomResource> getRoomList(Map<String, Object> params) {
        int page = params.get("page") != null ?  Integer.parseInt(params.get("page"). toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<RoomResource> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params. get("buildingId"))) {
            wrapper.eq(RoomResource::getBuildingId, params.get("buildingId"));
        }
        if (StringUtils.hasText((String) params. get("roomType"))) {
            wrapper.eq(RoomResource::getRoomType, params.get("roomType"));
        }
        if (StringUtils.hasText((String) params.get("status"))) {
            wrapper.eq(RoomResource::getStatus, params.get("status"));
        }

        wrapper. orderByAsc(RoomResource::getRoomNumber);

        Page<RoomResource> pageResult = page(new Page<>(page, pageSize), wrapper);

        return PageResult. of(pageResult. getRecords(), pageResult.getTotal(), page, pageSize);
    }

    @Override
    public RoomResource getRoomDetail(String id) {
        RoomResource room = getById(id);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        return room;
    }

    @Override
    public void createRoom(RoomResource room) {
        if (room.getRoomId() == null || room.getRoomId().isEmpty()) {
            room.setRoomId(IdGenerator.generateId("RM"));
        }
        if (room.getStatus() == null) {
            room.setStatus("IDLE");
        }
        save(room);
    }

    @Override
    public void updateRoom(String id, RoomResource room) {
        RoomResource existing = getById(id);
        if (existing == null) {
            throw new BusinessException("房间不存在");
        }
        room.setRoomId(id);
        updateById(room);
    }

    @Override
    public void deleteRoom(String id) {
        RoomResource room = getById(id);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        removeById(id);
    }

    @Override
    public List<RoomResource> getIdleRooms(Map<String, Object> params) {
        LambdaQueryWrapper<RoomResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomResource::getStatus, "IDLE");

        if (StringUtils.hasText((String) params.get("type"))) {
            wrapper.eq(RoomResource::getRoomType, params.get("type"));
        }
        if (params.get("minArea") != null) {
            wrapper.ge(RoomResource::getArea, params. get("minArea"));
        }
        if (params.get("maxArea") != null) {
            wrapper.le(RoomResource::getArea, params.get("maxArea"));
        }

        return list(wrapper);
    }

    @Override
    public List<Map<String, Object>> getClassroomOccupancy(Map<String, Object> params) {
        // 获取教室列表
        LambdaQueryWrapper<RoomResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomResource::getRoomType, "教室");

        if (params.get("buildingId") != null) {
            wrapper.eq(RoomResource::getBuildingId, params.get("buildingId"));
        }
        if (params.get("capacity") != null) {
            wrapper.ge(RoomResource::getCapacity, params.get("capacity"));
        }

        List<RoomResource> rooms = list(wrapper);

        // 构建返回数据（实际应查询教室借用表获取占用情况）
        List<Map<String, Object>> result = new ArrayList<>();
        for (RoomResource room : rooms) {
            Map<String, Object> item = new HashMap<>();
            item.put("roomId", room. getRoomId());
            item.put("roomCode", room.getRoomNumber());
            item. put("buildingName", room.getBuildingId()); // 实际应关联查询
            item.put("floor", room.getFloor());
            item.put("capacity", room.getCapacity());
            item.put("equipment", room.getEquipment());
            item.put("occupiedSlots", new ArrayList<>()); // 实际应查询借用记录
            item.put("availableSlots", Arrays.asList(
                    Map.of("startTime", "08:00", "endTime", "12:00"),
                    Map.of("startTime", "14:00", "endTime", "18:00")
            ));
            result.add(item);
        }

        return result;
    }
}