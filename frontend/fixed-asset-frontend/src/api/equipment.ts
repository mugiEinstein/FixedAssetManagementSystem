import request from './index'
import type { Equipment } from '@/types/asset'
import type { PageParams, PageResult } from '@/types/api'

const BASE_URL = '/equipments'

// 获取器材列表
export function getEquipmentList(params: PageParams) {
    return request.get<any, { data: PageResult<Equipment> }>(BASE_URL, { params })
}

// 获取器材详情
export function getEquipmentDetail(id: string) {
    return request.get<any, { data: Equipment }>(`${BASE_URL}/${id}`)
}

// 新增器材
export function createEquipment(data: Partial<Equipment>) {
    return request.post(BASE_URL, data)
}

// 更新器材
export function updateEquipment(id: string, data: Partial<Equipment>) {
    return request.put(`${BASE_URL}/${id}`, data)
}

// 删除器材
export function deleteEquipment(id: string) {
    return request.delete(`${BASE_URL}/${id}`)
}

// 获取器材净值
export function getEquipmentNetValue(id: string) {
    return request.get<any, { data: { netValue: number; depreciation: number } }>(
        `${BASE_URL}/${id}/net-value`
    )
}

// 获取空闲器材列表
export function getIdleEquipments(params?: PageParams) {
    return request.get<any, { data: Equipment[] }>(`${BASE_URL}/idle`, { params })
}

// 导出器材数据
export function exportEquipments(params?: PageParams) {
    return request.get(`${BASE_URL}/export`, {
        params,
        responseType: 'blob'
    })
}