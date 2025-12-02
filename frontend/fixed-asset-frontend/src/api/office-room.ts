import request from './index'
import type { PageParams, PageResult } from '@/types/api'

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
    status: string
    createdAt: string
}

// 获取办公用房申请列表
export function getOfficeRoomList(params: PageParams) {
    return request.get<any, { data: PageResult<OfficeRoomAllocation> }>(
        '/office-room',
        { params }
    )
}

// 提交办公用房申请
export function submitOfficeRoomApplication(data: Partial<OfficeRoomAllocation>) {
    return request.post('/office-room', data)
}

// 审核办公用房申请
export function reviewOfficeRoomApplication(id: string, data: { approved: boolean; roomId?: string; reason?: string }) {
    return request.post(`/office-room/${id}/review`, data)
}

// 退还办公用房
export function returnOfficeRoom(id: string, data?: { reason?: string }) {
    return request.post(`/office-room/${id}/return`, data)
}
