package ztt.fixedassetmanagement.controller;

import io.swagger.v3. oas.annotations.Operation;
import io. swagger.v3. oas.annotations. tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind. annotation.*;
import ztt.fixedassetmanagement.common.PageResult;
import ztt.fixedassetmanagement.common.Result;
import ztt.fixedassetmanagement. entity.ExternalEventBooking;
import ztt.fixedassetmanagement. entity.InternalEventBooking;
import ztt.fixedassetmanagement. service.ActivityVenueService;

import java.util. List;
import java. util.Map;

@Tag(name = "活动场地管理")
@RestController
@RequestMapping("/api/activity-venue")
@RequiredArgsConstructor
public class ActivityVenueController {

    private final ActivityVenueService activityVenueService;

    // ==================== 校外活动 ====================

    @Operation(summary = "获取校外活动列表")
    @GetMapping("/external")
    public Result<PageResult<ExternalEventBooking>> getExternalEventList(@RequestParam Map<String, Object> params) {
        return Result.success(activityVenueService. getExternalEventList(params));
    }

    @Operation(summary = "提交校外活动申请")
    @PostMapping("/external")
    public Result<Void> submitExternalEvent(@RequestBody ExternalEventBooking event) {
        activityVenueService.submitExternalEvent(event);
        return Result.success();
    }

    @Operation(summary = "校外活动-资质审核")
    @PostMapping("/external/{id}/qualification-review")
    public Result<Void> reviewExternalQualification(@PathVariable String id, @RequestBody Map<String, Object> data) {
        activityVenueService.reviewExternalQualification(id, data);
        return Result.success();
    }

    @Operation(summary = "校外活动-场地审核")
    @PostMapping("/external/{id}/venue-review")
    public Result<Void> reviewExternalVenue(@PathVariable String id, @RequestBody Map<String, Object> data) {
        activityVenueService.reviewExternalVenue(id, data);
        return Result.success();
    }

    @Operation(summary = "校外活动-确认缴费")
    @PostMapping("/external/{id}/confirm-payment")
    public Result<Void> confirmExternalPayment(@PathVariable String id) {
        activityVenueService.confirmExternalPayment(id);
        return Result.success();
    }

    @Operation(summary = "校外活动-签订协议")
    @PostMapping("/external/{id}/sign-contract")
    public Result<Void> signExternalContract(@PathVariable String id) {
        activityVenueService.signExternalContract(id);
        return Result.success();
    }

    // ==================== 校内活动 ====================

    @Operation(summary = "获取校内活动列表")
    @GetMapping("/internal")
    public Result<PageResult<InternalEventBooking>> getInternalEventList(@RequestParam Map<String, Object> params) {
        return Result. success(activityVenueService.getInternalEventList(params));
    }

    @Operation(summary = "提交校内活动申请")
    @PostMapping("/internal")
    public Result<Void> submitInternalEvent(@RequestBody InternalEventBooking event) {
        activityVenueService.submitInternalEvent(event);
        return Result.success();
    }

    @Operation(summary = "校内活动-审核")
    @PostMapping("/internal/{id}/review")
    public Result<Void> reviewInternalEvent(@PathVariable String id, @RequestBody Map<String, Object> data) {
        activityVenueService.reviewInternalEvent(id, data);
        return Result.success();
    }

    @Operation(summary = "校内活动-确认场地")
    @PostMapping("/internal/{id}/confirm")
    public Result<Void> confirmInternalVenue(@PathVariable String id) {
        activityVenueService.confirmInternalVenue(id);
        return Result.success();
    }

    // ==================== 场地查询 ====================

    @Operation(summary = "获取空闲场地")
    @GetMapping("/idle-venues")
    public Result<List<Map<String, Object>>> getIdleVenues(@RequestParam Map<String, Object> params) {
        return Result.success(activityVenueService.getIdleVenues(params));
    }

    // ==================== 安保管理 ====================

