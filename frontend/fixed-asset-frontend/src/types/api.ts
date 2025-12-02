// API 响应类型定义

export interface ApiResponse<T = any> {
    code: number
    msg: string
    data: T
}

export interface PageResult<T> {
    list: T[]
    total: number
    page: number
    pageSize: number
}

export interface PageParams {
    page?: number
    pageSize?: number
    [key: string]: any
}