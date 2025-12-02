// 资产相关类型定义（匹配数据库表结构）

// 建筑档案 (building_archive 表)
export interface Building {
    buildingId: string           // building_id VARCHAR(20)
    buildingName: string         // building_name VARCHAR(50)
    buildingType: string         // building_type VARCHAR(50)
    address?: string             // address VARCHAR(200)
    area?: number                // area DECIMAL(10,2)
    totalArea?: number           // 兼容前端使用的别名
    floors?: number              // 前端扩展字段
    buildYear?: number           // build_year INT
    originalValue?: number       // original_value DECIMAL(15,2)
    netValue?: number            // net_value DECIMAL(15,2)
    depreciationYears?: number   // depreciation_years INT
    facilities?: string          // facilities VARCHAR(500) JSON
    propertyOwner?: string       // 前端扩展字段
    status: string               // status VARCHAR(20) 默认'正常'
    createdAt?: string           // created_at DATETIME
    updatedAt?: string           // updated_at DATETIME
    isDeleted?: number           // is_deleted TINYINT
}

export type BuildingType =
    | 'teaching'    // 教学用房
    | 'office'      // 办公用房
    | 'living'      // 生活用房
    | 'other'       // 其他用房

// 房间资源 (room_resource 表)
export interface Room {
    roomId: string               // room_id VARCHAR(20)
    buildingId: string           // building_id VARCHAR(20)
    buildingName?: string        // 关联查询字段
    roomNumber: string           // room_number VARCHAR(20)
    roomName?: string            // room_name VARCHAR(100)
    roomType?: string            // room_type VARCHAR(50)
    usageType?: string           // usage_type VARCHAR(50)
    floor?: number               // floor INT
    area?: number                // area DECIMAL(8,2)
    capacity?: number            // capacity INT
    equipment?: string           // equipment VARCHAR(500) JSON
    currentUser?: string         // currentuser VARCHAR(100)
    hourlyRate?: number          // hourly_rate DECIMAL(10,2)
    status: string               // status VARCHAR(20) 默认'IDLE'
    version?: number             // version INT
    createdAt?: string           // created_at DATETIME
    updatedAt?: string           // updated_at DATETIME
}

export type RoomType =
    | 'classroom'   // 教室
    | 'office'      // 办公室
    | 'dormitory'   // 宿舍
    | 'lab'         // 实验室
    | 'meeting'     // 会议室
    | 'venue'       // 活动场地

export type RoomStatus = 'IDLE' | 'IN_USE' | 'MAINTENANCE' | 'RESERVED'

// 器材档案 (equipment_archive 表)
export interface Equipment {
    equipmentId: string          // equipment_id VARCHAR(50)
    equipmentName: string        // equipment_name VARCHAR(100)
    equipmentType: string        // equipment_type VARCHAR(50)
    model?: string               // model VARCHAR(100)
    manufacturer?: string        // manufacturer VARCHAR(100)
    location?: string            // location VARCHAR(100)
    responsiblePerson?: string   // responsible_person VARCHAR(50)
    originalValue: number        // original_value DECIMAL(15,2)
    netValue?: number            // net_value DECIMAL(15,2)
    purchaseDate: string         // purchase_date DATE
    depreciationYears: number    // depreciation_years INT
    dailyRent?: number           // daily_rent DECIMAL(10,2)
    maintenanceCycle?: string    // maintenance_cycle VARCHAR(20)
    storageRequirements?: string // storage_requirements VARCHAR(200)
    status: string               // status VARCHAR(20) 默认'IDLE'
    version?: number             // version INT
    createdAt?: string           // created_at DATETIME
    updatedAt?: string           // updated_at DATETIME
}

export type EquipmentType = 'TY' | 'DZ' // 通用/电子

// 车辆档案 (vehicle 表)
export interface Vehicle {
    vehicleId: string            // vehicle_id VARCHAR(50)
    plateNo: string              // plate_no VARCHAR(15)
    model: string                // model VARCHAR(50)
    seatNum: number              // seat_num INT
    originalValue?: number       // original_value DECIMAL(15,2)
    netValue?: number            // net_value DECIMAL(15,2)
    depreciationYears?: number   // depreciation_years INT
    purchaseDate: string         // purchase_date DATE
    status: number               // status TINYINT 默认1(1正常2维修)
    standardFuel?: number        // standard_fuel DECIMAL(6,2)
    responsiblePerson?: string   // responsible_person VARCHAR(50)
    mileAge?: number             // mile_age INT
    version?: number             // version INT
    createdAt?: string           // created_at DATETIME
    updatedAt?: string           // updated_at DATETIME
}

// 资产状态（前端通用）
export type AssetStatus =
    | 'normal'      // 正常
    | 'IN_USE'      // 在用
    | 'IDLE'        // 闲置
    | 'MAINTENANCE' // 维修中
    | 'scrapped'    // 已报废
    | '正常'        // 中文状态
    | '维修中'      // 中文状态