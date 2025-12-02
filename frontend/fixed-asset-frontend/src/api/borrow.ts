import request from './index'
import type { ExternalEquipmentBorrow, InternalEquipmentBorrow } from '@/types/business'
import type { PageParams, PageResult } from '@/types/api'

// ==================== 校外借用 ====================

export function getExternalBorrowList(params: PageParams) {
    return request.get<any, { data: PageResult<ExternalEquipmentBorrow> }>(
        '/equipment-borrow/external',
        { params }
    )
}

export function getExternalBorrowDetail(id: string) {
    return request.get<any, { data: ExternalEquipmentBorrow }>(
        `/equipment-borrow/external/${id}`
    )
}

export function submitExternalBorrow(data: Partial<ExternalEquipmentBorrow>) {
    return request.post('/equipment-borrow/external', data)
}

export function reviewExternalQualification(id: string, data: { approved: boolean; reason?: string }) {
    return request.post(`/equipment-borrow/external/${id}/qualification-review`, data)
}

export function reviewExternalEquipment(id: string, data: { approved: boolean; reason?: string }) {
    return request.post(`/equipment-borrow/external/${id}/equipment-review`, data)
}

export function confirmExternalPayment(id: string) {
    return request.post(`/equipment-borrow/external/${id}/confirm-payment`)
}

export function confirmExternalPickup(id: string) {
    return request.post(`/equipment-borrow/external/${id}/confirm-pickup`)
}

export function returnExternalEquipment(id: string, data: { 
    isNormal: boolean
    damageDesc?: string
    damageLevel?: 'minor' | 'moderate' | 'severe' | 'lost' | ''
    compensationAmount?: number
}) {
    return request.post(`/equipment-borrow/external/${id}/return`, data)
}

export function applyExternalRenewal(id: string, data: { additionalDays: number }) {
    return request.post(`/equipment-borrow/external/${id}/renewal`, data)
}

// ==================== 校内借用 ====================

export function getInternalBorrowList(params: PageParams) {
    return request.get<any, { data: PageResult<InternalEquipmentBorrow> }>(
        '/equipment-borrow/internal',
        { params }
    )
}

export function getInternalBorrowDetail(id: string) {
    return request.get<any, { data: InternalEquipmentBorrow }>(
        `/equipment-borrow/internal/${id}`
    )
}

export function submitInternalBorrow(data: Partial<InternalEquipmentBorrow>) {
    return request.post('/equipment-borrow/internal', data)
}

export function reviewInternalPermission(id: string, data: { approved: boolean; reason?: string }) {
    return request.post(`/equipment-borrow/internal/${id}/permission-review`, data)
}

export function reviewInternalEquipment(id: string, data: { approved: boolean; reason?: string }) {
    return request.post(`/equipment-borrow/internal/${id}/equipment-review`, data)
}

export function confirmInternalPickup(id: string) {
    return request.post(`/equipment-borrow/internal/${id}/confirm-pickup`)
}

export function returnInternalEquipment(id: string, data: { isNormal: boolean; damageDesc?: string }) {
    return request.post(`/equipment-borrow/internal/${id}/return`, data)
}

// ==================== 我的借用 ====================

export function getMyBorrowList(params: PageParams & { applicantId?: string }) {
    return request.get<any, { data: PageResult<InternalEquipmentBorrow> }>(
        '/equipment-borrow/my',
        { params }
    )
}

export function cancelMyBorrow(id: string) {
    return request.post(`/equipment-borrow/my/${id}/cancel`)
}

// 归还器材（用户端）- 支持损坏赔偿
export function returnMyEquipment(id: string, data: { 
    isNormal: boolean
    damageDesc?: string
    damageLevel?: 'minor' | 'moderate' | 'severe' | 'lost' | ''
    compensationAmount?: number 
}) {
    return request.post(`/equipment-borrow/internal/${id}/return`, data)
}