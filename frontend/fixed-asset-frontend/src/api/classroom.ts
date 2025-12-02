import request from './index'
import type { PageParams, PageResult } from '@/types/api'

export interface ClassroomBorrow {
    borrowId: string
    roomId: string
    roomName: string
    applicantDept: string
    applicantName: string
    purpose: string
    borrowDate: string
    startTime: string
    endTime: string
    expectedAttendees: number
    equipmentNeeds: string
    status: string
    departmentApproval: string
    academicApproval: string
    createdAt: string
}

export interface ClassroomOccupancy {
    roomId: string
    roomName: string
    building: string
    floor: number
    capacity: number
    area: number
    equipment: string
    isAvailable: boolean
}

// 获取教室借用列表
export function getClassroomBorrowList(params: PageParams) {
    return request.get<any, { data: PageResult<ClassroomBorrow> }>(
        '/classroom/borrows',
        { params }
    )
}

// 提交教室借用申请
export function submitClassroomBorrow(data: Partial<ClassroomBorrow>) {
    return request.post('/classroom/borrows', data)
}

// 审核教室借用
export function reviewClassroomBorrow(id: string, data: { approved: boolean; reason?: string }) {
    return request.post(`/classroom/borrows/${id}/review`, data)
}

// 获取教室占用情况（空闲教室查询）
export function getClassroomOccupancy(params: { date: string; timeSlot: string; building?: string; minCapacity?: number }) {
    return request.get<any, { data: ClassroomOccupancy[] }>(
        '/classroom/occupancy',
        { params }
    )
}
