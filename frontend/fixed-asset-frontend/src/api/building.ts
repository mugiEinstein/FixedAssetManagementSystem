import request from './index'
import type { Building } from '@/types/asset'
import type { PageParams } from '@/types/api'

const BASE_URL = '/buildings'

// 获取建筑列表
export function getBuildingList(params: PageParams) {
    return request.get(BASE_URL, { params })
}

// 获取建筑详情
export function getBuildingDetail(id: string) {
    return request.get(`${BASE_URL}/${id}`)
}

// 新增建筑
export function createBuilding(data: Partial<Building>) {
    return request.post(BASE_URL, data)
}

// 更新建筑
export function updateBuilding(id: string, data: Partial<Building>) {
    return request.put(`${BASE_URL}/${id}`, data)
}

// 删除建筑
export function deleteBuilding(id: string) {
    return request.delete(`${BASE_URL}/${id}`)
}

// 导出建筑数据
export function exportBuildings(params?: PageParams) {
    return request.get(`${BASE_URL}/export`, {
        params,
        responseType: 'blob'
    })
}