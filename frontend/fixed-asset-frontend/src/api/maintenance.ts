import request from './index'
import type { PageParams, PageResult } from '@/types/api'

export interface MaintenanceRecord {
    maintenanceId?: string
    recordId?: string
    assetType: string
    assetId: string
    assetName?: string
    location?: string
    faultDescription?: string
    priority?: string
    reporterName?: string
    reporterPhone?: string
    reporterDept?: string
    reporterDepartment?: string
    status?: string
    assignedTo?: string
    assignedName?: string
    maintenancePerson?: string
    maintenanceDate?: string
    startTime?: string
    endTime?: string
    maintenanceCost?: number
    cost?: number
    result?: string
    maintenanceDescription?: string
    createdAt?: string
    reportTime?: string
}

export interface Maintainer {
    id: string
    name: string
    phone: string
    specialty: string
}

// 获取维保列表
export function getMaintenanceList(params: PageParams) {
    return request.get<any, { data: PageResult<MaintenanceRecord> }>(
        '/maintenance',
        { params }
    )
}

// 提交维保申请
export function submitMaintenanceRequest(data: Partial<MaintenanceRecord>) {
    return request.post('/maintenance', data)
}

// 指派维护人员
export function assignMaintainer(id: string, data: { maintainerId: string; maintainerName: string }) {
    return request.post(`/maintenance/${id}/assign`, data)
}

// 开始维修
export function startMaintenance(id: string) {
    return request.post(`/maintenance/${id}/start`)
}

// 完成维修
export function completeMaintenance(id: string, data: { result: string; cost?: number }) {
    return request.post(`/maintenance/${id}/complete`, data)
}

// 获取维护人员列表
export function getMaintainerList() {
    return request.get<any, { data: Maintainer[] }>('/maintenance/maintainers')
}
