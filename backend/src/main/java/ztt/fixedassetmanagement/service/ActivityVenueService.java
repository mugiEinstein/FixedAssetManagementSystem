package ztt.fixedassetmanagement.service;

import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.entity.ExternalEventBooking;
import ztt. fixedassetmanagement.entity.InternalEventBooking;

import java.util. List;
import java.util.Map;

public interface ActivityVenueService {

    // 校外活动
    PageResult<ExternalEventBooking> getExternalEventList(Map<String, Object> params);

    void submitExternalEvent(ExternalEventBooking event);

    void reviewExternalQualification(String id, Map<String, Object> data);

    void reviewExternalVenue(String id, Map<String, Object> data);

    void confirmExternalPayment(String id);

    void signExternalContract(String id);

    // 校内活动
    PageResult<InternalEventBooking> getInternalEventList(Map<String, Object> params);

    void submitInternalEvent(InternalEventBooking event);

    void reviewInternalEvent(String id, Map<String, Object> data);

    void confirmInternalVenue(String id);

    // 场地查询
    List<Map<String, Object>> getIdleVenues(Map<String, Object> params);
}