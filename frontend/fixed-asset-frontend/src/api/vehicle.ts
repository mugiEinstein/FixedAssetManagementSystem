import request from './index'
import type { Vehicle } from '@/types/asset'
import type { PageParams } from '@/types/api'

const BASE_URL = '/vehicles'

// 获取车辆列表
export function getVehicleList(params: PageParams) {
    return request.get(BASE_URL, { params })
}

// 获取车辆详情
export function getVehicleDetail(id: string) {
    return request.get(`${BASE_URL}/${id}`)
}

// 新增车辆
export function createVehicle(data: Partial<Vehicle>) {
    return request.post(BASE_URL, data)
}

// 更新车辆
export function updateVehicle(id: string, data: Partial<Vehicle>) {
    return request.put(`${BASE_URL}/${id}`, data)
}

// 删除车辆
export function deleteVehicle(id: string) {
    return request.delete(`${BASE_URL}/${id}`)
}

// 获取空闲车辆
export function getIdleVehicles() {
    return request.get(`${BASE_URL}/idle`)
}

// ==================== 车辆调度 ====================

export interface VehicleDispatch {
    dispatchId: string
    vehicleId: string
    vehiclePlate: string
    vehicleType: string
    driverName: string
    driverPhone: string
    applicant: string
    department: string
    purpose: string
    destination: string
    startTime: string
    endTime: string
    mileageStart?: number
    mileageEnd?: number
    fuelCost?: number
    status: 'pending' | 'approved' | 'in_use' | 'completed' | 'cancelled'
    applyTime: string
    approver?: string
    approveTime?: string
}

// 获取车辆调度列表
export function getVehicleDispatchList(params: PageParams) {
    return request.get(`${BASE_URL}/dispatch`, { params })
}

// 申请用车
export function applyVehicle(data: Partial<VehicleDispatch>) {
    return request.post(`${BASE_URL}/dispatch/apply`, data)
}

// 审批用车申请
export function approveDispatch(id: string, data: { approved: boolean; reason?: string }) {
    return request.post(`${BASE_URL}/dispatch/${id}/approve`, data)
}

// 开始用车
export function startDispatch(id: string, data: { mileageStart: number }) {
    return request.post(`${BASE_URL}/dispatch/${id}/start`, data)
}

// 完成用车
export function completeDispatch(id: string, data: { mileageEnd: number; fuelCost?: number; remark?: string }) {
    return request.post(`${BASE_URL}/dispatch/${id}/complete`, data)
}

// ==================== 车辆维护 ====================

export interface VehicleMaintenance {
    maintenanceId: string
    vehicleId: string
    vehiclePlate: string
    vehicleType: string
    maintenanceType: 'regular' | 'repair' | 'inspection' | 'insurance'
    description: string
    maintenanceDate: string
    cost: number
    vendor?: string
    nextMaintenanceDate?: string
    status: 'pending' | 'in_progress' | 'completed'
    createdBy: string
    createdAt: string
}

// 获取车辆维护记录
export function getVehicleMaintenanceList(params: PageParams) {
    return request.get(`${BASE_URL}/maintenance`, { params })
}

// 添加维护记录
export function addVehicleMaintenance(data: Partial<VehicleMaintenance>) {
    return request.post(`${BASE_URL}/maintenance`, data)
}

// 更新维护记录
export function updateVehicleMaintenance(id: string, data: Partial<VehicleMaintenance>) {
    return request.put(`${BASE_URL}/maintenance/${id}`, data)
}

// 完成维护
export function completeMaintenance(id: string, data: { actualCost: number; remark?: string }) {
    return request.post(`${BASE_URL}/maintenance/${id}/complete`, data)
}
