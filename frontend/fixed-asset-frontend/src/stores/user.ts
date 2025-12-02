import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo, UserRole } from '@/types/user'
import { login as loginApi, logout as logoutApi } from '@/api/auth'
import { getToken, setToken, removeToken, getUserInfo, setUserInfo, removeUserInfo, clearAuth } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
    // 状态
    const token = ref<string | null>(getToken())
    const userInfo = ref<UserInfo | null>(getUserInfo())

    // 计算属性
    const isLoggedIn = computed(() => !!token.value)
    const role = computed(() => userInfo.value?.role || '')
    const realName = computed(() => userInfo.value?.realName || '')
    const userId = computed(() => userInfo.value?.id || '')

    // 登录
    async function login(username: string, password: string, role?: string) {
        try {
            const res = await loginApi({ username, password, role })
            const data = res.data

            token.value = data.token
            userInfo.value = data

            setToken(data.token)
            setUserInfo(data)

            return data
        } catch (error) {
            throw error
        }
    }

    // 登出
    async function logout() {
        try {
            await logoutApi()
        } catch (error) {
            console.error('登出接口调用失败', error)
        } finally {
            token.value = null
            userInfo.value = null
            clearAuth()
        }
    }

    // 检查权限
    function hasRole(roles: UserRole | UserRole[]): boolean {
        if (!userInfo.value) return false

        const userRole = userInfo.value.role
        if (Array.isArray(roles)) {
            return roles.includes(userRole)
        }
        return userRole === roles
    }

    // 检查是否是管理员
    function isAdmin(): boolean {
        return userInfo.value?.role === 'system_admin'
    }

    return {
        token,
        userInfo,
        isLoggedIn,
        role,
        realName,
        userId,
        login,
        logout,
        hasRole,
        isAdmin
    }
})