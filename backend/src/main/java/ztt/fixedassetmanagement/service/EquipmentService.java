package ztt. fixedassetmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity. EquipmentArchive;

import java.util.Map;

public interface EquipmentService extends IService<EquipmentArchive> {

    PageResult<EquipmentArchive> getEquipmentList(Map<String, Object> params);

    EquipmentArchive getEquipmentDetail(String id);

    Map<String, Object> getEquipmentNetValue(String id);

    void createEquipment(EquipmentArchive equipment);

    void updateEquipment(String id, EquipmentArchive equipment);

    void deleteEquipment(String id);
}