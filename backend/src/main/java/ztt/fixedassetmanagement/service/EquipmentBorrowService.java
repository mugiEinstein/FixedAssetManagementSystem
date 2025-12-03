package ztt.fixedassetmanagement.service;

import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity. OutboundEquipmentBorrow;
import ztt.fixedassetmanagement.entity. InternalEquipmentBorrow;

import java.util. Map;

public interface EquipmentBorrowService {

    // 校外借用
    PageResult<OutboundEquipmentBorrow> getExternalBorrowList(Map<String, Object> params);

    OutboundEquipmentBorrow getExternalBorrowDetail(String id);

    void submitExternalBorrow(OutboundEquipmentBorrow borrow);

    void reviewExternalQualification(String id, Map<String, Object> data);

    void reviewExternalEquipment(String id, Map<String, Object> data);

    void confirmExternalPayment(String id);

    void confirmExternalPickup(String id);

    void returnExternalEquipment(String id, Map<String, Object> data);

    void applyExternalRenewal(String id, Map<String, Object> data);

    // 校内借用
    PageResult<InternalEquipmentBorrow> getInternalBorrowList(Map<String, Object> params);

    InternalEquipmentBorrow getInternalBorrowDetail(String id);

    void submitInternalBorrow(InternalEquipmentBorrow borrow);

    void reviewInternalPermission(String id, Map<String, Object> data);

    void reviewInternalEquipment(String id, Map<String, Object> data);

    void confirmInternalPickup(String id);

    void returnInternalEquipment(String id, Map<String, Object> data);

    // 取消借用申请
    void cancelBorrow(String id);
}