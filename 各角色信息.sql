-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: rm-2ze5g00fo0zy6956emo.mysql.rds.aliyuncs.com    Database: teacher_manage
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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'c30a24de-ba39-11f0-aae3-7c8c09be377a:1-437713';

--
-- Table structure for table `teacher_user`
--

DROP TABLE IF EXISTS `teacher_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_user` (
  `teacher_user_id` int NOT NULL AUTO_INCREMENT COMMENT '教师用户ID',
  `teachers_name` varchar(64) DEFAULT NULL COMMENT '教师姓名',
  `gender_of_teachers` varchar(64) DEFAULT NULL COMMENT '教师性别',
  `teachers_telephone` varchar(16) DEFAULT NULL COMMENT '教师电话',
  `political_outlook` varchar(64) DEFAULT NULL COMMENT '政治面貌',
  `user_id_number` varchar(255) DEFAULT NULL COMMENT '身份证号',
  `examine_state` varchar(16) NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `user_id` int NOT NULL DEFAULT '0' COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` int NOT NULL DEFAULT '1' COMMENT '创建用户ID',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`teacher_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教师用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_user`
--

LOCK TABLES `teacher_user` WRITE;
/*!40000 ALTER TABLE `teacher_user` DISABLE KEYS */;
INSERT INTO `teacher_user` VALUES (1,'李娜','教师性别1','15812345678','致公党党员','583-93-8355','已通过',2,'2025-11-26 16:39:57',1,'2025-11-26 08:39:57'),(2,'王芳','教师性别2','13698765432','民革会员','691-73-7922','已通过',3,'2025-11-26 16:39:57',1,'2025-11-26 08:39:57'),(3,'陈静','教师性别3','13377889900','民建会员','346-75-2917','已通过',4,'2025-11-26 16:39:57',1,'2025-11-26 08:39:57'),(4,'王老师','女','13994003726','中共党员','141023200511060017','已通过',8,'2025-11-26 21:15:12',1,'2025-11-26 13:15:12'),(5,'张三','男','15704900850','群众','152723198812040223','已通过',9,'2025-11-26 21:52:18',1,'2025-11-26 13:52:18'),(6,'李四','男','13823247878','党员','152723198810012310','已通过',10,'2025-11-26 22:27:41',1,'2025-11-26 14:27:41'),(7,'黎明','男','15023456789','党员','152723199404040321','已通过',11,'2025-11-27 08:13:11',1,'2025-11-27 00:13:11'),(8,'张明','男','13623456789','中共党员','152723198902030405','已通过',12,'2025-11-27 08:23:55',1,'2025-11-27 00:23:55');
/*!40000 ALTER TABLE `teacher_user` ENABLE KEYS */;
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

-- Dump completed on 2025-11-30 17:07:00
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: rm-2ze5g00fo0zy6956emo.mysql.rds.aliyuncs.com    Database: logistics_manage
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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'c30a24de-ba39-11f0-aae3-7c8c09be377a:1-437713';

--
-- Table structure for table `employee_manage`
--

DROP TABLE IF EXISTS `employee_manage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_manage` (
  `employee_id` varchar(45) NOT NULL,
  `employee_name` varchar(45) NOT NULL,
  `employee_age` int NOT NULL,
  `employee_affiliated_united` varchar(45) NOT NULL,
  `employee_responsibility` varchar(45) NOT NULL,
  `employee_attendance_days` int NOT NULL,
  `length_of_service` int NOT NULL,
  `joining_date` date NOT NULL,
  `basic_salary` int NOT NULL,
  `performance_salary` int NOT NULL,
  `subsidy_amount` int NOT NULL,
  `deduct_amount` int NOT NULL,
  `net_salary` int NOT NULL,
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `employee_id_UNIQUE` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee_manage`
--

LOCK TABLES `employee_manage` WRITE;
/*!40000 ALTER TABLE `employee_manage` DISABLE KEYS */;
INSERT INTO `employee_manage` VALUES ('AB201108010001','胡丹',35,'安保部门','监控人员出入',30,14,'2011-08-01',5500,1000,500,0,7000),('CL201806010001','白肖',32,'车辆部门','管控校园车辆',28,7,'2018-06-01',5000,2000,1000,500,7500),('CY201906010001','王浩',48,'餐饮部门','万秀园打饭人员',28,6,'2019-06-01',6000,1000,500,100,7400),('EMP2025001','张三',32,'后勤部门','食堂管理员',22,5,'2020-03-15',3500,1200,300,100,4900),('EMP2025002','李四',28,'宿管部门','宿舍管理员',21,3,'2022-05-20',3200,1000,200,50,4350),('EMP2025003','王五',40,'维修部门','设施维修工',23,8,'2017-08-10',4000,1500,400,0,5900),('EMP2025004','赵六',35,'清洁部门','保洁员',22,6,'2019-02-25',3000,800,200,50,3950),('EMP2025005','孙七',26,'行政部门','行政专员',20,2,'2023-07-01',3800,1300,300,200,5200),('EMP2025006','周八',38,'食堂部门','厨师',23,7,'2018-04-12',4200,1800,500,0,6500),('EMP2025007','吴九',30,'宿管部门','宿舍安全员',21,4,'2021-09-05',3300,1100,200,100,4500),('EMP2025008','郑十',29,'清洁部门','保洁领班',22,3,'2022-03-18',3400,1200,300,50,4850),('EMP2025009','钱一',45,'维修部门','维修主管',23,10,'2015-06-20',4500,2000,600,0,7100),('EMP2025010','孙二',33,'后勤部门','物资管理员',21,5,'2020-08-15',3600,1300,300,100,4900),('EMP2025011','李三',27,'行政部门','人事专员',20,2,'2023-09-01',3700,1200,300,150,4950),('EMP2025012','王四',31,'食堂部门','服务员',22,4,'2021-02-10',3000,900,200,50,4050),('EMP2025013','赵五',36,'维修部门','电工',23,6,'2019-05-20',3800,1400,400,0,5600),('EMP2025014','孙六',28,'清洁部门','保洁员',21,3,'2022-07-15',3000,800,200,50,3950),('EMP2025015','周七',34,'宿管部门','宿舍管理员',22,5,'2020-04-20',3300,1100,200,100,4500),('EMP2025016','吴八',39,'食堂部门','面点师',23,7,'2018-03-10',4000,1600,500,0,6100),('EMP2025017','郑九',32,'后勤部门','后勤主管',22,6,'2019-07-25',4200,1800,600,100,6500),('EMP2025018','钱十',29,'行政部门','行政助理',20,2,'2023-08-01',3500,1000,200,50,4650),('EMP2025019','李一',42,'维修部门','管道工',23,9,'2016-09-15',3900,1500,400,0,5800),('EMP2025020','王二',30,'食堂部门','采购员',21,4,'2021-05-10',3700,1200,300,100,5100),('NY201501010001','张伟',45,'能源部门','监测5斋用水量',29,10,'2015-01-01',6000,1000,1000,200,8800),('NY201501010002','王敏',40,'能源部门','监测5斋用电量',30,10,'2025-01-01',6000,1000,500,0,7500),('WY200011010001','杨力',60,'物业部门','处理1斋学生报修',30,25,'2000-11-01',7000,1500,1000,0,8600),('WY202010010001','陈力',30,'物业部门','处理5斋学生报修',30,5,'2020-10-01',5000,1000,500,0,6500),('ZS202201010001','郑晨',37,'住宿部门','管理床位分配',30,3,'2022-01-01',5500,2000,0,200,7300);
/*!40000 ALTER TABLE `employee_manage` ENABLE KEYS */;
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

-- Dump completed on 2025-11-30 17:07:00
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: rm-2ze5g00fo0zy6956emo.mysql.rds.aliyuncs.com    Database: finance_manage
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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'c30a24de-ba39-11f0-aae3-7c8c09be377a:1-437713';

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` varchar(20) NOT NULL COMMENT '用户ID（学号/工号），主码',
  `name` varchar(20) NOT NULL COMMENT '用户姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '用户手机号',
  `id_card` varchar(30) DEFAULT NULL COMMENT '用户身份证号',
  `bank_card` varchar(30) NOT NULL COMMENT '用户银行卡号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户基础信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('1','rymfather','14525418666','','0000000000000000','2025-11-28 10:57:09'),('111','11','11','11','11','2025-11-29 11:53:09'),('A2023001','张伟','13955667788','440101198808105678','6217000200089012345','2023-09-02 10:00:00'),('A2023002','刘芳','13722334455','330101199202182345','6214880100056789012','2023-09-05 09:45:00'),('M1','物资管理','1',NULL,'1','2025-11-20 15:48:13'),('M2','固定资产管理','2',NULL,'2','2025-11-20 15:48:13'),('M3','实验室管理','3',NULL,'3','2025-11-20 15:48:13'),('M4','科研管理','4',NULL,'4','2025-11-20 15:48:13'),('M5','图书馆管理','5',NULL,'5','2025-11-20 15:48:13'),('M6','校医院诊疗管理','6',NULL,'6','2025-11-20 15:48:13'),('M7','研究生管理','7',NULL,'7','2025-11-20 15:48:13'),('M8','后勤集团管理','8',NULL,'8','2025-11-20 15:48:13'),('M9','对外交流管理','9',NULL,'9','2025-11-20 15:48:13'),('T2023001','陈宏宇','15701095067','110101198503156789','6222020200012345678','2023-09-01 09:00:00'),('T2023002','李梦琪','13811223344','310101199005201234','6226660100067890123','2023-09-01 09:15:00'),('T2023003','王建国','15099887766','120101197911058901','6228480010098765432','2023-09-03 14:30:00'),('U2023004','李四','15701095064','110101198503156784','6222020200012345674','2025-11-18 17:52:57'),('U2023005','王五','15701095065','110101198503156785','6222020200012345675','2025-11-18 17:52:57'),('U2023006','陈六','15701095066','110101198503156786','6222020200012345676','2025-11-18 17:52:57'),('U2023007','赵七','15701095068','110101198503156787','6222020200012345677','2025-11-18 17:52:57'),('U2023099','陈宏宇','15701095064','110101198503156784','6222020200012345674','2025-11-27 13:49:39'),('U2023111','张三','11111111111','120101197911058902','6228480010098765433','2023-09-04 14:30:00'),('U2023222','陈宏宇','15701095064','110101198503156784','6222020200012345674','2025-11-27 13:51:25'),('U202341221','z','','','','2025-11-29 19:53:24'),('U202341440','zhm','1556156','15185','215612','2025-11-26 22:58:14');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
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

-- Dump completed on 2025-11-30 17:07:00
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: rm-2ze5g00fo0zy6956emo.mysql.rds.aliyuncs.com    Database: laboratory_manage
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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'c30a24de-ba39-11f0-aae3-7c8c09be377a:1-437713';

--
-- Table structure for table `lm_employee`
--

DROP TABLE IF EXISTS `lm_employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lm_employee` (
  `lm_employee_id` varchar(10) NOT NULL COMMENT '实验室工号（格式：1位类型码（G=教师，S，=学生，L=实验室，D=设备，C=耗材，X=行政）+4位入职年份+5位序号，共10位）',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '员工状态：1=在职，0=请假',
  `role` tinyint NOT NULL DEFAULT '0' COMMENT '员工角色：0=实验室管理员，1=设备管理员，2=耗材管理员，3=学生，4=教师，5=维修人员，6=文职',
  PRIMARY KEY (`lm_employee_id`),
  KEY `idx_applicant_id` (`lm_employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='实验室员工信息表（含教师、行政人员，用于登录和权限管理）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lm_employee`
--

LOCK TABLES `lm_employee` WRITE;
/*!40000 ALTER TABLE `lm_employee` DISABLE KEYS */;
INSERT INTO `lm_employee` VALUES ('C202500003','password123',1,2),('D202500002','password123',1,1),('D202500009','password123',1,5),('G202500005','password123',1,4),('G202500008','password123',1,4),('L202500001','password123',1,0),('L202500006','password123',0,0),('L202510001','password123',1,0),('L202510002','password123',1,1),('L202510003','password123',1,2),('L20251004','password123',1,3),('L20251005','password123',1,4),('L20251006','password123',0,0),('L20251007','password123',1,6),('L20251008','password123',1,4),('L20251009','password123',1,5),('L20251010','password123',1,3),('S202500004','password123',1,3),('S202500010','password123',1,3),('X202500007','password123',1,6);
/*!40000 ALTER TABLE `lm_employee` ENABLE KEYS */;
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

-- Dump completed on 2025-11-30 17:07:01
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: rm-2ze5g00fo0zy6956emo.mysql.rds.aliyuncs.com    Database: studentstatus_manage
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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'c30a24de-ba39-11f0-aae3-7c8c09be377a:1-437713';

--
-- Table structure for table `student_basic_info`
--

DROP TABLE IF EXISTS `student_basic_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_basic_info` (
  `stu_id` char(10) NOT NULL COMMENT '学号',
  `name` varchar(10) NOT NULL COMMENT '姓名',
  `gender` enum('男','女') NOT NULL COMMENT '性别',
  `id_card` char(18) NOT NULL COMMENT '身份证',
  `birth_date` date NOT NULL COMMENT '出生日期',
  `nationality` varchar(10) NOT NULL COMMENT '国籍',
  `nation` varchar(10) NOT NULL COMMENT '民族',
  `native_place` varchar(30) NOT NULL COMMENT '籍贯',
  `political_status` enum('群众','共青团员','中共党员','中共预备党员') NOT NULL COMMENT '政治面貌',
  `phone` char(11) NOT NULL COMMENT '手机号码',
  PRIMARY KEY (`stu_id`) USING BTREE,
  UNIQUE KEY `id_card` (`id_card`) USING BTREE,
  UNIQUE KEY `phone` (`phone`) USING BTREE,
  CONSTRAINT `student_basic_info_chk_1` CHECK ((`stu_id` like _utf8mb4'U_________'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_basic_info`
--

LOCK TABLES `student_basic_info` WRITE;
/*!40000 ALTER TABLE `student_basic_info` DISABLE KEYS */;
INSERT INTO `student_basic_info` VALUES ('U202341001','大雄','男','110101200501011597','2005-01-01','中国','汉族','北京市北京市辖区东城区','共青团员','13512340001'),('U202341002','静香','女','110102200506015127','2005-06-01','中国','汉族','北京市北京市辖区西城区','群众','13512340002'),('U202341003','小夫','男','110105200508012345','2005-08-01','中国','汉族','北京市北京市辖区朝阳区','共青团员','13512340003'),('U202341004','胖虎','男','110106200510015678','2005-10-01','中国','汉族','北京市北京市辖区丰台区','群众','13512340004'),('U202341005','出木杉','男','110107200512013456','2005-12-01','中国','汉族','北京市北京市辖区石景山区','共青团员','13512340005'),('U202341006','王小明','男','320102200503156789','2005-03-15','中国','汉族','江苏省南京市玄武区','共青团员','13512340006'),('U202341007','李雪梅','女','440103200507228901','2005-07-22','中国','汉族','广东省广州市荔湾区','共青团员','13512340007'),('U202341008','张文博','男','510104200509103456','2005-09-10','中国','汉族','四川省成都市锦江区','中共预备党员','13512340008'),('U202341009','陈思琪','女','330105200502147890','2005-02-14','中国','汉族','浙江省杭州市拱墅区','共青团员','13512340009'),('U202341010','刘洋','男','370102200511052345','2005-11-05','中国','回族','山东省济南市历下区','群众','13512340010'),('U202341011','赵敏','女','420103200504186789','2005-04-18','中国','土家族','湖北省武汉市江汉区','共青团员','13512340011'),('U202341012','孙浩然','男','610104200506291234','2005-06-29','中国','汉族','陕西省西安市莲湖区','群众','13512340012');
/*!40000 ALTER TABLE `student_basic_info` ENABLE KEYS */;
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

-- Dump completed on 2025-11-30 17:07:01
