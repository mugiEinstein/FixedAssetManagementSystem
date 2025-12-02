-- 更新教职工住宿表，添加审核相关字段
-- 注意：如果某字段已存在会报错，请根据实际情况注释掉已存在的字段

-- 只添加缺失的字段（逐个执行，忽略已存在的字段报错）
ALTER TABLE `faculty_dorm_alloc` ADD COLUMN `status` varchar(20) DEFAULT 'pending' COMMENT '状态: pending/approved/rejected';
ALTER TABLE `faculty_dorm_alloc` ADD COLUMN `qualification_approval` varchar(20) DEFAULT 'pending' COMMENT '资格审核状态';
ALTER TABLE `faculty_dorm_alloc` ADD COLUMN `finance_approval` varchar(20) DEFAULT 'pending' COMMENT '财务审核状态';

-- 更新现有数据的状态
UPDATE `faculty_dorm_alloc` SET `status` = 'approved', `qualification_approval` = 'approved', `finance_approval` = 'approved' WHERE `status` IS NULL OR `status` = '';
