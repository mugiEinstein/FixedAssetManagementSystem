-- 创建住宿申请表
DROP TABLE IF EXISTS `housing_application`;
CREATE TABLE `housing_application` (
  `application_id` varchar(50) NOT NULL COMMENT '申请ID',
  `student_id` varchar(50) DEFAULT NULL COMMENT '学生ID',
  `student_no` varchar(50) DEFAULT NULL COMMENT '学号',
  `student_name` varchar(50) DEFAULT NULL COMMENT '学生姓名',
  `department` varchar(100) DEFAULT NULL COMMENT '院系',
  `grade` varchar(20) DEFAULT NULL COMMENT '年级',
  `application_type` varchar(20) NOT NULL COMMENT '申请类型: transfer(调换)/leave(外宿)/checkout(退宿)',
  `current_dorm` varchar(100) DEFAULT NULL COMMENT '当前宿舍',
  `reason` varchar(200) DEFAULT NULL COMMENT '申请原因',
  `reason_detail` varchar(500) DEFAULT NULL COMMENT '原因详情',
  `preferred_dorm` varchar(100) DEFAULT NULL COMMENT '期望宿舍(调换时)',
  `leave_start_date` date DEFAULT NULL COMMENT '外宿开始日期',
  `leave_end_date` date DEFAULT NULL COMMENT '外宿结束日期',
  `exit_date` date DEFAULT NULL COMMENT '退宿日期',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态: pending/approved/rejected',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `reviewer` varchar(50) DEFAULT NULL COMMENT '审核人',
  `review_reason` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`application_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_status` (`status`),
  KEY `idx_apply_time` (`apply_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='住宿申请表';

-- 插入测试数据
INSERT INTO `housing_application` VALUES 
('HA-2024-001', 'S2021001', '2024001001', '张三', '计算机学院', '2024级', 'transfer', '1斋-101', '与室友作息不合', '室友晚睡影响休息', '2斋-203', NULL, NULL, NULL, 'pending', '2024-12-01 10:30:00', NULL, NULL, NULL, NOW(), NOW(), 0),
('HA-2024-002', 'S2021002', '2024002002', '李四', '材料学院', '2023级', 'leave', '2斋-203', '实习', '在校外企业实习需外宿', NULL, '2024-12-01', '2025-03-01', NULL, 'pending', '2024-12-01 14:20:00', NULL, NULL, NULL, NOW(), NOW(), 0),
('HA-2024-003', 'S2021003', '2024003003', '王五', '物理学院', '2022级', 'transfer', '3斋-105', '毕业离校', '准备考研需要安静环境', '1斋-102', NULL, NULL, NULL, 'pending', '2024-12-01 16:00:00', NULL, NULL, NULL, NOW(), NOW(), 0),
('HA-2024-004', 'S2021004', '2024004004', '赵六', '计算机学院', '2024级', 'transfer', '1斋-102', '身体健康原因', '需要住在低层方便就医', '1斋-101', NULL, NULL, NULL, 'approved', '2024-11-28 09:00:00', '2024-11-28 15:00:00', '管理员', '已通过', NOW(), NOW(), 0),
('HA-2024-005', 'S2021005', '2024005005', '钱七', '材料学院', '2023级', 'checkout', '2斋-301', '回家', '因家庭原因需要退宿回家', NULL, NULL, NULL, '2024-12-15', 'pending', '2024-12-02 08:30:00', NULL, NULL, NULL, NOW(), NOW(), 0);
