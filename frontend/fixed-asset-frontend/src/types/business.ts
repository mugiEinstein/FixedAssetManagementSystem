// 业务相关类型定义

// 器材校外借用
export interface ExternalEquipmentBorrow {
    borrowId: string
    equipmentId: string
    equipmentName?: string
    applicantUnit: string
    contactPerson: string
    contactPhone: string
    businessLicense?: string
    borrowDays: number
    purpose: string
    startDate?: string
    endDate?: string
    actualReturnDate?: string
    depositAmount: number
    rentAmount: number
    depositPaid: boolean
    penaltyFee?: number
    status: BorrowStatus
    qualificationApproval: ApprovalStatus
    equipmentApproval: ApprovalStatus
    createdAt: string
}

// 器材校内借用
export interface InternalEquipmentBorrow {
    borrowId: string
    equipmentId: string
    equipmentName?: string
    userId: string
    applicantName: string
    department: string
    userType: 'student' | 'teacher' | 'staff'
    borrowDate: string
    returnDate: string
    actualReturnDate?: string
    purpose: string
    penaltyFee?: number
    compensationFee?: number  // 赔偿金
    status: BorrowStatus
    permissionApproval: ApprovalStatus
    equipmentApproval: ApprovalStatus
    createdAt: string
}

export type BorrowStatus =
    | 'pending'     // 待审核
    | 'approved'    // 已批准
    | 'rejected'    // 已驳回
    | 'in_use'      // 使用中
    | 'returned'    // 已归还
    | 'overdue'     // 已逾期
    | 'need_repair' // 需维修

export type ApprovalStatus = 'pending' | 'approved' | 'rejected'

// 办公用房分配
export interface OfficeRoomAllocation {
    allocationId: string
    userId: string
    applicantName: string
    department: string
    staffLevel: string
    baseMinArea: number
    baseMaxArea: number
    actualArea: number
    roomId?: string
    roomName?: string
    excessArea: number
    excessFee: number
    status: ApprovalStatus
    departmentApproval: ApprovalStatus
    hrApproval: ApprovalStatus
    createdAt: string
}

// 教室借用
export interface ClassroomBorrow {
    borrowId: string
    roomId: string
    roomName?: string
    applicantDept: string
    applicantName: string
    purpose: string
    borrowDate: string
    startTime: string
    endTime: string
    expectedAttendees: number
    equipmentNeeds: string
    status: ApprovalStatus
    departmentApproval: ApprovalStatus
    academicApproval: ApprovalStatus
    createdAt: string
}

// 学生住宿
export interface StudentHousing {
    allocationId: string
    studentId: string
    studentName: string
    gender: 'male' | 'female'
    college: string
    major: string
    grade: string
    dormId: string
    dormName?: string
    bedNumber: number
    checkInDate: string
    checkOutDate?: string
    status: 'active' | 'transferred' | 'exited'
}

// 教职工住宿
export interface StaffHousing {
    allocationId: string
    staffId: string
    staffName: string
    department: string
    roomType: string
    area: number
    rentAmount: number
    dormId?: string
    contractStart?: string
    contractEnd?: string
    status: ApprovalStatus
    qualificationApproval: ApprovalStatus
    financeApproval: ApprovalStatus
    createdAt: string
}

// 校外活动
export interface ExternalEvent {
    eventId: string
    organizer: string
    contactPerson: string
    contactPhone: string
    eventName: string
    eventDescription: string
    venueId: string
    venueName?: string
    expectedAttendees: number
    eventDate: string
    startTime: string
    endTime: string
    duration: number
    venueRent: number
    equipmentFee: number
    securityFee: number
    totalFee: number
    depositPaid: boolean
    status: ApprovalStatus
    qualificationApproval: ApprovalStatus
    venueApproval: ApprovalStatus
    createdAt: string
}

// 校内活动
export interface InternalEvent {
    eventId: string
    organizerDept: string
    applicantName: string
    eventName: string
    venueId: string
    venueName?: string
    expectedAttendees: number
    eventDate: string
    startTime: string
    endTime: string
    duration: number
    venueRent: number
    discountFee: number
    status: ApprovalStatus
    departmentApproval: ApprovalStatus
    createdAt: string
}

// 维保记录
export interface MaintenanceRecord {
    maintenanceId: string
    assetType: 'building' | 'equipment' | 'vehicle'
    assetId: string
    assetName?: string
    reporterName: string
    reporterDept: string
    faultDescription: string
    reportDate: string
    assignedTo?: string
    repairDate?: string
    repairResult?: string
    repairCost?: number
    status: 'pending' | 'assigned' | 'in_progress' | 'completed'
    createdAt: string
}

// 盘点记录
export interface InventoryRecord {
    inventoryId: string
    planId: string
    assetId: string
    assetType: string
    assetName: string
    inventoryDate: string
    physicalStatus: 'normal' | 'surplus' | 'loss' | 'damaged'
    differenceReason?: string
    assessedValue: number
    depreciation: number
    netValue: number
    valueAdjustment?: number
    inventoryStatus: 'pending' | 'completed'
}

// 费用记录
export interface FeeRecord {
    feeId: string
    feeType: string
    businessType: string
    businessId: string
    payerName: string
    payerType: string
    amount: number
    status: 'pending' | 'paid' | 'refunded' | 'partial_refund'
    paymentDate?: string
    refundAmount?: number
    refundDate?: string
    createdAt: string
}