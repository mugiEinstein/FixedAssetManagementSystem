package ztt.fixedassetmanagement. service.impl;

import com.baomidou.mybatisplus.core. conditions.query.LambdaQueryWrapper;
import com. baomidou. mybatisplus. extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok. RequiredArgsConstructor;
import org.springframework.stereotype. Service;
import org.springframework.util.StringUtils;
import ztt.fixedassetmanagement.common.BusinessException;
import ztt.fixedassetmanagement.common. PageResult;
import ztt.fixedassetmanagement. entity.ClassroomBorrow;
import ztt.fixedassetmanagement. entity.RoomResource;
import ztt.fixedassetmanagement.mapper. ClassroomBorrowMapper;
import ztt.fixedassetmanagement. mapper.RoomResourceMapper;
import ztt.fixedassetmanagement.service.ClassroomService;
import ztt.fixedassetmanagement.util. IdGenerator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl extends ServiceImpl<ClassroomBorrowMapper, ClassroomBorrow>
        implements ClassroomService {

    private final RoomResourceMapper roomMapper;

    @Override
    public PageResult<ClassroomBorrow> getClassroomBorrowList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params. get("pageSize") != null ? Integer. parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<ClassroomBorrow> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("applicationNo"))) {
            wrapper.like(ClassroomBorrow::getBorrowId, params.get("applicationNo"));
        }
        if (StringUtils.hasText((String) params.get("purpose"))) {
            wrapper.eq(ClassroomBorrow::getBorrowPurpose, params.get("purpose"));
        }
        if (StringUtils.hasText((String) params.get("status"))) {
            wrapper.eq(ClassroomBorrow::getStatus, params. get("status"));
        }
        // 按申请人过滤（用于普通用户只查看自己的记录）
        if (StringUtils.hasText((String) params.get("applicantName"))) {
            wrapper.eq(ClassroomBorrow::getApplicantName, params.get("applicantName"));
        }

        wrapper.orderByDesc(ClassroomBorrow::getCreatedAt);

        Page<ClassroomBorrow> pageResult = page(new Page<>(page, pageSize), wrapper);

        // 关联教室信息
        pageResult.getRecords().forEach(borrow -> {
            RoomResource room = roomMapper.selectById(borrow.getClassroomId());
            if (room != null) {
                borrow.setRoomCode(room.getRoomNumber());
                borrow.setRoomName(room.getRoomName() != null ? room.getRoomName() : room.getRoomNumber());
            }
        });

        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), page, pageSize);
    }

    @Override
    public void submitClassroomBorrow(ClassroomBorrow borrow) {
        borrow.setBorrowId(IdGenerator.generateBorrowId());
        borrow.setStatus("pending");
        
        // 前端发送的字段映射到数据库字段
        // roomId -> classroomId
        if (!StringUtils.hasText(borrow.getClassroomId()) && StringUtils.hasText(borrow.getRoomId())) {
            borrow.setClassroomId(borrow.getRoomId());
        }
        
        // purpose -> borrowPurpose
        if (!StringUtils.hasText(borrow.getBorrowPurpose()) && StringUtils.hasText(borrow.getPurpose())) {
            borrow.setBorrowPurpose(borrow.getPurpose());
        }
        
        // 处理借用日期字段
        if (borrow.getBorrowDateStr() != null && !borrow.getBorrowDateStr().isEmpty()) {
            try {
                borrow.setBorrowDate(LocalDate.parse(borrow.getBorrowDateStr(), DateTimeFormatter.ISO_LOCAL_DATE));
            } catch (Exception e) {
                // 日期解析失败时使用当前日期
                borrow.setBorrowDate(LocalDate.now());
            }
        } else if (borrow.getBorrowDate() == null) {
            borrow.setBorrowDate(LocalDate.now());
        }
        
        // 组合借用时间信息: borrowDate + startTime + endTime -> borrowTime
        if (borrow.getBorrowDate() != null && StringUtils.hasText(borrow.getStartTime()) && StringUtils.hasText(borrow.getEndTime())) {
            String borrowTime = borrow.getBorrowDate().toString() + " " + borrow.getStartTime() + "-" + borrow.getEndTime();
            borrow.setBorrowTime(borrowTime);
        }
        
        // 确保 applicantDept 不为空
        if (!StringUtils.hasText(borrow.getApplicantDept())) {
            borrow.setApplicantDept(borrow.getApplicantName() != null ? borrow.getApplicantName() : "未知部门");
        }
        
        save(borrow);
    }

    @Override
    public void reviewClassroomBorrow(String id, Map<String, Object> data) {
        ClassroomBorrow borrow = getById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }

        boolean approved = (Boolean) data.get("approved");
        borrow.setDepartmentApproval(approved ? "approved" : "rejected");
        borrow.setStatus(approved ? "approved" : "rejected");

        updateById(borrow);
    }

    @Override
    public List<Map<String, Object>> getClassroomOccupancy(Map<String, Object> params) {
        // 获取所有教室
        LambdaQueryWrapper<RoomResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomResource::getRoomType, "教室");

        // 支持多种参数名
        String buildingId = params.get("buildingId") != null ? params.get("buildingId").toString() : null;
        String building = params.get("building") != null ? params.get("building").toString() : null;
        
        if (buildingId != null) {
            wrapper.eq(RoomResource::getBuildingId, buildingId);
        } else if (building != null && !building.isEmpty()) {
            // 根据建筑名称模糊匹配buildingId
            wrapper.like(RoomResource::getBuildingId, building);
        }
        
        // 支持 capacity 和 minCapacity 参数
        Object capacityObj = params.get("capacity") != null ? params.get("capacity") : params.get("minCapacity");
        if (capacityObj != null) {
            try {
                int capacity = Integer.parseInt(capacityObj.toString());
                if (capacity > 0) {
                    wrapper.ge(RoomResource::getCapacity, capacity);
                }
            } catch (NumberFormatException ignored) {}
        }

        List<RoomResource> rooms = roomMapper.selectList(wrapper);

        String date = (String) params.get("date");

        List<Map<String, Object>> result = new ArrayList<>();
        for (RoomResource room : rooms) {
            Map<String, Object> item = new HashMap<>();
            item.put("roomId", room.getRoomId());
            item.put("roomName", room.getRoomName() != null ? room.getRoomName() : room.getRoomNumber());
            item.put("roomCode", room.getRoomNumber());
            item.put("buildingId", room.getBuildingId());
            // 从房间号解析楼层 (如 R-B-A-01-03-0201 中的 02 是楼层)
            String roomNum = room.getRoomNumber();
            int floor = 1;
            if (roomNum != null && roomNum.length() > 4) {
                try {
                    String floorStr = roomNum.substring(roomNum.length() - 4, roomNum.length() - 2);
                    floor = Integer.parseInt(floorStr);
                } catch (Exception ignored) {}
            }
            item.put("floor", floor);
            item.put("building", getBuildingName(room.getBuildingId()));
            item.put("capacity", room.getCapacity());
            item.put("area", room.getArea());
            item.put("equipment", "多媒体设备");

            // 查询该日期的借用情况
            LambdaQueryWrapper<ClassroomBorrow> borrowWrapper = new LambdaQueryWrapper<>();
            borrowWrapper.eq(ClassroomBorrow::getClassroomId, room.getRoomId());
            if (date != null) {
                // 使用 borrowTime 字段进行模糊匹配日期
                borrowWrapper.like(ClassroomBorrow::getBorrowTime, date);
            }
            borrowWrapper.eq(ClassroomBorrow::getStatus, "approved");

            List<ClassroomBorrow> borrows = list(borrowWrapper);

            List<Map<String, String>> occupiedSlots = new ArrayList<>();
            for (ClassroomBorrow borrow : borrows) {
                Map<String, String> slot = new HashMap<>();
                slot.put("startTime", borrow.getStartTime() != null ? borrow.getStartTime() : "08:00");
                slot.put("endTime", borrow.getEndTime() != null ? borrow.getEndTime() : "22:00");
                occupiedSlots.add(slot);
            }
            item.put("occupiedSlots", occupiedSlots);

            // 计算空闲时段（简化处理）
            List<Map<String, String>> availableSlots = new ArrayList<>();
            if (occupiedSlots.isEmpty()) {
                availableSlots.add(Map.of("startTime", "08:00", "endTime", "22:00"));
            }
            item.put("availableSlots", availableSlots);

            result.add(item);
        }

        return result;
    }

    // 根据建筑ID获取建筑名称
    private String getBuildingName(String buildingId) {
        if (buildingId == null) return "未知";
        Map<String, String> buildingMap = new HashMap<>();
        buildingMap.put("B-A-01-03", "教学楼A");
        buildingMap.put("B-A-01-04", "教学楼B");
        buildingMap.put("B-F-01-01", "逸夫楼");
        buildingMap.put("B-H-01-02", "图书馆");
        buildingMap.put("B-C-01-05", "实验楼");
        // 尝试匹配
        for (Map.Entry<String, String> entry : buildingMap.entrySet()) {
            if (buildingId.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "教学楼";
    }
}