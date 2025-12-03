package ztt.fixedassetmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ztt.fixedassetmanagement.common.BusinessException;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.FacultyOfficeAlloc;
import ztt.fixedassetmanagement.entity.RoomResource;
import ztt.fixedassetmanagement.entity.SysUser;
import ztt.fixedassetmanagement.mapper.FacultyOfficeAllocMapper;
import ztt.fixedassetmanagement.mapper.RoomResourceMapper;
import ztt.fixedassetmanagement.mapper.SysUserMapper;
import ztt.fixedassetmanagement.service.OfficeRoomService;
import ztt.fixedassetmanagement.util.FeeCalculator;
import ztt.fixedassetmanagement.util.IdGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OfficeRoomServiceImpl extends ServiceImpl<FacultyOfficeAllocMapper, FacultyOfficeAlloc>
        implements OfficeRoomService {

    private final RoomResourceMapper roomMapper;
    private final SysUserMapper userMapper;

    @Override
    public PageResult<Map<String, Object>> getApplicationList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        int offset = (page - 1) * pageSize;

        // 使用JOIN查询获取用户信息
        List<Map<String, Object>> list = baseMapper.selectPageWithUserInfo(offset, pageSize);
        long total = baseMapper.countAll();

        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    public void submitApplication(FacultyOfficeAlloc alloc) {
        alloc.setAllocationId(IdGenerator.generateAllocationId());
        
        // 根据申请人姓名查找userId
        if (alloc.getApplicantName() != null && !alloc.getApplicantName().isEmpty()) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getRealName, alloc.getApplicantName());
            SysUser user = userMapper.selectOne(wrapper);
            if (user != null) {
                alloc.setUserId(user.getUserId());
            } else {
                // 找不到用户，生成临时ID
                alloc.setUserId("U" + System.currentTimeMillis());
            }
        } else if (alloc.getUserId() == null || alloc.getUserId().isEmpty()) {
            alloc.setUserId("U" + System.currentTimeMillis());
        }
        
        alloc.setStatus("pending");
        save(alloc);
    }

    @Override
    public void reviewByDepartment(String id, Map<String, Object> data) {
        FacultyOfficeAlloc alloc = getById(id);
        if (alloc == null) {
            throw new BusinessException("申请记录不存在");
        }
        boolean approved = (Boolean) data.get("approved");
        alloc.setStatus(approved ? "approved" : "rejected");
        updateById(alloc);
    }

    @Override
    public void verifyTitle(String id, Map<String, Object> data) {
        // 职称验证（简化处理）
        reviewByDepartment(id, data);
    }

    @Override
    public void reviewMaterial(String id, Map<String, Object> data) {
        // 材料审核（简化处理）
        reviewByDepartment(id, data);
    }

    @Override
    @Transactional
    public void allocateRoom(String id, Map<String, Object> data) {
        FacultyOfficeAlloc alloc = getById(id);
        if (alloc == null) {
            throw new BusinessException("申请记录不存在");
        }

        String roomId = (String) data.get("roomId");
        BigDecimal allocatedArea = new BigDecimal(data.get("allocatedArea").toString());

        alloc.setRoomId(roomId);
        alloc.setActualArea(allocatedArea);
        alloc.setStatus("approved");

        // 计算超面积费用
        if (alloc.getStandardArea() != null && allocatedArea.compareTo(alloc.getStandardArea()) > 0) {
            BigDecimal excess = allocatedArea.subtract(alloc.getStandardArea());
            alloc.setOverAreaFee(excess.multiply(new BigDecimal("50")));
        }

        updateById(alloc);

        // 更新房间状态
        RoomResource room = roomMapper.selectById(roomId);
        if (room != null) {
            room.setStatus("IN_USE");
            roomMapper.updateById(room);
        }
    }

    @Override
    public void reviewApplication(String id, Map<String, Object> data) {
        FacultyOfficeAlloc alloc = getById(id);
        if (alloc == null) {
            throw new BusinessException("申请记录不存在");
        }

        boolean approved = (Boolean) data.get("approved");
        String roomId = (String) data.get("roomId");

        if (approved) {
            alloc.setStatus("approved");
            if (roomId != null) {
                alloc.setRoomId(roomId);
            }
        } else {
            alloc.setStatus("rejected");
        }

        updateById(alloc);
    }

    @Override
    @Transactional
    public void returnOfficeRoom(String id, Map<String, Object> data) {
        FacultyOfficeAlloc alloc = getById(id);
        if (alloc == null) {
            throw new BusinessException("申请记录不存在");
        }

        // 释放房间
        if (alloc.getRoomId() != null) {
            RoomResource room = roomMapper.selectById(alloc.getRoomId());
            if (room != null) {
                room.setStatus("IDLE");
                room.setCurrentUser(null);
                roomMapper.updateById(room);
            }
        }

        // 更新分配状态
        alloc.setStatus("returned");
        alloc.setRoomId(null);
        updateById(alloc);
    }
}