-- ========================================
-- 办公用房分配表修复脚本
-- ========================================

-- 删除外键约束允许room_id为空（申请阶段未分配房间）
ALTER TABLE `faculty_office_alloc` DROP FOREIGN KEY `fk_fo_room`;

-- 修改room_id为可空
ALTER TABLE `faculty_office_alloc` 
MODIFY COLUMN `room_id` varchar(20) DEFAULT NULL COMMENT '房间ID（审批后分配）';

-- status字段已存在，无需添加
-- 申请人姓名、部门、职称等信息通过user_id关联sys_user表获取
