import request from './index'
import type { PageParams, PageResult } from '@/types/api'

// ==================== 学生住宿 ====================

export interface StudentDormAlloc {
    allocId: string
    studentId: string
    studentName: string
    studentNo: string
    department: string
    grade: string
    gender: string
    buildingId: string
    buildingName: string
    roomNo: string
    bedNo: string
    checkInDate: string
    checkOutDate?: string
    status: string
    createdAt: string
}

// 获取学生住宿列表
export function getStudentHousingList(params: PageParams) {
    return request.get<any, { data: PageResult<StudentDormAlloc> }>(
        '/student-housing',
        { params }
    )
}

// 学生入住
export function checkInStudent(data: Partial<StudentDormAlloc>) {
    return request.post('/student-housing/check-in', data)
}

// 学生退宿
export function checkOutStudent(id: string, data: { reason?: string }) {
    return request.post(`/student-housing/${id}/check-out`, data)
}

// 宿舍调换
export function changeDormitory(id: string, data: { newBuildingId: string; newRoomNo: string; newBedNo: string; reason?: string }) {
    return request.post(`/student-housing/${id}/change`, data)
}

// ==================== 教职工住宿 ====================

export interface FacultyDormAlloc {
    allocId: string
    staffId: string
    staffName: string
    staffNo: string
    department: string
    title: string
    buildingId: string
    buildingName: string
    roomNo: string
    roomType: string
    rentAmount: number
    checkInDate: string
    checkOutDate?: string
    contractEndDate?: string
    status: string
    qualificationApproval: string
    financeApproval: string
    createdAt: string
}

// 获取教职工住宿列表
export function getStaffHousingList(params: PageParams) {
    return request.get<any, { data: PageResult<FacultyDormAlloc> }>(
        '/staff-housing',
        { params }
    )
}

// 提交住宿申请
export function submitStaffHousingApplication(data: Partial<FacultyDormAlloc>) {
    return request.post('/staff-housing/applications', data)
}

// 资格审核
export function reviewStaffQualification(id: string, data: { approved: boolean; reason?: string }) {
    return request.post(`/staff-housing/applications/${id}/qualification-review`, data)
}

// 财务审核
export function reviewStaffFinance(id: string, data: { approved: boolean; reason?: string }) {
    return request.post(`/staff-housing/applications/${id}/finance-review`, data)
}

// 分配房源
export function allocateStaffRoom(id: string, data: { buildingId: string; roomNo: string }) {
    return request.post(`/staff-housing/applications/${id}/allocate`, data)
}

// 签订合同
export function signStaffContract(id: string, data: { contractEndDate: string }) {
    return request.post(`/staff-housing/applications/${id}/sign-contract`, data)
}

// ==================== 学生住宿申请 ====================

// 获取我的住宿信息
export function getMyHousingInfo() {
    return request.get('/student-housing/my-info')
}

// 获取室友信息
export function getRoommateInfo(dormId: string) {
    return request.get(`/student-housing/roommates/${dormId}`)
}

// 申请调换宿舍
export function applyDormTransfer(data: { reasonType: string; reason: string; preferredDorm?: string }) {
    return request.post('/student-housing/transfer-apply', data)
}

// 申请外宿
export function applyLeave(data: {
    leaveType: string
    startDate: string
    endDate: string
    reason: string
    description?: string
    address: string
    emergencyContact: string
}) {
    return request.post('/student-housing/leave-apply', data)
}

// 申请退宿
export function applyCheckout(data: {
    reason: string
    exitDate: string
    description?: string
    phone: string
}) {
    return request.post('/student-housing/checkout-apply', data)
}

// 获取待审核的学生住宿申请列表
export function getPendingHousingApplications(params: PageParams) {
    return request.get<any, { data: PageResult<StudentHousingApplication> }>(
        '/student-housing/applications',
        { params }
    )
}

// 审核学生住宿申请
export function reviewHousingApplication(id: string, data: { approved: boolean; reason?: string }) {
    return request.post(`/student-housing/applications/${id}/review`, data)
}

// 学生住宿申请类型
export interface StudentHousingApplication {
    applicationId: string
    studentNo: string
    studentName: string
    department: string
    grade: string
    applicationType: 'transfer' | 'leave' | 'checkout'  // 调换/外宿/退宿
    currentDorm?: string
    reason: string
    reasonDetail?: string
    preferredDorm?: string
    leaveStartDate?: string
    leaveEndDate?: string
    exitDate?: string
    status: 'pending' | 'approved' | 'rejected'
    applyTime: string
    reviewTime?: string
    reviewer?: string
    reviewReason?: string
}
