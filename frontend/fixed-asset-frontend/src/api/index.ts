import axios from 'axios'
import type { InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearAuth } from '@/utils/auth'

// 创建 axios 实例
const request = axios.create({
    baseURL: '/api',
    timeout: 15000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器
request.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = getToken()
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response: AxiosResponse) => {
        const res = response.data

        // 业务错误处理
        if (res.code !== 200) {
            ElMessage.error(res.msg || '请求失败')

            // 401 未授权，跳转登录
            if (res.code === 401) {
                clearAuth()
                // 延迟跳转，避免循环依赖
                setTimeout(() => {
                    window.location.href = '/login'
                }, 100)
            }

            return Promise.reject(new Error(res.msg || '请求失败'))
        }

        return res
    },
    (error) => {
        // HTTP 错误处理
        if (error.response) {
            const status = error.response.status

            switch (status) {
                case 401:
                    ElMessage.error('登录已过期，请重新登录')
                    clearAuth()
                    setTimeout(() => {
                        window.location.href = '/login'
                    }, 100)
                    break
                case 403:
                    ElMessage.error('没有权限访问')
                    break
                case 404:
                    ElMessage.error('请求的资源不存在')
                    break
                case 500:
                    ElMessage.error('服务器内部错误')
                    break
                default:
                    ElMessage.error(error.message || '网络错误')
            }
        } else {
            ElMessage.error('网络连接失败')
        }

        return Promise.reject(error)
    }
)

export default request