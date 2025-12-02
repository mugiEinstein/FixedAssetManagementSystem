-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: rm-2ze5g00fo0zy6956emo.mysql.rds.aliyuncs.com    Database: fixedassets_manage
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'c30a24de-ba39-11f0-aae3-7c8c09be377a:1-463739';

--
-- Table structure for table `asset_fee_record`
--

DROP TABLE IF EXISTS `asset_fee_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_fee_record` (
  `fee_id` varchar(50) NOT NULL COMMENT '费用ID',
  `fee_type` varchar(30) NOT NULL COMMENT '费用类型(deposit/rent/penalty/repair/excess_area/venue)',
  `business_type` varchar(30) DEFAULT NULL COMMENT '业务类型(equipment_borrow/office_room/activity_venue/staff_housing)',
  `business_id` varchar(50) DEFAULT NULL COMMENT '关联业务ID',
  `payer_id` varchar(50) DEFAULT NULL COMMENT '付款人ID(关联其他系统用户)',
  `payer_name` varchar(50) DEFAULT NULL COMMENT '付款人名称',
  `payer_type` varchar(20) DEFAULT NULL COMMENT '付款人类型(external/internal/staff/student)',
  `amount` decimal(12,2) NOT NULL COMMENT '金额',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态(pending/paid/refunded)',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `refund_amount` decimal(12,2) DEFAULT '0.00' COMMENT '退款金额',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `sync_status` varchar(20) DEFAULT 'pending' COMMENT '同步到财务系统状态',
  `finance_record_id` varchar(50) DEFAULT NULL COMMENT '财务系统记录ID',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`fee_id`),
  KEY `idx_business` (`business_type`,`business_id`),
  KEY `idx_payer` (`payer_id`),
  KEY `idx_sync` (`sync_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='固定资产费用明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asset_fee_record`
--

LOCK TABLES `asset_fee_record` WRITE;
/*!40000 ALTER TABLE `asset_fee_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `asset_fee_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asset_inventory`
--

DROP TABLE IF EXISTS `asset_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_inventory` (
  `inventory_id` varchar(50) NOT NULL COMMENT '盘点ID',
  `plan_id` varchar(50) DEFAULT NULL COMMENT '盘点计划ID',
  `asset_id` varchar(50) NOT NULL COMMENT '资产ID',
  `asset_type` varchar(20) DEFAULT NULL COMMENT '资产类型',
  `asset_name` varchar(100) DEFAULT NULL COMMENT '资产名称',
  `inventory_date` date NOT NULL COMMENT '盘点日期',
  `physical_status` varchar(50) DEFAULT NULL COMMENT '实物状态',
  `difference_reason` varchar(200) DEFAULT NULL COMMENT '差异原因',
  `assessed_value` decimal(12,2) DEFAULT NULL COMMENT '评估价值',
  `depreciation` decimal(12,2) DEFAULT NULL COMMENT '累计折旧',
  `net_value` decimal(12,2) DEFAULT NULL COMMENT '资产净值',
  `value_adjustment` decimal(12,2) DEFAULT NULL COMMENT '价值调整',
  `inventory_status` varchar(20) DEFAULT '待盘点' COMMENT '状态',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`inventory_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产盘点表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asset_inventory`
--

LOCK TABLES `asset_inventory` WRITE;
/*!40000 ALTER TABLE `asset_inventory` DISABLE KEYS */;
INSERT INTO `asset_inventory` VALUES ('AI-2025-002',NULL,'EQ-WL-2023-002',NULL,NULL,'2025-11-01','盘亏',NULL,6800.00,1700.00,5100.00,0.00,'已完成','2025-11-26 16:27:31','2025-11-26 16:27:31',0),('AI-2025-003',NULL,'B-A-01-03',NULL,NULL,'2025-11-02','完好',NULL,5440000.00,1360000.00,4080000.00,0.00,'已完成','2025-11-26 16:27:31','2025-11-26 16:27:31',0),('AI-2025-004',NULL,'EQ-HX-2023-001',NULL,NULL,'2025-11-03','完好',NULL,14400.00,3600.00,10800.00,0.00,'已完成','2025-11-26 16:27:31','2025-11-26 16:27:31',0),('AI-2025-005',NULL,'R-B-C-01-04-0502',NULL,NULL,'2025-11-04','完好',NULL,200000.00,50000.00,150000.00,0.00,'已完成','2025-11-26 16:27:31','2025-11-26 16:27:31',0),('AI-2025-006',NULL,'EQ-SW-2023-001',NULL,NULL,'2025-11-05','完好',NULL,7360.00,1840.00,5520.00,0.00,'已完成','2025-11-26 16:27:31','2025-11-26 16:27:31',0),('AI-2025-007',NULL,'B-D-01-02',NULL,NULL,'2025-11-06','完好',NULL,3840240.00,960060.00,2880180.00,0.00,'已完成','2025-11-26 16:27:31','2025-11-26 16:27:31',0),('AI-2025-008',NULL,'EQ-TY-2024-001',NULL,NULL,'2025-11-07','完好',NULL,3600.00,900.00,2700.00,0.00,'已完成','2025-11-26 16:27:31','2025-11-26 16:27:31',0),('AI-2025-009',NULL,'R-B-A-01-03-0201',NULL,NULL,'2025-11-08','完好',NULL,72000.00,18000.00,54000.00,0.00,'已完成','2025-11-26 16:27:31','2025-11-26 16:27:31',0),('AI-2025-010',NULL,'EQ-JM-2024-002',NULL,NULL,'2025-11-09','完好',NULL,9600.00,2400.00,7200.00,0.00,'已完成','2025-11-26 16:27:31','2025-11-26 16:27:31',0),('AI-2025-011',NULL,'B-C-01-04',NULL,NULL,'2025-11-10','完好',NULL,7360000.00,1840000.00,5520000.00,0.00,'已完成','2025-11-26 16:27:31','2025-11-26 16:27:31',0),('IV202512020148110001',NULL,'EQ-WL-2023-002',NULL,NULL,'2025-12-02','盘亏',NULL,6800.00,1700.00,5100.00,0.00,'pending','2025-11-26 16:27:31','2025-11-26 16:27:31',0);
/*!40000 ALTER TABLE `asset_inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asset_operation_log`
--

DROP TABLE IF EXISTS `asset_operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_operation_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `asset_id` varchar(50) NOT NULL COMMENT '资产ID',
  `operator_id` varchar(50) DEFAULT 'SYSTEM' COMMENT '操作人ID',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型(入库/借用/归还/报废)',
  `pre_status` varchar(20) DEFAULT NULL COMMENT '操作前状态',
  `current_status` varchar(20) DEFAULT NULL COMMENT '操作后状态',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_asset` (`asset_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产全生命周期日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asset_operation_log`
--

LOCK TABLES `asset_operation_log` WRITE;
/*!40000 ALTER TABLE `asset_operation_log` DISABLE KEYS */;
INSERT INTO `asset_operation_log` VALUES (1,'EQ-WL-2023-002','U-GZ-2019-001','入库',NULL,'IDLE','物理实验器材激光测距仪入库','2025-11-26 16:40:39'),(2,'B-A-01-03','U-GZ-2019-002','入库',NULL,'正常','第三教学楼入库建档','2025-11-26 16:40:39'),(3,'EQ-HX-2023-001','U-SY-2018-003','借用','IDLE','借用中','某科研研究院借用气相色谱仪','2025-11-26 16:40:39'),(4,'R-B-C-01-04-0502','U-JW-2020-004','分配','IDLE','在住','分配给2025级新生住宿','2025-11-26 16:40:39'),(5,'EQ-SW-2023-001','U-SY-2020-005','归还','借用中','IDLE','校内用户归还生物显微镜','2025-11-26 16:40:39'),(6,'B-D-01-02','U-GZ-2021-006','维修','正常','维修中','第二实验楼水电维修','2025-11-26 16:40:39'),(7,'EQ-TY-2024-001','U-CW-2017-007','借用','IDLE','借用中','经管学院借用多功能打印机','2025-11-26 16:40:39'),(8,'R-B-A-01-03-0201','U-JW-2022-008','预订','IDLE','已预订','信息学院预订多媒体教室','2025-11-26 16:40:39'),(9,'EQ-JM-2024-002','U-HZ-2021-009','盘点','IDLE','完好','2025年11月资产盘点','2025-11-26 16:40:39'),(10,'V-XZ-2023-002','U-GZ-2021-010','借用','正常','借用中','固定资产管理处公务出差借用车辆','2025-11-26 16:40:39');
/*!40000 ALTER TABLE `asset_operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building_archive`
--

DROP TABLE IF EXISTS `building_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `building_archive` (
  `building_id` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '建筑编号',
  `building_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '建筑名称',
  `building_type` varchar(50) NOT NULL COMMENT '房屋类型',
  `area` decimal(10,2) NOT NULL COMMENT '建筑面积(㎡)',
  `build_year` int NOT NULL COMMENT '建成年份',
  `depreciation_years` int NOT NULL COMMENT '折旧年限',
  `status` varchar(20) DEFAULT '正常' COMMENT '状态',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`building_id`),
  KEY `idx_building_name` (`building_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='建筑档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_archive`
--

LOCK TABLES `building_archive` WRITE;
/*!40000 ALTER TABLE `building_archive` DISABLE KEYS */;
INSERT INTO `building_archive` VALUES ('afsdfa','逸夫楼','教学楼',6800.00,2012,50,'正常','2025-11-25 23:54:41',NULL,1),('B-A-01-03','逸夫楼','教学楼',6800.00,2012,50,'正常','2025-11-25 23:54:41','2025-11-25 23:54:41',0),('B-A-01-10','逸夫楼','教学',6800.00,2012,50,'正常','2025-11-25 23:54:41','2025-12-02 01:31:06',1),('B-B-01-02','办公楼','办公楼',3500.50,2016,50,'正常','2025-11-25 23:54:41','2025-11-25 23:54:41',0),('B-C-01-04','七斋','宿舍楼',6200.00,2019,50,'正常','2025-11-25 23:54:41','2025-11-25 23:54:41',0),('B-C-01-05','五斋','宿舍楼',9800.00,2021,50,'正常','2025-11-25 23:54:41','2025-11-25 23:54:41',0),('B-D-01-02','主楼','实验楼',4800.30,2015,50,'维修中','2025-11-25 23:54:41','2025-11-25 23:54:41',0),('B-E-01-01','1号楼','生活用房',2800.00,2008,70,'正常','2025-11-25 23:54:41','2025-11-25 23:54:41',0),('B-F-01-01','体育馆','其他',8500.00,2010,30,'正常','2025-11-25 23:54:41','2025-11-25 23:54:41',0),('B-F-01-03','教职工礼堂','其他',5200.00,2017,50,'正常','2025-11-25 23:54:41','2025-11-25 23:54:41',0),('B-F-01-04','鸿博园','其他',7500.80,2018,50,'正常','2025-11-25 23:54:41','2025-11-25 23:54:41',0),('B-F-01-05','万秀园','其他',7500.80,2018,50,'正常','2025-11-25 23:54:41','2025-11-25 23:54:41',0),('B-H-01-02','校医院','医疗建筑',2200.00,2020,50,'正常','2025-11-25 23:54:41','2025-11-25 23:54:41',0);
/*!40000 ALTER TABLE `building_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classroom_borrow`
--

DROP TABLE IF EXISTS `classroom_borrow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classroom_borrow` (
  `borrow_id` varchar(50) NOT NULL COMMENT '借用ID',
  `applicant_dept` varchar(50) NOT NULL COMMENT '申请部门',
  `applicant_name` varchar(50) DEFAULT NULL COMMENT '申请人姓名',
  `classroom_id` varchar(20) NOT NULL COMMENT '教室ID',
  `borrow_purpose` varchar(200) DEFAULT NULL COMMENT '借用用途',
  `borrow_time` varchar(100) DEFAULT NULL COMMENT '借用时段',
  `borrow_date` date DEFAULT NULL COMMENT '借用日期',
  `expected_attendees` int DEFAULT NULL COMMENT '预计人数',
  `status` varchar(20) DEFAULT '申请中' COMMENT '状态',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`borrow_id`),
  KEY `fk_cb_room` (`classroom_id`),
  CONSTRAINT `fk_cb_room` FOREIGN KEY (`classroom_id`) REFERENCES `room_resource` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教室借用表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classroom_borrow`
--

LOCK TABLES `classroom_borrow` WRITE;
/*!40000 ALTER TABLE `classroom_borrow` DISABLE KEYS */;
INSERT INTO `classroom_borrow` VALUES ('CB-2025-002','智能科学与技术学院',NULL,'R-B-A-01-03-0201','人工智能课程实训','2025-12-02 09:00-12:00',NULL,NULL,'已批准','2025-11-26 15:38:12','2025-11-26 15:38:12',0),('CB-2025-003','经管学院',NULL,'R-B-A-01-04-0306','企业管理案例分析','2025-12-03 14:00-17:00',NULL,NULL,'申请中','2025-11-26 15:38:12','2025-11-26 15:38:12',0),('CB-2025-004','外国语学院',NULL,'R-B-A-01-03-0201','英语演讲比赛','2025-12-05 10:00-12:00',NULL,NULL,'已通过','2025-11-26 15:38:12','2025-11-26 15:38:12',0),('CB-2025-005','数理学院',NULL,'R-B-A-01-04-0306','高等数学研讨会','2025-12-06 15:00-17:30',NULL,NULL,'申请中','2025-11-26 15:38:12','2025-11-26 15:38:12',0),('CB-2025-006','文发学院',NULL,'R-B-A-01-03-0201','经典诵读活动','2025-12-08 09:30-11:30',NULL,NULL,'已通过','2025-11-26 15:38:12','2025-11-26 15:38:12',0),('CB-2025-007','自动化学院',NULL,'R-B-A-01-04-0306','智能感知技术讲座','2025-12-09 14:30-16:30',NULL,NULL,'申请中','2025-11-26 15:38:12','2025-11-26 15:38:12',0),('CB-2025-008','马克思主义学院',NULL,'R-B-A-01-03-0201','思政课实践分享','2025-12-10 10:00-12:00',NULL,NULL,'已通过','2025-11-26 15:38:12','2025-11-26 15:38:12',0),('CB-2025-009','未来城市学院',NULL,'R-B-A-01-04-0306','建筑环境与能源应用研讨会','2025-12-12 15:00-17:00',NULL,NULL,'申请中','2025-11-26 15:38:12','2025-11-26 15:38:12',0),('CB-2025-010','机械工程学院',NULL,'R-B-A-01-03-0201','机器人工程讲座','2025-12-15 09:00-16:00',NULL,NULL,'已通过','2025-11-26 15:38:12','2025-11-26 15:38:12',0),('CB-2025-011','材料科学与功能学院',NULL,'R-B-A-01-04-0306','材料科学讲座','2025-12-16 14:00-16:00',NULL,NULL,'申请中','2025-11-26 15:38:12','2025-11-26 15:38:12',0),('JY202511301638230001','超级管理员',NULL,'R-B-A-01-03-0201','课程教学','2025-12-01 14:00-15:40',NULL,NULL,'rejected','2025-11-30 16:38:24','2025-11-30 16:38:24',0),('JY202511301649510002','超级管理员',NULL,'R-B-01-101','答辩','2025-12-01 10:00-11:40',NULL,NULL,'rejected','2025-11-30 16:49:51','2025-11-30 16:49:51',0),('JY202512020201300003','超级管理员',NULL,'R-B-01-101','答辩','2025-12-01 10:00-11:40','2025-12-02',NULL,'rejected','2025-11-30 16:49:51','2025-11-30 16:49:51',0),('JY202512020231340001','大雄','大雄','R-B-A-01-03-0201','班级活动','2025-12-10 10:00-11:40','2025-12-10',20,'approved','2025-12-02 02:31:35','2025-12-02 02:31:35',0);
/*!40000 ALTER TABLE `classroom_borrow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_schedule`
--

DROP TABLE IF EXISTS `course_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_schedule` (
  `schedule_id` varchar(50) NOT NULL COMMENT '排课记录ID（格式：CS-年份-流水号）',
  `course_name` varchar(100) NOT NULL COMMENT '课程名称',
  `class_time` varchar(50) NOT NULL COMMENT '上课时间（如：周一3-4节、周二1-2节）',
  `classroom_id` varchar(50) NOT NULL COMMENT '占用教室（房间编码，如：R-B-A-01-01-0101）',
  `enrolled_count` int NOT NULL DEFAULT '0' COMMENT '课程人数（实际上课人数）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`schedule_id`),
  KEY `idx_classroom` (`classroom_id`),
  KEY `idx_class_time` (`class_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课表数据表（简化版，接收排课系统数据）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_schedule`
--

LOCK TABLES `course_schedule` WRITE;
/*!40000 ALTER TABLE `course_schedule` DISABLE KEYS */;
INSERT INTO `course_schedule` VALUES ('CS-2025-001','数据结构与算法','周一3-4节','R-B-A-01-01-0101',85,'2025-12-01 20:29:19'),('CS-2025-002','操作系统原理','周一5-6节','R-B-A-01-01-0201',90,'2025-12-01 20:29:19'),('CS-2025-003','线性代数','周一1-2节','R-B-A-01-02-0101',120,'2025-12-01 20:29:19'),('CS-2025-004','计算机网络','周二1-2节','R-B-A-01-01-0301',72,'2025-12-01 20:29:19'),('CS-2025-005','高等数学A','周二1-2节','R-B-A-01-02-0201',121,'2025-12-01 20:29:19'),('CS-2025-006','大数据技术','周二7-8节','R-B-A-01-01-0201',39,'2025-12-01 20:29:19'),('CS-2025-007','人工智能导论','周三5-6节','R-B-A-01-02-0301',45,'2025-12-01 20:29:19'),('CS-2025-008','云计算技术','周三3-4节','R-B-A-01-01-0101',43,'2025-12-01 20:29:19'),('CS-2025-009','数据库系统','周四3-4节','R-B-A-01-02-0101',69,'2025-12-01 20:29:19'),('CS-2025-010','艺术鉴赏','周四1-2节','R-B-F-01-01-0101',35,'2025-12-01 20:29:19'),('CS-2025-011','音乐欣赏','周四3-4节','R-B-F-01-01-0201',29,'2025-12-01 20:29:19'),('CS-2025-012','区块链技术','周五9-10节','R-B-D-01-01-0101',9,'2025-12-01 20:29:19'),('CS-2025-013','文学赏析','周五1-2节','R-B-A-01-01-0201',31,'2025-12-01 20:29:19'),('CS-2025-014','体育与健康','周五1-2节','R-B-F-01-01-0301',46,'2025-12-01 20:29:19');
/*!40000 ALTER TABLE `course_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipment_archive`
--

DROP TABLE IF EXISTS `equipment_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment_archive` (
  `equipment_id` varchar(50) NOT NULL COMMENT '器材编号',
  `equipment_name` varchar(100) NOT NULL COMMENT '器材名称',
  `model` varchar(100) DEFAULT NULL COMMENT '规格型号',
  `equipment_type` varchar(50) NOT NULL COMMENT '器材类型',
  `manufacturer` varchar(100) DEFAULT NULL COMMENT '生产厂家',
  `location` varchar(100) DEFAULT NULL COMMENT '存放位置',
  `responsible_person` varchar(50) DEFAULT NULL COMMENT '责任人',
  `original_value` decimal(15,2) NOT NULL COMMENT '原值',
  `net_value` decimal(15,2) DEFAULT NULL COMMENT '净值',
  `purchase_date` date NOT NULL COMMENT '采购日期',
  `depreciation_years` int NOT NULL COMMENT '折旧年限',
  `daily_rent` decimal(10,2) DEFAULT '0.00' COMMENT '每日租金',
  `maintenance_cycle` varchar(20) DEFAULT NULL COMMENT '维保周期',
  `storage_requirements` varchar(200) DEFAULT NULL COMMENT '存储要求',
  `status` varchar(20) DEFAULT 'IDLE' COMMENT '状态',
  `version` int DEFAULT '0' COMMENT '乐观锁',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='器材档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment_archive`
--

LOCK TABLES `equipment_archive` WRITE;
/*!40000 ALTER TABLE `equipment_archive` DISABLE KEYS */;
INSERT INTO `equipment_archive` VALUES ('EQ-HX-2023-001','气相色谱仪',NULL,'化学实验器材',NULL,NULL,NULL,18000.00,NULL,'2023-06-20',10,0.00,'每半年',NULL,'IN_USE',1,'2025-11-26 15:47:51','2025-11-26 15:47:51',0),('EQ-HX-2023-100','气相色谱仪',NULL,'化学实验器材',NULL,NULL,NULL,18000.00,NULL,'2023-06-20',10,0.00,'每半年',NULL,'IN_USE',1,'2025-11-26 15:47:51','2025-12-02 01:36:04',1),('EQ-HX-2023-GLJ BHJ','气相色谱仪',NULL,'化学实验器材',NULL,NULL,NULL,18000.00,NULL,'2023-06-20',10,0.00,'每半年',NULL,'IDLE',1,'2025-11-26 15:47:51',NULL,1),('EQ-HX-2024-002','紫外分光光度计',NULL,'化学实验器材',NULL,NULL,NULL,15000.00,NULL,'2024-07-22',10,0.00,'每季度',NULL,'维修中',0,'2025-11-26 15:47:51','2025-11-26 15:47:51',0),('EQ-JM-2024-002','高精度天平',NULL,'精密仪器',NULL,NULL,NULL,12000.00,NULL,'2024-03-12',10,0.00,'每季度',NULL,'IN_USE',1,'2025-11-26 15:47:51','2025-11-26 15:47:51',0),('EQ-JM-2025-001','质谱仪',NULL,'精密仪器',NULL,NULL,NULL,28000.00,NULL,'2025-01-10',12,0.00,'每季度',NULL,'IN_USE',2,'2025-11-26 15:47:51','2025-11-26 15:47:51',0),('EQ-SW-2023-001','生物显微镜',NULL,'生物实验器材',NULL,NULL,NULL,9200.00,NULL,'2023-08-10',8,0.00,'每季度',NULL,'IN_USE',2,'2025-11-26 15:47:51','2025-11-26 15:47:51',0),('EQ-SW-2024-002','细胞培养箱',NULL,'生物实验器材',NULL,NULL,NULL,13500.00,NULL,'2024-09-30',8,0.00,'每半年',NULL,'IN_USE',1,'2025-11-26 15:47:51','2025-11-26 15:47:51',0),('EQ-TY-2024-001','多功能打印机',NULL,'通用实验设备',NULL,NULL,NULL,4500.00,NULL,'2024-01-25',5,0.00,'每年',NULL,'IN_USE',1,'2025-11-26 15:47:51','2025-11-26 15:47:51',0),('EQ-TY-2024-002','投影仪',NULL,'通用实验设备',NULL,NULL,NULL,5200.00,NULL,'2024-11-15',5,0.00,'每年',NULL,'IN_USE',3,'2025-11-26 15:47:51','2025-11-26 15:47:51',0),('EQ-TY-2025-001','高清投影仪',NULL,'TY',NULL,NULL,NULL,5000.00,NULL,'2025-11-25',5,0.00,'一年',NULL,'IN_USE',1,'2025-11-25 13:38:05','2025-11-25 13:38:05',0),('EQ-WL-2023-002','激光测距仪',NULL,'物理实验器材',NULL,NULL,NULL,8500.00,NULL,'2023-04-15',10,0.00,'每季度',NULL,'IN_USE',1,'2025-11-26 15:47:51','2025-11-26 15:47:51',0),('EQ-WL-2024-003','示波器',NULL,'物理实验器材',NULL,NULL,NULL,7800.00,NULL,'2024-05-18',10,0.00,'每半年',NULL,'IDLE',7,'2025-11-26 15:47:51','2025-11-26 15:47:51',0);
/*!40000 ALTER TABLE `equipment_archive` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `external_event_booking`
--

DROP TABLE IF EXISTS `external_event_booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `external_event_booking` (
  `event_id` varchar(50) NOT NULL COMMENT '活动ID',
  `organizer` varchar(100) NOT NULL COMMENT '主办单位',
  `venue_id` varchar(20) NOT NULL COMMENT '场地ID',
  `event_date` date NOT NULL COMMENT '活动日期',
  `total_fee` decimal(10,2) DEFAULT NULL COMMENT '总费用',
  `status` varchar(20) DEFAULT '申请中' COMMENT '状态',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`event_id`),
  KEY `fk_eb_venue` (`venue_id`),
  CONSTRAINT `fk_eb_venue` FOREIGN KEY (`venue_id`) REFERENCES `building_archive` (`building_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校外活动预订表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `external_event_booking`
--

LOCK TABLES `external_event_booking` WRITE;
/*!40000 ALTER TABLE `external_event_booking` DISABLE KEYS */;
INSERT INTO `external_event_booking` VALUES ('0216e22d02f65be6acab65802b37dbc7','','B-C-01-05','2025-11-27',NULL,'已拒绝','2025-11-27 12:02:04','2025-11-27 12:02:04',0),('EE-2025-002','某教育科技公司','B-F-01-01','2025-03-20',8000.00,'已结束','2025-11-26 16:29:26','2025-11-26 16:29:26',0),('EE-2025-003','某行业协会','B-A-01-03','2025-12-22',5000.00,'已批准','2025-11-26 16:29:26','2025-11-26 16:29:26',0),('EE-2025-004','某医疗器械厂商','B-H-01-02','2025-04-25',6500.00,'已结束','2025-11-26 16:29:26','2025-11-26 16:29:26',0),('EE-2025-005','某软件公司','B-A-01-04','2025-05-28',4500.00,'已结束','2025-11-26 16:29:26','2025-11-26 16:29:26',0),('EE-2025-006','某环保组织','B-F-01-01','2025-01-05',7000.00,'已结束','2025-11-26 16:29:26','2025-11-26 16:29:26',0),('EE-2025-007','某金融机构','B-A-01-03','2026-01-08',5500.00,'已取消','2025-11-26 16:29:26','2025-11-26 16:29:26',0),('EE-2025-008','某汽车厂商','B-F-01-01','2026-01-12',9000.00,'已预订','2025-11-26 16:29:26','2025-11-26 16:29:26',0),('EE-2025-009','某高校联盟','B-A-01-04','2026-01-15',6000.00,'已预订','2025-11-26 16:29:26','2025-11-26 16:29:26',0),('EE-2025-010','某公益基金会','B-H-01-02','2026-01-20',5800.00,'已预订','2025-11-26 16:29:26','2025-11-26 16:29:26',0),('EE-2025-011','某科技创业公司','B-A-01-03','2026-01-22',4800.00,'已预订','2025-11-26 16:29:26','2025-11-26 16:29:26',0);
/*!40000 ALTER TABLE `external_event_booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `faculty_dorm_alloc`
--

DROP TABLE IF EXISTS `faculty_dorm_alloc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `faculty_dorm_alloc` (
  `allocation_id` varchar(50) NOT NULL COMMENT '分配ID',
  `user_id` varchar(50) NOT NULL COMMENT '教职工ID',
  `staff_name` varchar(50) DEFAULT NULL COMMENT '教职工姓名',
  `department` varchar(100) DEFAULT NULL COMMENT '所属部门',
  `dorm_id` varchar(20) NOT NULL COMMENT '宿舍ID',
  `room_type` varchar(50) DEFAULT NULL COMMENT '房型',
  `area` decimal(8,2) DEFAULT NULL COMMENT '面积',
  `rent_amount` decimal(8,2) DEFAULT NULL COMMENT '租金',
  `contract_start` date NOT NULL COMMENT '合同开始',
  `contract_end` date NOT NULL COMMENT '合同结束',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态',
  `qualification_approval` varchar(20) DEFAULT 'pending' COMMENT '资格审核',
  `finance_approval` varchar(20) DEFAULT 'pending' COMMENT '财务审核',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`allocation_id`),
  KEY `fk_fd_room` (`dorm_id`),
  CONSTRAINT `fk_fd_room` FOREIGN KEY (`dorm_id`) REFERENCES `room_resource` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教职工宿舍分配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `faculty_dorm_alloc`
--

LOCK TABLES `faculty_dorm_alloc` WRITE;
/*!40000 ALTER TABLE `faculty_dorm_alloc` DISABLE KEYS */;
INSERT INTO `faculty_dorm_alloc` VALUES ('AL202512020200130002','U-JW-2020-002',NULL,NULL,'R-B-E-01-01-0203',NULL,NULL,2700.00,'2025-01-01','2026-12-31','approved','approved','approved','2025-11-26 15:47:03','2025-12-02 03:21:00',0),('FD-2025-002','U-JW-2020-002',NULL,NULL,'R-B-E-01-01-0203',NULL,NULL,2700.00,'2025-01-01','2026-12-31','approved','approved','approved','2025-11-26 15:47:03','2025-12-02 03:21:00',0),('FD-2025-003','U-SY-2018-004',NULL,NULL,'R-B-E-01-01-0305',NULL,NULL,3000.00,'2025-02-10','2026-12-31','approved','approved','approved','2025-11-26 15:47:03','2025-12-02 03:21:00',0),('FD-2025-004','U-RS-2021-006',NULL,NULL,'R-B-E-01-01-0402',NULL,NULL,2400.00,'2025-03-01','2026-12-31','approved','approved','approved','2025-11-26 15:47:03','2025-12-02 03:21:00',0),('FD-2025-005','U-CW-2017-008',NULL,NULL,'R-B-E-01-01-0403',NULL,NULL,2700.00,'2025-01-15','2026-12-31','approved','approved','approved','2025-11-26 15:47:03','2025-12-02 03:21:00',0),('FD-2025-006','U-HZ-2019-010',NULL,NULL,'R-B-E-01-01-0501',NULL,NULL,3300.00,'2025-04-01','2026-12-31','approved','approved','approved','2025-11-26 15:47:03','2025-12-02 03:21:00',0),('FD-2025-007','U-JW-2022-012',NULL,NULL,'R-B-E-01-01-0502',NULL,NULL,2700.00,'2025-05-10','2026-12-31','approved','approved','approved','2025-11-26 15:47:03','2025-12-02 03:21:00',0),('FD-2025-008','U-SY-2020-014',NULL,NULL,'R-B-E-01-01-0603',NULL,NULL,3000.00,'2025-02-20','2026-12-31','approved','approved','approved','2025-11-26 15:47:03','2025-12-02 03:21:00',0),('FD-2025-009','U-GZ-2021-016',NULL,NULL,'R-B-E-01-01-0604',NULL,NULL,2400.00,'2025-03-15','2026-12-31','approved','approved','approved','2025-11-26 15:47:03','2025-12-02 03:21:00',0),('FD-2025-010','U-RS-2018-018',NULL,NULL,'R-B-E-01-01-0701',NULL,NULL,3300.00,'2025-01-20','2026-12-31','approved','approved','approved','2025-11-26 15:47:03','2025-12-02 03:21:00',0),('FD-2025-011','U-CW-2019-020',NULL,NULL,'R-B-E-01-01-0702',NULL,NULL,2700.00,'2025-04-25','2026-12-31','approved','approved','approved','2025-11-26 15:47:03','2025-12-02 03:21:00',0);
/*!40000 ALTER TABLE `faculty_dorm_alloc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `faculty_office_alloc`
--

DROP TABLE IF EXISTS `faculty_office_alloc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `faculty_office_alloc` (
  `allocation_id` varchar(50) NOT NULL COMMENT '分配ID',
  `user_id` varchar(50) NOT NULL COMMENT '教职工ID',
  `room_id` varchar(20) NOT NULL COMMENT '房间ID',
  `standard_area` decimal(6,2) DEFAULT NULL COMMENT '基准面积(㎡)',
  `actual_area` decimal(6,2) DEFAULT NULL COMMENT '实际面积(㎡)',
  `over_area_fee` decimal(8,2) DEFAULT '0.00' COMMENT '超面积费用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`allocation_id`),
  KEY `fk_fo_room` (`room_id`),
  CONSTRAINT `fk_fo_room` FOREIGN KEY (`room_id`) REFERENCES `room_resource` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教职工办公分配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `faculty_office_alloc`
--

LOCK TABLES `faculty_office_alloc` WRITE;
/*!40000 ALTER TABLE `faculty_office_alloc` DISABLE KEYS */;
INSERT INTO `faculty_office_alloc` VALUES ('AL202512020042150001','U-GZ-2019-003','R-B-B-01-02-0405',15.00,15.00,0.00,'2025-11-26 14:08:03','2025-11-26 14:08:03',0),('FO-2025-002','U-GZ-2019-003','R-B-B-01-02-0405',15.00,15.00,0.00,'2025-11-26 14:08:03','2025-11-26 14:08:03',0),('FO-2025-003','U-JW-2020-005','R-B-B-01-02-0406',12.00,12.00,0.00,'2025-11-26 14:08:03','2025-11-26 14:08:03',0),('FO-2025-004','U-SY-2018-007','R-B-D-01-02-0203',18.00,20.00,100.00,'2025-11-26 14:08:03','2025-11-26 14:08:03',0),('FO-2025-005','U-RS-2021-009','R-B-B-01-02-0302',10.00,10.00,0.00,'2025-11-26 14:08:03','2025-11-26 14:08:03',0),('FO-2025-006','U-CW-2017-011','R-B-B-01-02-0303',12.00,14.00,100.00,'2025-11-26 14:08:03','2025-11-26 14:08:03',0),('FO-2025-007','U-HZ-2019-013','R-B-B-01-02-0501',10.00,10.00,0.00,'2025-11-26 14:08:03','2025-11-26 14:08:03',0),('FO-2025-008','U-JW-2022-015','R-B-B-01-02-0502',12.00,12.00,0.00,'2025-11-26 14:08:03','2025-11-26 14:08:03',0),('FO-2025-009','U-SY-2020-017','R-B-D-01-03-0305',18.00,18.00,0.00,'2025-11-26 14:08:03','2025-11-26 14:08:03',0),('FO-2025-010','U-GZ-2021-019','R-B-B-01-02-0601',15.00,16.00,50.00,'2025-11-26 14:08:03','2025-11-26 14:08:03',0),('FO-2025-011','U-RS-2018-021','R-B-B-01-02-0602',10.00,10.00,0.00,'2025-11-26 14:08:03','2025-11-26 14:08:03',0);
/*!40000 ALTER TABLE `faculty_office_alloc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `financial_record`
--

DROP TABLE IF EXISTS `financial_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `financial_record` (
  `id` varchar(50) NOT NULL COMMENT '记录ID',
  `type` varchar(20) DEFAULT NULL COMMENT '类型: INCOME/EXPENSE',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `amount` decimal(10,2) DEFAULT '0.00' COMMENT '金额',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='财务记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financial_record`
--

LOCK TABLES `financial_record` WRITE;
/*!40000 ALTER TABLE `financial_record` DISABLE KEYS */;
INSERT INTO `financial_record` VALUES ('1','INCOME','场地租用费-学生活动',500.00,'2025-11-29 15:03:21');
/*!40000 ALTER TABLE `financial_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hospital_asset`
--

DROP TABLE IF EXISTS `hospital_asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hospital_asset` (
  `asset_id` varchar(20) NOT NULL COMMENT '资产编号',
  `asset_name` varchar(100) NOT NULL COMMENT '资产名称',
  `asset_type` varchar(50) NOT NULL COMMENT '资产类型',
  `user_department` varchar(50) NOT NULL COMMENT '使用部门',
  `purchase_date` date NOT NULL COMMENT '购置日期',
  `asset_status` varchar(20) NOT NULL COMMENT '资产状态',
  `next_inspection_date` date NOT NULL COMMENT '下次检验日期',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`asset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='校医院资产表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hospital_asset`
--

LOCK TABLES `hospital_asset` WRITE;
/*!40000 ALTER TABLE `hospital_asset` DISABLE KEYS */;
/*!40000 ALTER TABLE `hospital_asset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `housing_application`
--

DROP TABLE IF EXISTS `housing_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `housing_application`
--

LOCK TABLES `housing_application` WRITE;
/*!40000 ALTER TABLE `housing_application` DISABLE KEYS */;
INSERT INTO `housing_application` VALUES ('HA-2024-001','S2021001','2024001001','张三','计算机学院','2024级','transfer','1斋-101','与室友作息不合','室友晚睡影响休息','2斋-203',NULL,NULL,NULL,'pending','2024-12-01 10:30:00',NULL,NULL,NULL,'2025-12-02 03:16:51','2025-12-02 03:16:51',0),('HA-2024-002','S2021002','2024002002','李四','材料学院','2023级','leave','2斋-203','实习','在校外企业实习需外宿',NULL,'2024-12-01','2025-03-01',NULL,'pending','2024-12-01 14:20:00',NULL,NULL,NULL,'2025-12-02 03:16:51','2025-12-02 03:16:51',0),('HA-2024-003','S2021003','2024003003','王五','物理学院','2022级','transfer','3斋-105','毕业离校','准备考研需要安静环境','1斋-102',NULL,NULL,NULL,'rejected','2024-12-01 16:00:00','2025-12-02 03:28:05','管理员','1','2025-12-02 03:16:51','2025-12-02 03:16:51',0),('HA-2024-004','S2021004','2024004004','赵六','计算机学院','2024级','transfer','1斋-102','身体健康原因','需要住在低层方便就医','1斋-101',NULL,NULL,NULL,'approved','2024-11-28 09:00:00','2024-11-28 15:00:00','管理员','已通过','2025-12-02 03:16:51','2025-12-02 03:16:51',0),('HA-2024-005','S2021005','2024005005','钱七','材料学院','2023级','checkout','2斋-301','回家','因家庭原因需要退宿回家',NULL,NULL,NULL,'2024-12-15','approved','2024-12-02 08:30:00','2025-12-02 03:22:58','管理员',NULL,'2025-12-02 03:16:51','2025-12-02 03:16:51',0);
/*!40000 ALTER TABLE `housing_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `internal_equipment_borrow`
--

DROP TABLE IF EXISTS `internal_equipment_borrow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `internal_equipment_borrow` (
  `borrow_id` varchar(50) NOT NULL COMMENT '借用ID',
  `user_id` varchar(50) NOT NULL COMMENT '领用人ID',
  `applicant_name` varchar(50) DEFAULT NULL COMMENT '申请人姓名',
  `applicant_type` varchar(20) DEFAULT NULL COMMENT '申请人类型',
  `department` varchar(50) DEFAULT NULL COMMENT '部门',
  `purpose` varchar(500) DEFAULT NULL COMMENT '借用用途',
  `equipment_id` varchar(50) NOT NULL COMMENT '器材ID',
  `borrow_date` date NOT NULL COMMENT '借用日期',
  `return_date` date NOT NULL COMMENT '应还日期',
  `actual_return_date` date DEFAULT NULL COMMENT '实还日期',
  `rent_amount` decimal(8,2) DEFAULT NULL COMMENT '租金',
  `penalty_fee` decimal(10,2) DEFAULT '0.00' COMMENT '违约金',
  `status` varchar(20) DEFAULT '借用中' COMMENT '状态',
  `permission_approval` varchar(20) DEFAULT 'pending' COMMENT '权限审核',
  `equipment_approval` varchar(20) DEFAULT 'pending' COMMENT '器材审核',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`borrow_id`),
  KEY `fk_ib_equip` (`equipment_id`),
  CONSTRAINT `fk_ib_equip` FOREIGN KEY (`equipment_id`) REFERENCES `equipment_archive` (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校内器材借用表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `internal_equipment_borrow`
--

LOCK TABLES `internal_equipment_borrow` WRITE;
/*!40000 ALTER TABLE `internal_equipment_borrow` DISABLE KEYS */;
INSERT INTO `internal_equipment_borrow` VALUES ('07627e739b85d6476914959a54a10196','S2021001',NULL,NULL,NULL,NULL,'EQ-HX-2023-001','2025-11-27','2025-11-30',NULL,NULL,0.00,'REJECTED','pending','pending','2025-11-27 11:39:52','2025-11-27 11:39:52',0),('1da5073d7461c05b040a6de622398feb','STU-2023',NULL,NULL,NULL,NULL,'EQ-TY-2025-001','2025-11-28','2025-12-04',NULL,NULL,0.00,'BORROWED','pending','pending','2025-11-28 22:28:38','2025-11-28 22:28:38',0),('3211689f294b9401605ee96c03b5e4ec','S2021001',NULL,NULL,NULL,NULL,'EQ-TY-2024-001','2025-11-27','2025-11-30',NULL,NULL,0.00,'BORROWED','pending','pending','2025-11-27 16:42:05','2025-11-27 16:42:05',0),('3bff233c0799eaef87827dd09930ebcd','S2021001',NULL,NULL,NULL,NULL,'EQ-SW-2024-002','2025-11-27','2025-11-30',NULL,NULL,0.00,'BORROWED','pending','pending','2025-11-27 16:20:40','2025-11-27 16:20:40',0),('537b5ea843b43dcefedfe8ada6943785','S2021001',NULL,NULL,NULL,NULL,'EQ-SW-2023-001','2025-11-27','2025-11-29',NULL,NULL,0.00,'REJECTED','pending','pending','2025-11-27 12:01:22','2025-11-27 12:01:22',0),('7f48192233195b1c52e9e9d482e781f5','S2021001',NULL,NULL,NULL,NULL,'EQ-SW-2023-001','2025-11-27','2025-12-02',NULL,NULL,0.00,'BORROWED','pending','pending','2025-11-27 13:54:28','2025-11-27 13:54:28',0),('81b08bd6a6bf20261c2dfc33a96da655','STU-2023',NULL,NULL,NULL,NULL,'EQ-TY-2024-002','2025-11-28','2025-12-01',NULL,NULL,0.00,'REJECTED','pending','pending','2025-11-28 15:46:21','2025-11-28 15:46:21',0),('aaaacefcd3d648dbdaf1e8ccf872b6b8','S2021001',NULL,NULL,NULL,NULL,'EQ-JM-2024-002','2025-11-27','2025-11-30',NULL,NULL,0.00,'REJECTED','pending','pending','2025-11-27 11:50:40','2025-11-27 11:50:40',0),('b58ee9fd18ce401040a807cd7abcbc44','S2021001',NULL,NULL,NULL,NULL,'EQ-SW-2023-001','2025-11-27','2025-11-30',NULL,NULL,0.00,'BORROWED','pending','pending','2025-11-27 15:33:04','2025-11-27 15:33:04',0),('ca58bffbfb0cfe795b8317a84122fdfc','S2021001',NULL,NULL,NULL,NULL,'EQ-JM-2025-001','2025-11-27','2025-11-30',NULL,NULL,0.00,'BORROWED','pending','pending','2025-11-27 12:58:08','2025-11-27 12:58:08',0),('cc70cdcdfd93ecd5e45917a99bf7d917','S2021001',NULL,NULL,NULL,NULL,'EQ-JM-2025-001','2025-11-27','2025-11-30',NULL,NULL,0.00,'BORROWED','pending','pending','2025-11-27 13:19:48','2025-11-27 13:19:48',0),('f8d9d877be84b5f75501d2ab27cc0f68','S2021001',NULL,NULL,NULL,NULL,'EQ-JM-2025-001','2025-11-27','2025-11-30',NULL,NULL,0.00,'REJECTED','pending','pending','2025-11-27 12:39:20','2025-11-27 12:39:20',0),('IB-2025-002','U-SY-2018-001',NULL,NULL,NULL,NULL,'EQ-WL-2023-002','2025-11-10','2025-11-27',NULL,0.00,0.00,'借用中','pending','pending','2025-11-26 15:57:34','2025-11-26 15:57:34',0),('IB-2025-003','U-JW-2020-003',NULL,NULL,NULL,NULL,'EQ-TY-2024-001','2025-11-08','2025-11-15','2025-11-15',0.00,0.00,'已归还','pending','pending','2025-11-26 15:57:34','2025-11-26 15:57:34',0),('IB-2025-004','U-SY-2020-005',NULL,NULL,NULL,NULL,'EQ-HX-2023-001','2025-11-12','2025-11-29',NULL,0.00,0.00,'借用中','pending','pending','2025-11-26 15:57:34','2025-11-26 15:57:34',0),('IB-2025-005','U-GZ-2019-007',NULL,NULL,NULL,NULL,'EQ-SW-2023-001','2025-11-05','2025-11-12','2025-11-12',0.00,0.00,'已归还','pending','pending','2025-11-26 15:57:34','2025-11-26 15:57:34',0),('IB-2025-006','U-HZ-2021-009',NULL,NULL,NULL,NULL,'EQ-JM-2024-002','2025-11-15','2025-11-29',NULL,0.00,0.00,'借用中','pending','pending','2025-11-26 15:57:34','2025-11-26 15:57:34',0),('IB-2025-007','U-RS-2017-011',NULL,NULL,NULL,NULL,'EQ-WL-2024-003','2025-11-03','2025-11-10','2025-11-10',0.00,0.00,'已归还','pending','pending','2025-11-26 15:57:34','2025-11-26 15:57:34',0),('IB-2025-008','U-CW-2018-013',NULL,NULL,NULL,NULL,'EQ-HX-2024-002','2025-11-18','2025-11-28',NULL,0.00,0.00,'借用中','pending','pending','2025-11-26 15:57:34','2025-11-26 15:57:34',0),('IB-2025-009','U-JW-2022-015',NULL,NULL,NULL,NULL,'EQ-SW-2024-002','2025-11-01','2025-11-08','2025-11-08',0.00,0.00,'已归还','pending','pending','2025-11-26 15:57:34','2025-11-26 15:57:34',0),('IB-2025-010','U-SY-2021-017',NULL,NULL,NULL,NULL,'EQ-TY-2024-002','2025-11-20','2025-11-27',NULL,0.00,0.00,'借用中','pending','pending','2025-11-26 15:57:34','2025-11-26 15:57:34',0),('IB-2025-011','U-GZ-2020-019',NULL,NULL,NULL,NULL,'EQ-JM-2025-001','2025-11-06','2025-11-13','2025-11-13',0.00,0.00,'已归还','pending','pending','2025-11-26 15:57:34','2025-11-26 15:57:34',0),('JY202511301424200001','ADMIN-888','超级管理员',NULL,NULL,'毕设','EQ-WL-2023-002','2025-11-30','2025-12-03',NULL,NULL,0.00,'approved','approved','approved','2025-11-30 14:24:20','2025-11-30 14:24:20',0),('JY202511301441370001','ADMIN-888','超级管理员',NULL,NULL,'实验比赛','EQ-TY-2024-002','2025-11-30','2025-12-04','2025-11-30',NULL,0.00,'returned','approved','approved','2025-11-30 14:41:37','2025-11-30 14:41:37',0),('JY202511301627560003','ADMIN-888','超级管理员',NULL,NULL,'展示使用','EQ-TY-2024-002','2025-12-02','2025-12-04',NULL,NULL,0.00,'rejected','rejected','rejected','2025-11-30 16:27:57','2025-11-30 16:27:57',0),('JY202511301830140001','U202341001','大雄',NULL,NULL,'考试','EQ-WL-2024-003','2025-12-02','2025-12-03',NULL,NULL,0.00,'cancelled','pending','pending','2025-11-30 18:30:15','2025-11-30 18:30:15',0),('JY202512012348240001','U202341001','大雄',NULL,NULL,'1','EQ-WL-2024-003','2025-12-01','2025-12-03','2025-12-02',NULL,0.00,'returned','approved','approved','2025-12-01 23:48:25','2025-12-01 23:48:25',0),('JY202512012349090002','U202341001','大雄',NULL,NULL,'看lyy臭臭嘤嘤','EQ-WL-2024-003','2025-12-01','2025-12-08','2025-12-01',NULL,0.00,'returned','approved','approved','2025-12-01 23:49:10','2025-12-01 23:49:10',0),('JY202512020254500001','ADMIN-888','超级管理员',NULL,NULL,'111','EQ-WL-2024-003','2025-12-09','2025-12-10',NULL,NULL,0.00,'cancelled','pending','pending','2025-12-02 02:54:51','2025-12-02 02:54:51',0);
/*!40000 ALTER TABLE `internal_equipment_borrow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `internal_event_booking`
--

DROP TABLE IF EXISTS `internal_event_booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `internal_event_booking` (
  `event_id` varchar(50) NOT NULL COMMENT '活动ID',
  `organizer_dept` varchar(50) NOT NULL COMMENT '主办部门',
  `venue_id` varchar(20) NOT NULL COMMENT '场地ID',
  `event_date` date NOT NULL COMMENT '活动日期',
  `discount_fee` decimal(10,2) DEFAULT NULL COMMENT '优惠费用',
  `status` varchar(20) DEFAULT '申请中' COMMENT '状态',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`event_id`),
  KEY `fk_ib_venue` (`venue_id`),
  CONSTRAINT `fk_ib_venue` FOREIGN KEY (`venue_id`) REFERENCES `building_archive` (`building_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校内活动预订表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `internal_event_booking`
--

LOCK TABLES `internal_event_booking` WRITE;
/*!40000 ALTER TABLE `internal_event_booking` DISABLE KEYS */;
INSERT INTO `internal_event_booking` VALUES ('EV202512020203480005','学生会','B-F-01-01','2025-12-21',0.00,'pending','2025-11-26 16:31:40','2025-11-26 16:31:40',0),('IE-2025-002','学生会','B-F-01-01','2025-12-21',0.00,'已通过','2025-11-26 16:31:40','2025-11-26 16:31:40',0),('IE-2025-003','教务处','B-A-01-03','2025-12-23',0.00,'已通过','2025-11-26 16:31:40','2025-11-26 16:31:40',0),('IE-2025-004','校团委','B-A-01-04','2025-03-26',0.00,'已结束','2025-11-26 16:31:40','2025-11-26 16:31:40',0),('IE-2025-005','研究生院','B-F-01-01','2025-12-29',0.00,'已通过','2025-11-26 16:31:40','2025-11-26 16:31:40',0),('IE-2025-006','工会','B-H-01-02','2026-01-06',0.00,'已通过','2025-11-26 16:31:40','2025-11-26 16:31:40',0),('IE-2025-007','科研处','B-A-01-03','2026-01-09',0.00,'待确认','2025-11-26 16:31:40','2025-11-26 16:31:40',0),('IE-2025-008','学生处','B-F-01-01','2026-01-13',0.00,'待确认','2025-11-26 16:31:40','2025-11-26 16:31:40',0),('IE-2025-009','招生办','B-A-01-04','2026-01-16',0.00,'待确认','2025-11-26 16:31:40','2025-11-26 16:31:40',0),('IE-2025-010','校友会','B-F-01-01','2026-01-21',0.00,'待确认','2025-11-26 16:31:40','2025-11-26 16:31:40',0),('IE-2025-011','国际交流处','B-A-01-03','2026-01-23',0.00,'待确认','2025-11-26 16:31:40','2025-11-26 16:31:40',0);
/*!40000 ALTER TABLE `internal_event_booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenance_record`
--

DROP TABLE IF EXISTS `maintenance_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `maintenance_record` (
  `maintenance_id` varchar(50) NOT NULL COMMENT '维保ID',
  `asset_id` varchar(50) NOT NULL COMMENT '资产ID',
  `asset_type` varchar(20) DEFAULT NULL COMMENT '资产类型',
  `asset_name` varchar(100) DEFAULT NULL COMMENT '资产名称',
  `location` varchar(100) DEFAULT NULL COMMENT '位置',
  `fault_description` varchar(500) DEFAULT NULL COMMENT '故障描述',
  `fault_reason` varchar(500) DEFAULT NULL COMMENT '故障原因',
  `reporter_id` varchar(50) DEFAULT NULL COMMENT '报修人ID',
  `reporter_name` varchar(50) DEFAULT NULL COMMENT '报修人姓名',
  `reporter_department` varchar(50) DEFAULT NULL COMMENT '报修人部门',
  `report_time` datetime DEFAULT NULL COMMENT '报修时间',
  `maintenance_person` varchar(50) NOT NULL COMMENT '维保人员',
  `maintenance_date` date NOT NULL COMMENT '维保日期',
  `maintenance_reason` varchar(200) DEFAULT NULL COMMENT '维护原因',
  `maintenance_description` varchar(500) DEFAULT NULL COMMENT '维修描述',
  `parts_replaced` varchar(500) DEFAULT NULL COMMENT '更换配件(JSON)',
  `cost` decimal(8,2) DEFAULT NULL COMMENT '维修费用',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态',
  `completion_time` datetime DEFAULT NULL COMMENT '完成时间',
  `maintenance_type` varchar(20) DEFAULT '维修' COMMENT '类型',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`maintenance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='维修记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenance_record`
--

LOCK TABLES `maintenance_record` WRITE;
/*!40000 ALTER TABLE `maintenance_record` DISABLE KEYS */;
INSERT INTO `maintenance_record` VALUES ('9a0f7d99ce548308b83496bb15228d16','EQ-JM-2025-001',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Teacher','2025-11-27','',NULL,NULL,NULL,'pending',NULL,'维修','2025-11-27 13:13:00','2025-11-27 13:13:00',0),('MR-2025-002','EQ-HX-2024-002',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'李四','2025-10-20','分光光度不准确',NULL,NULL,450.00,'pending',NULL,'维修','2025-11-26 16:19:58','2025-11-26 16:19:58',0),('MR-2025-003','B-D-01-02',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'王五','2025-10-15','实验室水电维修',NULL,NULL,1200.00,'pending',NULL,'维修','2025-11-26 16:19:58','2025-11-26 16:19:58',0),('MR-2025-004','EQ-JM-2024-002',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'赵六','2025-10-28','天平校准',NULL,NULL,300.00,'pending',NULL,'保养','2025-11-26 16:19:58','2025-11-26 16:19:58',0),('MR-2025-005','R-B-A-01-03-0201',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'孙七','2025-10-12','投影仪维修',NULL,NULL,600.00,'pending',NULL,'维修','2025-11-26 16:19:58','2025-11-26 16:19:58',0),('MR-2025-006','EQ-SW-2023-001',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'周八','2025-10-30','显微镜镜头清洁',NULL,NULL,150.00,'pending',NULL,'保养','2025-11-26 16:19:58','2025-11-26 16:19:58',0),('MR-2025-007','B-C-01-04',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'吴九','2025-10-08','宿舍门锁更换',NULL,NULL,300.00,'pending',NULL,'维修','2025-11-26 16:19:58','2025-11-26 16:19:58',0),('MR-2025-008','EQ-WL-2023-002',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'郑十','2025-10-25','测距仪电池更换',NULL,NULL,200.00,'pending',NULL,'维修','2025-11-26 16:19:58','2025-11-26 16:19:58',0),('MR-2025-009','R-B-H-01-02-0102',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'王十一','2025-10-18','药房设备维护',NULL,NULL,500.00,'pending',NULL,'保养','2025-11-26 16:19:58','2025-11-26 16:19:58',0),('MR-2025-010','EQ-TY-2024-001',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'李十二','2025-10-22','打印机卡纸故障',NULL,NULL,180.00,'pending',NULL,'维修','2025-11-26 16:19:58','2025-11-26 16:19:58',0),('MR-2025-011','B-F-01-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'张十三','2025-10-05','体育馆照明维修',NULL,NULL,1800.00,'pending',NULL,'维修','2025-11-26 16:19:58','2025-11-26 16:19:58',0),('WB202511301649040001','B-001','building','逸夫楼101教室空调',NULL,'空调不制冷',NULL,NULL,'超级管理员','用户部门','2025-11-30 16:49:05','李师傅','2025-11-30',NULL,NULL,NULL,NULL,'assigned',NULL,'维修','2025-11-30 16:49:05','2025-11-30 16:49:05',0),('WB202512011918110001','B-001','building','逸夫楼101教室空调',NULL,'空调不制冷',NULL,NULL,'李娜','用户部门','2025-12-01 19:18:11','待分配','2025-12-01',NULL,NULL,NULL,NULL,'pending',NULL,'维修','2025-12-01 19:18:11','2025-12-01 19:18:11',0),('WB202512012151060004','E-001','equipment','高精度光谱分析仪',NULL,'1111',NULL,NULL,'设备管理员','用户部门','2025-12-01 21:51:06','张师傅','2025-12-01',NULL,NULL,NULL,NULL,'assigned',NULL,'维修','2025-12-01 21:51:06','2025-12-01 21:51:06',0);
/*!40000 ALTER TABLE `maintenance_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `outbound_equipment_borrow`
--

DROP TABLE IF EXISTS `outbound_equipment_borrow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `outbound_equipment_borrow` (
  `borrow_id` varchar(50) NOT NULL COMMENT '借用ID',
  `applicant_unit` varchar(100) NOT NULL COMMENT '申请单位',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `equipment_id` varchar(50) NOT NULL COMMENT '器材ID',
  `borrow_days` int NOT NULL COMMENT '借用天数',
  `purpose` varchar(500) DEFAULT NULL COMMENT '借用用途',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '预计归还日期',
  `actual_return_date` date DEFAULT NULL COMMENT '实际归还日期',
  `qualification_files` varchar(500) DEFAULT NULL COMMENT '资质文件(JSON)',
  `deposit_amount` decimal(10,2) DEFAULT NULL COMMENT '押金',
  `rent_amount` decimal(10,2) DEFAULT NULL COMMENT '租金',
  `penalty_fee` decimal(10,2) DEFAULT '0.00' COMMENT '违约金',
  `status` varchar(20) DEFAULT '申请中' COMMENT '状态',
  `qualification_approval` varchar(20) DEFAULT 'pending' COMMENT '资质审核状态',
  `equipment_approval` varchar(20) DEFAULT 'pending' COMMENT '器材审核状态',
  `deposit_paid` tinyint DEFAULT '0' COMMENT '押金已付(0否1是)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`borrow_id`),
  KEY `fk_ob_equip` (`equipment_id`),
  CONSTRAINT `fk_ob_equip` FOREIGN KEY (`equipment_id`) REFERENCES `equipment_archive` (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校外器材借用表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `outbound_equipment_borrow`
--

LOCK TABLES `outbound_equipment_borrow` WRITE;
/*!40000 ALTER TABLE `outbound_equipment_borrow` DISABLE KEYS */;
INSERT INTO `outbound_equipment_borrow` VALUES ('f25c9ed3042a0f3ed2bda916f4778a6b','student',NULL,NULL,'EQ-HX-2023-001',1,NULL,NULL,NULL,NULL,NULL,1800.00,0.00,0.00,'申请中','pending','pending',0,'2025-11-27 04:03:29','2025-11-27 04:03:29',0),('JY202512012142000002','USTB','帅哥','11111111111','EQ-WL-2024-003',1,'商业用途','2025-12-01','2025-12-02',NULL,NULL,1000.00,0.00,0.00,'approved','approved','approved',0,'2025-12-01 21:42:01','2025-12-01 21:42:01',0),('JY202512012148260003','USTB','USTB','USTB','EQ-TY-2024-002',1,'USTB','2025-12-01','2025-12-02',NULL,NULL,1000.00,0.00,0.00,'approved','approved','approved',0,'2025-12-01 21:48:26','2025-12-01 21:48:26',0),('OB-2025-002','某工业大学实验室',NULL,NULL,'EQ-JM-2024-002',14,NULL,NULL,NULL,NULL,NULL,1500.00,840.00,0.00,'已归还','pending','pending',0,'2025-11-26 15:54:58','2025-11-26 15:54:58',0),('OB-2025-003','某科研研究院',NULL,NULL,'EQ-HX-2023-001',7,NULL,NULL,NULL,NULL,NULL,2000.00,630.00,0.00,'已归还','pending','pending',0,'2025-11-26 15:54:58','2025-11-26 15:54:58',0),('OB-2025-004','某合作中学',NULL,NULL,'EQ-SW-2023-001',5,NULL,NULL,NULL,NULL,NULL,1000.00,225.00,0.00,'已归还','pending','pending',0,'2025-11-26 15:54:58','2025-11-26 15:54:58',0),('OB-2025-005','某科技公司',NULL,NULL,'EQ-JM-2025-001',10,NULL,NULL,NULL,NULL,NULL,3000.00,1400.00,0.00,'借用中','pending','pending',0,'2025-11-26 15:54:58','2025-11-26 15:54:58',0),('OB-2025-006','某职业技术学院',NULL,NULL,'EQ-WL-2023-002',8,NULL,NULL,NULL,NULL,NULL,1000.00,336.00,0.00,'已归还','pending','pending',0,'2025-11-26 15:54:58','2025-11-26 15:54:58',0),('OB-2025-007','某医疗器械公司',NULL,NULL,'EQ-HX-2024-002',12,NULL,NULL,NULL,NULL,NULL,2000.00,540.00,0.00,'逾期','pending','pending',0,'2025-11-26 15:54:58','2025-11-26 15:54:58',0),('OB-2025-008','某农业科学院',NULL,NULL,'EQ-SW-2024-002',6,NULL,NULL,NULL,NULL,NULL,1500.00,315.00,0.00,'已归还','pending','pending',0,'2025-11-26 15:54:58','2025-11-26 15:54:58',0),('OB-2025-009','某环保检测中心',NULL,NULL,'EQ-TY-2024-001',3,NULL,NULL,NULL,NULL,NULL,500.00,67.50,0.00,'借用中','pending','pending',0,'2025-11-26 15:54:58','2025-11-26 15:54:58',0),('OB-2025-010','某高校物理系',NULL,NULL,'EQ-WL-2024-003',9,NULL,NULL,NULL,NULL,NULL,1000.00,315.00,0.00,'已归还','pending','pending',0,'2025-11-26 15:54:58','2025-11-26 15:54:58',0),('OB-2025-011','某电子科技公司',NULL,NULL,'EQ-JM-2024-002',15,NULL,NULL,NULL,NULL,NULL,1500.00,900.00,0.00,'借用中','pending','pending',0,'2025-11-26 15:54:58','2025-11-26 15:54:58',0);
/*!40000 ALTER TABLE `outbound_equipment_borrow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_resource`
--

DROP TABLE IF EXISTS `room_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_resource` (
  `room_id` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '房间编号',
  `building_id` varchar(20) NOT NULL COMMENT '所属建筑ID',
  `room_number` varchar(20) NOT NULL COMMENT '房间号',
  `room_name` varchar(100) DEFAULT NULL COMMENT '房间名称',
  `room_type` varchar(50) DEFAULT NULL COMMENT '房间类型',
  `usage_type` varchar(50) DEFAULT NULL COMMENT '使用类型',
  `area` decimal(8,2) DEFAULT NULL COMMENT '面积',
  `capacity` int DEFAULT NULL COMMENT '容纳人数',
  `status` varchar(20) DEFAULT 'IDLE' COMMENT '状态',
  `version` int DEFAULT '0' COMMENT '乐观锁',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`room_id`),
  KEY `idx_building` (`building_id`),
  CONSTRAINT `fk_room_building` FOREIGN KEY (`building_id`) REFERENCES `building_archive` (`building_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房间资源表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_resource`
--

LOCK TABLES `room_resource` WRITE;
/*!40000 ALTER TABLE `room_resource` DISABLE KEYS */;
INSERT INTO `room_resource` VALUES ('R-B-01-101','B-01','101','阶梯教室','教室','教学',120.00,100,'IDLE',0,'2025-11-25 13:37:55','2025-11-25 13:37:55',0),('R-B-A-01-03-0201','B-A-01-03','201','逸夫楼201','教室','教学',90.00,50,'IN_USE',1,'2025-11-26 01:06:26','2025-11-26 01:06:26',0),('R-B-A-01-03-0210','B-A-01-03','201','逸夫楼201','教室','教学',90.00,50,'IN_USE',1,'2025-11-26 01:06:26','2025-12-02 01:37:32',1),('R-B-B-01-02-0405','B-B-01-02','405','财务处办公室','办公室','办公',40.00,8,'IDLE',0,'2025-11-26 01:06:26','2025-11-26 01:06:26',0),('R-B-C-01-04-0502','B-C-01-04','502','七斋655','宿舍','住宿',25.00,4,'IDLE',0,'2025-11-26 01:06:26','2025-11-26 01:06:26',0),('R-B-C-01-05-0608','B-C-01-05','608','五斋608','宿舍','住宿',25.00,4,'IDLE',0,'2025-11-26 01:06:26','2025-11-26 01:06:26',0),('R-B-D-01-02-0301','B-D-01-02','301','主楼热模拟实验室301','实验室','教学',80.00,20,'IDLE',0,'2025-11-26 01:06:26','2025-11-26 01:06:26',0),('R-B-E-01-01-0203','B-E-01-01','203','1号楼203室','住宅','居住',120.00,3,'IDLE',0,'2025-11-26 01:06:26','2025-11-26 01:06:26',0),('R-B-F-01-01-0101','B-F-01-01','101','体育馆乒乓球馆','场馆','活动',1200.00,500,'IDLE',0,'2025-11-26 01:06:26','2025-11-26 01:06:26',0),('R-B-F-01-04-0306','B-F-01-04','106','鸿博园1层后厨','餐饮','餐饮',85.00,45,'IDLE',0,'2025-11-26 01:06:26','2025-11-26 01:06:26',0),('R-B-F-01-05-0402','B-F-01-05','302','万秀园1层2号窗口','餐饮','餐饮',90.00,25,'IDLE',0,'2025-11-26 01:06:26','2025-11-26 01:06:26',0),('R-B-H-01-02-0102','B-H-01-02','102','校医院内科诊室102','诊室','医疗',60.00,4,'IDLE',0,'2025-11-26 01:06:26','2025-11-26 01:06:26',0);
/*!40000 ALTER TABLE `room_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_dorm_alloc`
--

DROP TABLE IF EXISTS `student_dorm_alloc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_dorm_alloc` (
  `allocation_id` varchar(50) NOT NULL COMMENT '分配ID',
  `student_id` varchar(50) NOT NULL COMMENT '学生ID',
  `student_name` varchar(50) DEFAULT NULL COMMENT '学生姓名',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `college` varchar(100) DEFAULT NULL COMMENT '学院',
  `major` varchar(100) DEFAULT NULL COMMENT '专业',
  `grade` varchar(20) DEFAULT NULL COMMENT '年级',
  `dorm_id` varchar(20) NOT NULL COMMENT '宿舍ID',
  `building_name` varchar(100) DEFAULT NULL COMMENT '宿舍楼',
  `dormitory_code` varchar(50) DEFAULT NULL COMMENT '宿舍号',
  `bed_number` int DEFAULT NULL COMMENT '床位号',
  `check_in_date` date NOT NULL COMMENT '入住日期',
  `check_out_date` date DEFAULT NULL COMMENT '退宿日期',
  `status` varchar(20) DEFAULT '在住' COMMENT '状态',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`allocation_id`),
  KEY `fk_sd_room` (`dorm_id`),
  CONSTRAINT `fk_sd_room` FOREIGN KEY (`dorm_id`) REFERENCES `room_resource` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生宿舍分配表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_dorm_alloc`
--

LOCK TABLES `student_dorm_alloc` WRITE;
/*!40000 ALTER TABLE `student_dorm_alloc` DISABLE KEYS */;
INSERT INTO `student_dorm_alloc` VALUES ('312daff9eafe321c9c22fa0a8dedf6fb','S2021001',NULL,NULL,NULL,NULL,NULL,'R-B-C-01-04-0502',NULL,NULL,NULL,'2025-11-27',NULL,'申请中','2025-11-27 04:53:53','2025-11-27 04:53:53',0),('563b3b1f7d71ff13acceb9d36fefaaf7','T10086',NULL,NULL,NULL,NULL,NULL,'R-B-A-01-03-0201',NULL,NULL,NULL,'2025-11-27',NULL,'申请中','2025-11-27 09:06:26','2025-11-27 09:06:26',0),('5f03995aed446e197f41113cddf5c4b4','S2021001',NULL,NULL,NULL,NULL,NULL,'R-B-01-101',NULL,NULL,NULL,'2025-11-27',NULL,'申请中','2025-11-27 09:05:52','2025-11-27 09:05:53',0),('AL202512020052560001','1223',NULL,NULL,NULL,NULL,NULL,'R-B-A-01-03-0201',NULL,NULL,NULL,'2025-11-27',NULL,'active','2025-11-27 09:06:26','2025-11-27 09:06:26',0),('SD-2025-002','U202545002',NULL,NULL,NULL,NULL,NULL,'R-B-C-01-04-0502',NULL,NULL,NULL,'2025-09-01',NULL,'入住中','2025-11-26 16:16:09','2025-11-26 16:16:09',0),('SD-2025-003','U202556032',NULL,NULL,NULL,NULL,NULL,'R-B-C-01-04-0502',NULL,NULL,NULL,'2025-09-01',NULL,'入住中','2025-11-26 16:16:09','2025-11-26 16:16:09',0),('SD-2025-004','U202501044',NULL,NULL,NULL,NULL,NULL,'R-B-C-01-04-0503',NULL,NULL,NULL,'2025-09-01',NULL,'入住中','2025-11-26 16:16:09','2025-11-26 16:16:09',0),('SD-2025-005','U202501053',NULL,NULL,NULL,NULL,NULL,'R-B-C-01-04-0503',NULL,NULL,NULL,'2025-09-01',NULL,'入住中','2025-11-26 16:16:09','2025-11-26 16:16:09',0),('SD-2025-006','U202500466',NULL,NULL,NULL,NULL,NULL,'R-B-C-01-05-0608',NULL,NULL,NULL,'2025-09-01',NULL,'入住中','2025-11-26 16:16:09','2025-11-26 16:16:09',0),('SD-2025-007','U202503078',NULL,NULL,NULL,NULL,NULL,'R-B-C-01-05-0608',NULL,NULL,NULL,'2025-09-01',NULL,'入住中','2025-11-26 16:16:09','2025-11-26 16:16:09',0),('SD-2025-008','U202504083',NULL,NULL,NULL,NULL,NULL,'R-B-C-01-05-0609',NULL,NULL,NULL,'2025-09-01',NULL,'入住中','2025-11-26 16:16:09','2025-11-26 16:16:09',0),('SD-2025-009','U202503092',NULL,NULL,NULL,NULL,NULL,'R-B-C-01-05-0609',NULL,NULL,NULL,'2025-09-01',NULL,'入住中','2025-11-26 16:16:09','2025-11-26 16:16:09',0),('SD-2025-010','U202000101',NULL,NULL,NULL,NULL,NULL,'R-B-C-01-04-0504',NULL,NULL,NULL,'2020-09-01','2024-07-01','已退宿','2025-11-26 16:16:09','2025-11-26 16:16:09',0),('SD-2025-011','U202000202',NULL,NULL,NULL,NULL,NULL,'R-B-C-01-04-0504',NULL,NULL,NULL,'2020-09-01','2024-07-01','已退宿','2025-11-26 16:16:09','2025-11-26 16:16:09',0);
/*!40000 ALTER TABLE `student_dorm_alloc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` varchar(50) NOT NULL COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `id_card` varchar(30) DEFAULT NULL COMMENT '身份证号',
  `role` varchar(20) DEFAULT 'STUDENT' COMMENT '角色: ADMIN/TEACHER/STUDENT',
  `staff_level` varchar(30) DEFAULT NULL COMMENT '职称等级',
  `title` varchar(50) DEFAULT NULL COMMENT '职称',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `dept` varchar(50) DEFAULT NULL COMMENT '部门/学院',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES ('A2023001','A2023001','123456','张伟',NULL,NULL,'finance_manager',NULL,'财务主管','13955667788',NULL,'财务处','2025-11-30 17:34:20'),('A2023002','A2023002','123456','刘芳',NULL,NULL,'finance_manager',NULL,'财务专员','13722334455',NULL,'财务处','2025-11-30 17:34:20'),('AB201108010001','AB201108010001','123456','胡丹',NULL,NULL,'logistics_admin',NULL,'安保主管','13800000101',NULL,'后勤处安保部门','2025-11-30 17:34:20'),('ADMIN-888','admin','123456','超级管理员',NULL,NULL,'system_admin',NULL,NULL,'13800000001','admin@ustb.edu.cn','信息中心','2025-11-28 14:45:22'),('CL201806010001','CL201806010001','123456','白肖',NULL,NULL,'logistics_admin',NULL,'车辆管理员','13800000102',NULL,'后勤处车辆部门','2025-11-30 17:34:20'),('D202500002','D202500002','123456','设备管理员',NULL,NULL,'equip_manager',NULL,'器材主管','13800000201',NULL,'实验室管理中心','2025-11-30 17:34:20'),('EMP2025003','EMP2025003','123456','王五',NULL,NULL,'maintenance_staff',NULL,'维修人员','13800000103',NULL,'后勤处维修部门','2025-11-30 17:34:20'),('L202500001','L202500001','123456','实验室管理员',NULL,NULL,'equip_manager',NULL,'实验室主管','13800000202',NULL,'实验室管理中心','2025-11-30 17:34:20'),('M2','M2','123456','房产管理员',NULL,NULL,'room_manager',NULL,'房产主管','13800000301',NULL,'房产管理处','2025-11-30 17:34:20'),('T2023001','T2023001','123456','李娜',NULL,NULL,'teacher',NULL,'讲师','15812345678',NULL,'计算机学院','2025-11-30 17:34:20'),('T2023002','T2023002','123456','王芳',NULL,NULL,'teacher',NULL,'副教授','13698765432',NULL,'信息工程学院','2025-11-30 17:34:20'),('T2023003','T2023003','123456','陈静',NULL,NULL,'teacher',NULL,'教授','13377889900',NULL,'自动化学院','2025-11-30 17:34:20'),('T2023004','T2023004','123456','王老师',NULL,NULL,'teacher',NULL,'讲师','13994003726',NULL,'材料学院','2025-11-30 17:34:20'),('T2023005','T2023005','123456','张三',NULL,NULL,'teacher',NULL,'副教授','15704900850',NULL,'机械学院','2025-11-30 17:34:20'),('T2023006','T2023006','123456','李四',NULL,NULL,'teacher',NULL,'讲师','13823247878',NULL,'土木学院','2025-11-30 17:34:20'),('T2023007','T2023007','123456','黎明',NULL,NULL,'teacher',NULL,'教授','15023456789',NULL,'经管学院','2025-11-30 17:34:20'),('T2023008','T2023008','123456','张明',NULL,NULL,'teacher',NULL,'副教授','13623456789',NULL,'外国语学院','2025-11-30 17:34:20'),('U202341001','U202341001','123456','大雄',NULL,NULL,'student',NULL,NULL,'13512340001',NULL,'计算机学院软件工程1班','2025-11-30 17:34:20'),('U202341002','U202341002','123456','静香',NULL,NULL,'student',NULL,NULL,'13512340002',NULL,'计算机学院软件工程1班','2025-11-30 17:34:20'),('U202341003','U202341003','123456','小夫',NULL,NULL,'student',NULL,NULL,'13512340003',NULL,'信息工程学院通信1班','2025-11-30 17:34:20'),('U202341004','U202341004','123456','胖虎',NULL,NULL,'student',NULL,NULL,'13512340004',NULL,'自动化学院自动化1班','2025-11-30 17:34:20'),('U202341005','U202341005','123456','出木杉',NULL,NULL,'student',NULL,NULL,'13512340005',NULL,'计算机学院计科1班','2025-11-30 17:34:20'),('U202341006','U202341006','123456','王小明',NULL,NULL,'student',NULL,NULL,'13512340006',NULL,'机械学院机械1班','2025-11-30 17:34:20'),('U202341007','U202341007','123456','李雪梅',NULL,NULL,'student',NULL,NULL,'13512340007',NULL,'经管学院会计1班','2025-11-30 17:34:20'),('U202341008','U202341008','123456','张文博',NULL,NULL,'student',NULL,NULL,'13512340008',NULL,'材料学院材料1班','2025-11-30 17:34:20'),('U202341009','U202341009','123456','陈思琪',NULL,NULL,'student',NULL,NULL,'13512340009',NULL,'外国语学院英语1班','2025-11-30 17:34:20'),('U202341010','U202341010','123456','刘洋',NULL,NULL,'student',NULL,NULL,'13512340010',NULL,'土木学院土木1班','2025-11-30 17:34:20'),('U202341011','U202341011','123456','赵敏',NULL,NULL,'student',NULL,NULL,'13512340011',NULL,'计算机学院软件工程2班','2025-11-30 17:34:20'),('U202341012','U202341012','123456','孙浩然',NULL,NULL,'student',NULL,NULL,'13512340012',NULL,'自动化学院自动化2班','2025-11-30 17:34:20');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicle`
--

DROP TABLE IF EXISTS `vehicle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehicle` (
  `vehicle_id` varchar(50) NOT NULL COMMENT '车辆ID',
  `plate_no` varchar(15) NOT NULL COMMENT '车牌号',
  `model` varchar(50) NOT NULL COMMENT '品牌型号',
  `seat_num` int NOT NULL COMMENT '座位数',
  `original_value` decimal(15,2) DEFAULT NULL COMMENT '原值',
  `net_value` decimal(15,2) DEFAULT NULL COMMENT '净值',
  `depreciation_years` int DEFAULT '10' COMMENT '折旧年限',
  `purchase_date` date NOT NULL COMMENT '购置日期',
  `status` tinyint DEFAULT '1' COMMENT '状态(1正常2维修)',
  `standard_fuel` decimal(6,2) DEFAULT NULL COMMENT '基准油耗',
  `responsible_person` varchar(50) DEFAULT NULL COMMENT '责任人',
  `mile_age` int DEFAULT '0' COMMENT '当前里程',
  `version` int DEFAULT '0' COMMENT '乐观锁',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`vehicle_id`),
  UNIQUE KEY `plate_no` (`plate_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='车辆档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle`
--

LOCK TABLES `vehicle` WRITE;
/*!40000 ALTER TABLE `vehicle` DISABLE KEYS */;
INSERT INTO `vehicle` VALUES ('V-JX-2023-001','京A34567','丰田凯美瑞 2023款',5,NULL,NULL,10,'2023-05-20',1,6.20,NULL,28000,0,'2025-11-26 16:32:15','2025-11-26 16:32:15',0),('V-JX-2023-100','京A34569','丰田凯美瑞 2023款',5,NULL,NULL,10,'2023-05-20',1,6.20,NULL,28000,0,'2025-11-26 16:32:15','2025-12-02 01:39:51',1),('V-JX-2024-002','京A78901','日产天籁 2024款',5,NULL,NULL,10,'2024-04-30',1,6.80,NULL,12000,0,'2025-11-26 16:32:15','2025-11-26 16:32:15',0),('V-KY-2023-001','京A45678','本田CR-V 2023款',5,NULL,NULL,10,'2023-07-10',2,7.80,NULL,45000,0,'2025-11-26 16:32:15','2025-11-26 16:32:15',0),('V-KY-2024-002','京A89012','福特锐界 2024款',7,NULL,NULL,10,'2024-06-18',1,8.50,NULL,9000,0,'2025-11-26 16:32:15','2025-11-26 16:32:15',0),('V-TQ-2023-001','京A56789','金龙客车 2023款',35,NULL,NULL,10,'2023-09-25',1,18.50,NULL,18000,0,'2025-11-26 16:32:15','2025-11-26 16:32:15',0),('V-TQ-2024-002','京B01234','宇通客车 2024款',45,NULL,NULL,10,'2024-10-15',1,20.00,NULL,3000,0,'2025-11-26 16:32:15','2025-11-26 16:32:15',0),('V-XZ-2023-002','京A23456','大众帕萨特 2023款',5,NULL,NULL,10,'2023-03-15',1,6.50,NULL,32000,0,'2025-11-26 16:32:15','2025-11-26 16:32:15',0),('V-XZ-2024-003','京A67890','别克君越 2024款',5,NULL,NULL,10,'2024-01-12',1,7.00,NULL,15000,0,'2025-11-26 16:32:15','2025-11-26 16:32:15',0),('V-XZ-2025-001','京B12345','宝马5系 2025款',5,NULL,NULL,10,'2025-01-08',1,7.50,NULL,1500,0,'2025-11-26 16:32:15','2025-11-26 16:32:15',0),('V-YL-2024-001','京A90123','奔驰威霆 2024款',7,NULL,NULL,10,'2024-08-22',1,9.20,NULL,6000,0,'2025-11-26 16:32:15','2025-11-26 16:32:15',0);
/*!40000 ALTER TABLE `vehicle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicle_borrow`
--

DROP TABLE IF EXISTS `vehicle_borrow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehicle_borrow` (
  `borrow_id` varchar(50) NOT NULL COMMENT '借用ID',
  `vehicle_id` varchar(50) NOT NULL COMMENT '车辆ID',
  `borrower_id` varchar(50) NOT NULL COMMENT '借用者ID',
  `borrow_date` date NOT NULL COMMENT '借用日期',
  `return_date` date NOT NULL COMMENT '应还日期',
  `actual_return_date` date DEFAULT NULL COMMENT '实还日期',
  `borrow_purpose` varchar(200) DEFAULT NULL COMMENT '借用用途',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`borrow_id`),
  KEY `fk_vb_vehicle` (`vehicle_id`),
  CONSTRAINT `fk_vb_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='车辆借用表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle_borrow`
--

LOCK TABLES `vehicle_borrow` WRITE;
/*!40000 ALTER TABLE `vehicle_borrow` DISABLE KEYS */;
INSERT INTO `vehicle_borrow` VALUES ('JY202512020113030001','V-XZ-2023-002','U-GZ-2019-001','2025-11-10','2025-11-12',NULL,'公务出差','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('JY202512020115280002','V-XZ-2023-002','U-GZ-2019-001','2025-11-10','2025-11-12',NULL,'公务出1','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('JY202512020115300003','V-XZ-2023-002','U-GZ-2019-001','2025-11-10','2025-11-12',NULL,'公务出1','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('JY202512020115310004','V-XZ-2023-002','U-GZ-2019-001','2025-11-10','2025-11-12',NULL,'公务出1','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('VB-2025-002','V-XZ-2023-002','U-GZ-2019-001','2025-11-10','2025-11-12',NULL,'公务出差','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('VB-2025-003','V-JX-2023-001','U-JW-2020-002','2025-11-08','2025-11-10','2025-11-10','教学实践','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('VB-2025-004','V-KY-2024-002','U-SY-2018-003','2025-11-12','2025-11-15',NULL,'科研采样','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('VB-2025-005','V-TQ-2023-001','U-HZ-2021-004','2025-11-05','2025-11-07','2025-11-07','学生实习','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('VB-2025-006','V-XZ-2024-003','U-RS-2017-005','2025-11-15','2025-11-18',NULL,'招聘宣讲','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('VB-2025-007','V-JX-2024-002','U-CW-2018-006','2025-11-03','2025-11-05','2025-11-05','教材采购','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('VB-2025-008','V-YL-2024-001','U-YY-2023-007','2025-11-18','2025-11-19',NULL,'医疗急救演练','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('VB-2025-009','V-TQ-2024-002','U-JW-2022-008','2025-11-01','2025-11-03','2025-11-03','学术会议接送','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('VB-2025-010','V-XZ-2025-001','U-GZ-2021-009','2025-11-20','2025-11-22',NULL,'校际交流','2025-11-26 16:32:53','2025-11-26 16:32:53',0),('VB-2025-011','V-KY-2023-001','U-SY-2020-010','2025-11-06','2025-11-08','2025-11-08','实验设备运输','2025-11-26 16:32:53','2025-11-26 16:32:53',0);
/*!40000 ALTER TABLE `vehicle_borrow` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-02  3:30:14