    // 内存存储安保任务状态（模拟数据库）
    private static final java.util.Map<String, String> taskStatusMap = new java.util.concurrent.ConcurrentHashMap<>();
    static {
        taskStatusMap.put("ST-001", "pending");
        taskStatusMap.put("ST-002", "ongoing");
        taskStatusMap.put("ST-003", "completed");
    }

    @Operation(summary = "获取安保任务列表")
    @GetMapping("/security/tasks")
    public Result<PageResult<Map<String, Object>>> getSecurityTasks(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> tasks = new java.util.ArrayList<>();

        Map<String, Object> task1 = new java.util.HashMap<>();
        task1.put("taskId", "ST-001");
        task1.put("activityName", "元旦晚会");
        task1.put("venue", "体育馆");
        task1.put("activityDate", "2025-01-01");
        task1.put("activityTime", "19:00-22:00");
        task1.put("expectedPeople", 2500);
        task1.put("securityStaff", 12);
        task1.put("leader", "张三");
        task1.put("status", taskStatusMap.getOrDefault("ST-001", "pending"));
        task1.put("assignedStaff", List.of("张三", "李四", "王五"));
        task1.put("createdAt", "2024-12-01");
        tasks.add(task1);

        Map<String, Object> task2 = new java.util.HashMap<>();
        task2.put("taskId", "ST-002");
        task2.put("activityName", "迎新晚会");
        task2.put("venue", "大礼堂");
        task2.put("activityDate", "2024-12-20");
        task2.put("activityTime", "18:30-21:30");
        task2.put("expectedPeople", 1500);
        task2.put("securityStaff", 8);
        task2.put("leader", "李四");
        task2.put("status", taskStatusMap.getOrDefault("ST-002", "ongoing"));
        task2.put("assignedStaff", List.of("李四", "赵六", "钱七"));
        task2.put("createdAt", "2024-12-01");
        tasks.add(task2);

        Map<String, Object> task3 = new java.util.HashMap<>();
        task3.put("taskId", "ST-003");
        task3.put("activityName", "校庆晚会");
        task3.put("venue", "体育馆");
        task3.put("activityDate", "2024-10-01");
        task3.put("activityTime", "18:00-22:00");
        task3.put("expectedPeople", 3000);
        task3.put("securityStaff", 15);
        task3.put("leader", "王五");
        task3.put("status", taskStatusMap.getOrDefault("ST-003", "completed"));
        task3.put("assignedStaff", List.of("王五", "张三", "赵六", "钱七"));
        task3.put("createdAt", "2024-09-15");
        tasks.add(task3);

        return Result.success(PageResult.of(tasks, (long) tasks.size(), 1, 10));
    }

