package ztt.fixedassetmanagement. service.impl;

import com.baomidou.mybatisplus.core.conditions.query. LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org. springframework.stereotype.Service;
import org.springframework.transaction. annotation.Transactional;
import org.springframework.util.StringUtils;
import ztt.fixedassetmanagement.common.BusinessException;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.Vehicle;
import ztt. fixedassetmanagement.entity.VehicleBorrow;
import ztt. fixedassetmanagement.mapper.VehicleBorrowMapper;
import ztt. fixedassetmanagement.mapper.VehicleMapper;
import ztt.fixedassetmanagement.service.VehicleService;
import ztt.fixedassetmanagement.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {

    private final VehicleBorrowMapper borrowMapper;

    @Override
    public PageResult<Vehicle> getVehicleList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params. get("page").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("plateNo"))) {
            wrapper.like(Vehicle::getPlateNo, params.get("plateNo"));
        }
        if (params.get("status") != null) {
            wrapper.eq(Vehicle::getStatus, params.get("status"));
        }

        wrapper.orderByDesc(Vehicle::getCreatedAt);

        Page<Vehicle> pageResult = page(new Page<>(page, pageSize), wrapper);

        return PageResult.of(pageResult. getRecords(), pageResult. getTotal(), page, pageSize);
    }

    @Override
    public Vehicle getVehicleDetail(String id) {
        Vehicle vehicle = getById(id);
        if (vehicle == null) {
            throw new BusinessException("车辆不存在");
        }
        return vehicle;
    }

    @Override
    public void createVehicle(Vehicle vehicle) {
        if (vehicle.getVehicleId() == null || vehicle.getVehicleId().isEmpty()) {
            vehicle.setVehicleId(IdGenerator. generateId("VH"));
        }
        if (vehicle.getStatus() == null) {
            vehicle.setStatus(1); // 1正常
        }
        save(vehicle);
    }

    @Override
    public void updateVehicle(String id, Vehicle vehicle) {
        Vehicle existing = getById(id);
        if (existing == null) {
            throw new BusinessException("车辆不存在");
        }
        vehicle.setVehicleId(id);
        updateById(vehicle);
    }

    @Override
    public void deleteVehicle(String id) {
        Vehicle vehicle = getById(id);
        if (vehicle == null) {
            throw new BusinessException("车辆不存在");
        }
        removeById(id);
    }

    @Override
    public List<Vehicle> getIdleVehicles() {
        return lambdaQuery(). eq(Vehicle::getStatus, 1).list();
    }

    @Override
    public PageResult<VehicleBorrow> getVehicleBorrowList(Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int pageSize = params. get("pageSize") != null ? Integer. parseInt(params.get("pageSize").toString()) : 10;

        LambdaQueryWrapper<VehicleBorrow> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText((String) params.get("vehicleId"))) {
            wrapper.eq(VehicleBorrow::getVehicleId, params. get("vehicleId"));
        }

        wrapper.orderByDesc(VehicleBorrow::getCreatedAt);

        Page<VehicleBorrow> pageResult = borrowMapper.selectPage(new Page<>(page, pageSize), wrapper);

        // 关联车辆信息
        pageResult.getRecords().forEach(borrow -> {
            Vehicle vehicle = getById(borrow.getVehicleId());
            if (vehicle != null) {
                borrow.setPlateNo(vehicle. getPlateNo());
            }
        });

        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), page, pageSize);
    }

    @Override
    @Transactional
    public void submitVehicleBorrow(VehicleBorrow borrow) {
        borrow.setBorrowId(IdGenerator.generateBorrowId());
        borrowMapper.insert(borrow);

        // 更新车辆状态为使用中
        Vehicle vehicle = getById(borrow.getVehicleId());
        vehicle.setStatus(3); // 3使用中
        updateById(vehicle);
    }

    @Override
    @Transactional
    public void returnVehicle(String id, Map<String, Object> data) {
        VehicleBorrow borrow = borrowMapper. selectById(id);
        if (borrow == null) {
            throw new BusinessException("借用记录不存在");
        }

        borrow. setActualReturnDate(LocalDate.now());
        borrowMapper.updateById(borrow);

        // 更新车辆状态
        Vehicle vehicle = getById(borrow. getVehicleId());
        vehicle. setStatus(1); // 1正常

        // 更新里程
        if (data. get("mileage") != null) {
            vehicle.setMileAge(Integer.parseInt(data.get("mileage").toString()));
        }

        updateById(vehicle);
    }
}