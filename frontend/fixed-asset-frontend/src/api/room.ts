import request from './index'
import type { Room } from '@/types/asset'
import type { PageParams } from '@/types/api'

const BASE_URL = '/rooms'

// 获取房间列表
export function getRoomList(params: PageParams) {
    return request.get(BASE_URL, { params })
}

// 获取房间详情
export function getRoomDetail(id: string) {
    return request.get(`${BASE_URL}/${id}`)
}

// 新增房间
export function createRoom(data: Partial<Room>) {
    return request.post(BASE_URL, data)
}

// 更新房间
export function updateRoom(id: string, data: Partial<Room>) {
    return request.put(`${BASE_URL}/${id}`, data)
}

// 删除房间
export function deleteRoom(id: string) {
    return request.delete(`${BASE_URL}/${id}`)
}