    @Operation(summary = "创建安保任务")
    @PostMapping("/security/tasks")
    public Result<Void> createSecurityTask(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @Operation(summary = "更新安保任务")
    @PutMapping("/security/tasks/{id}")
    public Result<Void> updateSecurityTask(@PathVariable String id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @Operation(summary = "分配安保人员")
    @PostMapping("/security/tasks/{id}/assign")
    public Result<Void> assignSecurityStaff(@PathVariable String id, @RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @Operation(summary = "开始执行任务")
    @PostMapping("/security/tasks/{id}/start")
    public Result<Void> startSecurityTask(@PathVariable String id) {
        // 更新任务状态为进行中
        taskStatusMap.put(id, "ongoing");
        return Result.success();
    }

    @Operation(summary = "完成任务")
    @PostMapping("/security/tasks/{id}/complete")
    public Result<Void> completeSecurityTask(@PathVariable String id, @RequestBody Map<String, Object> data) {
        // 更新任务状态为已完成
        taskStatusMap.put(id, "completed");
        return Result.success();
    }

    @Operation(summary = "获取巡逻记录")
    @GetMapping("/security/patrols")
    public Result<PageResult<Map<String, Object>>> getPatrolRecords(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> patrols = new java.util.ArrayList<>();

        Map<String, Object> patrol1 = new java.util.HashMap<>();
        patrol1.put("patrolId", "PT-2024-001");
        patrol1.put("patrolArea", "教学楼区域");
        patrol1.put("patrolTime", "2024-12-10 08:00-10:00");
        patrol1.put("patroller", "安保员A");
        patrol1.put("situation", "一切正常，无异常情况");
        patrol1.put("issues", "");
        patrol1.put("createdAt", "2024-12-10");
        patrols.add(patrol1);

        Map<String, Object> patrol2 = new java.util.HashMap<>();
        patrol2.put("patrolId", "PT-2024-002");
        patrol2.put("patrolArea", "宿舍楼区域");
        patrol2.put("patrolTime", "2024-12-10 14:00-16:00");
        patrol2.put("patroller", "安保员B");
        patrol2.put("situation", "发现3号楼门禁故障");
        patrol2.put("issues", "门禁故障待维修");
        patrol2.put("createdAt", "2024-12-10");
        patrols.add(patrol2);

        Map<String, Object> patrol3 = new java.util.HashMap<>();
        patrol3.put("patrolId", "PT-2024-003");
        patrol3.put("patrolArea", "图书馆区域");
        patrol3.put("patrolTime", "2024-12-10 20:00-22:00");
        patrol3.put("patroller", "安保员C");
        patrol3.put("situation", "正常巡逻完成");
        patrol3.put("issues", "");
        patrol3.put("createdAt", "2024-12-10");
        patrols.add(patrol3);

        return Result.success(PageResult.of(patrols, (long) patrols.size(), 1, 10));
    }

    @Operation(summary = "添加巡逻记录")
    @PostMapping("/security/patrols")
    public Result<Void> createPatrolRecord(@RequestBody Map<String, Object> data) {
        return Result.success();
    }

    @Operation(summary = "获取排班表")
    @GetMapping("/security/schedule")
    public Result<List<Map<String, Object>>> getStaffSchedule(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> schedule = new java.util.ArrayList<>();

        Map<String, Object> s1 = new java.util.HashMap<>();
        s1.put("name", "张三");
        s1.put("monday", "白班");
        s1.put("tuesday", "白班");
        s1.put("wednesday", "休息");
        s1.put("thursday", "夜班");
        s1.put("friday", "夜班");
        s1.put("saturday", "白班");
        s1.put("sunday", "休息");
        schedule.add(s1);

        Map<String, Object> s2 = new java.util.HashMap<>();
        s2.put("name", "李四");
        s2.put("monday", "夜班");
        s2.put("tuesday", "休息");
        s2.put("wednesday", "白班");
        s2.put("thursday", "白班");
        s2.put("friday", "休息");
        s2.put("saturday", "夜班");
        s2.put("sunday", "夜班");
        schedule.add(s2);

        Map<String, Object> s3 = new java.util.HashMap<>();
        s3.put("name", "王五");
        s3.put("monday", "休息");
        s3.put("tuesday", "夜班");
        s3.put("wednesday", "夜班");
        s3.put("thursday", "休息");
        s3.put("friday", "白班");
        s3.put("saturday", "白班");
        s3.put("sunday", "白班");
        schedule.add(s3);

        Map<String, Object> s4 = new java.util.HashMap<>();
        s4.put("name", "赵六");
        s4.put("monday", "白班");
        s4.put("tuesday", "白班");
        s4.put("wednesday", "白班");
        s4.put("thursday", "白班");
        s4.put("friday", "白班");
        s4.put("saturday", "休息");
        s4.put("sunday", "休息");
        schedule.add(s4);

        Map<String, Object> s5 = new java.util.HashMap<>();
        s5.put("name", "钱七");
        s5.put("monday", "夜班");
        s5.put("tuesday", "夜班");
        s5.put("wednesday", "休息");
        s5.put("thursday", "休息");
        s5.put("friday", "夜班");
        s5.put("saturday", "夜班");
        s5.put("sunday", "白班");
        schedule.add(s5);

        return Result.success(schedule);
    }

    @Operation(summary = "更新排班表")
    @PutMapping("/security/schedule")
    public Result<Void> updateStaffSchedule(@RequestBody List<Map<String, Object>> data) {
        return Result.success();
    }
}