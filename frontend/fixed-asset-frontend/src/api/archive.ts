import request from './index'
import type { PageParams, PageResult } from '@/types/api'

// ==================== 器材档案 ====================

export interface EquipmentArchive {
    equipmentId: string
    equipmentName: string
    equipmentType: string
    model?: string
    specifications?: string
    originalValue: number
    purchaseDate: string
    manufacturer?: string
    storageLocation?: string
    depreciationYears: number
    custodian?: string
    archiveStatus: string
    remark?: string
}

export function getEquipmentArchiveList(params: PageParams) {
    return request.get<any, { data: PageResult<EquipmentArchive> }>(
        '/archive/equipment',
        { params }
    )
}

export function createEquipmentArchive(data: Partial<EquipmentArchive>) {
    return request.post('/archive/equipment', data)
}

export function updateEquipmentArchive(id: string, data: Partial<EquipmentArchive>) {
    return request.put(`/archive/equipment/${id}`, data)
}

export function deleteEquipmentArchive(id: string) {
    return request.delete(`/archive/equipment/${id}`)
}

// ==================== 建筑档案 ====================

export interface BuildingArchive {
    buildingId: string
    buildingName: string
    buildingType: string
    address: string
    totalArea: number
    floors: number
    buildYear: number
    originalValue: number
    depreciationYears: number
    propertyOwner?: string
    archiveStatus: string
    remark?: string
}

export function getBuildingArchiveList(params: PageParams) {
    return request.get<any, { data: PageResult<BuildingArchive> }>(
        '/archive/building',
        { params }
    )
}

export function createBuildingArchive(data: Partial<BuildingArchive>) {
    return request.post('/archive/building', data)
}

export function updateBuildingArchive(id: string, data: Partial<BuildingArchive>) {
    return request.put(`/archive/building/${id}`, data)
}

export function deleteBuildingArchive(id: string) {
    return request.delete(`/archive/building/${id}`)
}

// 获取所有建筑(下拉框用)
export function getAllBuildings() {
    return request.get<any, { data: BuildingArchive[] }>('/archive/building/all')
}

// ==================== 房屋档案 ====================

export interface RoomArchive {
    roomId: string
    roomName: string
    buildingId: string
    buildingName?: string
    floor: number
    roomArea: number
    roomType: string
    roomStatus: string
    currentUser?: string
    remark?: string
}

export function getRoomArchiveList(params: PageParams) {
    return request.get<any, { data: PageResult<RoomArchive> }>(
        '/archive/room',
        { params }
    )
}

export function createRoomArchive(data: Partial<RoomArchive>) {
    return request.post('/archive/room', data)
}

export function updateRoomArchive(id: string, data: Partial<RoomArchive>) {
    return request.put(`/archive/room/${id}`, data)
}

export function deleteRoomArchive(id: string) {
    return request.delete(`/archive/room/${id}`)
}

// ==================== 车辆档案 ====================

export interface VehicleArchive {
    vehicleId: string
    vehicleName: string
    vehicleType: string
    plateNumber: string
    brand?: string
    model?: string
    engineNumber?: string
    frameNumber?: string
    purchaseDate: string
    originalValue: number
    depreciationYears: number
    driver?: string
    archiveStatus: string
    remark?: string
}

export function getVehicleArchiveList(params: PageParams) {
    return request.get<any, { data: PageResult<VehicleArchive> }>(
        '/archive/vehicle',
        { params }
    )
}

export function createVehicleArchive(data: Partial<VehicleArchive>) {
    return request.post('/archive/vehicle', data)
}

export function updateVehicleArchive(id: string, data: Partial<VehicleArchive>) {
    return request.put(`/archive/vehicle/${id}`, data)
}

export function deleteVehicleArchive(id: string) {
    return request.delete(`/archive/vehicle/${id}`)
}
