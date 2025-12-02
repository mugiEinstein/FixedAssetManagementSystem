import request from './index'
import type { LoginParams, UserInfo } from '@/types/user'

// 登录
export function login(data: LoginParams) {
    return request.post<any, { data: UserInfo }>('/login', data)
}

// 登出
export function logout() {
    return request.post('/logout')
}

// 获取当前用户信息
export function getCurrentUser() {
    return request.get<any, { data: UserInfo }>('/user/current')
}