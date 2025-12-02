import axios from 'axios'
import { getToken } from '@/utils/auth'

// 创建独立的请求实例（不显示错误提示）
const silentRequest = axios.create({
    baseURL: '/api',
    timeout: 15000,
    headers: { 'Content-Type': 'application/json' }
})

silentRequest.interceptors.request.use((config) => {
    const token = getToken()
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

// 获取仪表盘统计数据（静默请求，失败不提示）
export function getDashboardStats() {
    return silentRequest.get<{ data: DashboardStats }>('/reports/asset-statistics')
        .then(res => res.data)
        .catch(() => ({ data: null }))
}

// 仪表盘统计数据类型（匹配后端 ReportServiceImpl 返回的数据结构）
export interface DashboardStats {
    buildingCount: number
    buildingValue: number
    equipmentCount: number
    equipmentValue: number
    vehicleCount: number
    vehicleValue: number
    totalCount: number
    totalValue: number
    statusDistribution?: Array<{ name: string; value: number }>
    typeDistribution?: Array<{ name: string; value: number }>
}
