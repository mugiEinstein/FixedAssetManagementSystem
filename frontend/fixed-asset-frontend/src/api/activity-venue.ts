import request from './index'
import type { PageParams, PageResult } from '@/types/api'

export interface ExternalEventBooking {
    eventId: string
    organizer: string
    venueId: string
    venueName?: string
    contactPerson?: string
    contactPhone?: string
    eventName?: string
    eventDescription?: string
    eventDate: string
    startTime?: string
    endTime?: string
    expectedAttendees?: number
    duration?: number
    venueRent?: number
    equipmentFee?: number
    securityFee?: number
    totalFee?: number
    depositPaid?: number
    status: string
    qualificationApproval?: string
    venueApproval?: string
    createdAt: string
}

export interface InternalEventBooking {
    bookingId: string
    venueId: string
    venueName: string
    applicantDept: string
    applicantName: string
    eventName: string
    eventType: string
    eventDate: string
    startTime: string
    endTime: string
    expectedAttendees: number
    equipmentNeeds: string
    status: string
    createdAt: string
}

// ==================== 校外活动 ====================

export function getExternalEventList(params: PageParams) {
    return request.get<any, { data: PageResult<ExternalEventBooking> }>(
        '/activity-venue/external',
        { params }
    )
}

export function submitExternalEvent(data: Partial<ExternalEventBooking>) {
    return request.post('/activity-venue/external', data)
}

export function reviewExternalQualification(id: string, data: { approved: boolean; reason?: string }) {
    return request.post(`/activity-venue/external/${id}/qualification-review`, data)
}

export function reviewExternalVenue(id: string, data: { approved: boolean; reason?: string }) {
    return request.post(`/activity-venue/external/${id}/venue-review`, data)
}

export function confirmExternalPayment(id: string) {
    return request.post(`/activity-venue/external/${id}/confirm-payment`)
}

export function signExternalContract(id: string) {
    return request.post(`/activity-venue/external/${id}/sign-contract`)
}

// ==================== 校内活动 ====================

export function getInternalEventList(params: PageParams) {
    return request.get<any, { data: PageResult<InternalEventBooking> }>(
        '/activity-venue/internal',
        { params }
    )
}

export function submitInternalEvent(data: Partial<InternalEventBooking>) {
    return request.post('/activity-venue/internal', data)
}

export function reviewInternalEvent(id: string, data: { approved: boolean; reason?: string }) {
    return request.post(`/activity-venue/internal/${id}/review`, data)
}

export function confirmInternalVenue(id: string) {
    return request.post(`/activity-venue/internal/${id}/confirm`)
}

// ==================== 场地查询 ====================

export function getIdleVenues(params: { date: string; timeSlot?: string }) {
    return request.get<any, { data: any[] }>(
        '/activity-venue/idle-venues',
        { params }
    )
}

// ==================== 安保记录 ====================

export interface SecurityTask {
    taskId: string
    activityName: string
    venue: string
    activityDate: string
    activityTime: string
    expectedPeople: number
    securityStaff: number
    leader?: string
    assignedStaff?: string[]
    securityPoints?: string
    status: 'pending' | 'ongoing' | 'completed'
    createdAt: string
}

export interface PatrolRecord {
    patrolId: string
    patrolArea: string
    patrolTime: string
    patroller: string
    situation: string
    issues?: string
    createdAt: string
}

export interface StaffSchedule {
    name: string
    monday: string
    tuesday: string
    wednesday: string
    thursday: string
    friday: string
    saturday: string
    sunday: string
}

// 获取安保任务列表
export function getSecurityTaskList(params: PageParams) {
    return request.get<any, { data: PageResult<SecurityTask> }>(
        '/activity-venue/security/tasks',
        { params }
    )
}

// 创建安保任务
export function createSecurityTask(data: Partial<SecurityTask>) {
    return request.post('/activity-venue/security/tasks', data)
}

// 更新安保任务
export function updateSecurityTask(id: string, data: Partial<SecurityTask>) {
    return request.put(`/activity-venue/security/tasks/${id}`, data)
}

// 分配安保人员
export function assignSecurityStaff(id: string, data: { staffList: string[]; leader: string }) {
    return request.post(`/activity-venue/security/tasks/${id}/assign`, data)
}

// 开始执行任务
export function startSecurityTask(id: string) {
    return request.post(`/activity-venue/security/tasks/${id}/start`)
}

// 完成任务
export function completeSecurityTask(id: string, data: { report: string; issues?: string }) {
    return request.post(`/activity-venue/security/tasks/${id}/complete`, data)
}

// 获取巡逻记录
export function getPatrolRecords(params: PageParams) {
    return request.get<any, { data: PageResult<PatrolRecord> }>(
        '/activity-venue/security/patrols',
        { params }
    )
}

// 添加巡逻记录
export function createPatrolRecord(data: Partial<PatrolRecord>) {
    return request.post('/activity-venue/security/patrols', data)
}

// 获取排班表
export function getStaffSchedule(params: { week: string }) {
    return request.get<any, { data: StaffSchedule[] }>(
        '/activity-venue/security/schedule',
        { params }
    )
}

// 更新排班表
export function updateStaffSchedule(data: StaffSchedule[]) {
    return request.put('/activity-venue/security/schedule', data)
}
