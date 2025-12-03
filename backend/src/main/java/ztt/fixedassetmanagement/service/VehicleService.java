package ztt.fixedassetmanagement. service;

import com.baomidou.mybatisplus.extension.service. IService;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.Vehicle;
import ztt. fixedassetmanagement.entity.VehicleBorrow;

import java.util.List;
import java. util.Map;

public interface VehicleService extends IService<Vehicle> {

    PageResult<Vehicle> getVehicleList(Map<String, Object> params);

    Vehicle getVehicleDetail(String id);

    void createVehicle(Vehicle vehicle);

    void updateVehicle(String id, Vehicle vehicle);

    void deleteVehicle(String id);

    List<Vehicle> getIdleVehicles();

    PageResult<VehicleBorrow> getVehicleBorrowList(Map<String, Object> params);

    void submitVehicleBorrow(VehicleBorrow borrow);

    void returnVehicle(String id, Map<String, Object> data);
}