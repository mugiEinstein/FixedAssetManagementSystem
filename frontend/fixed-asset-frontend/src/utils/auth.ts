// Token 管理
const TOKEN_KEY = 'token'
const USER_INFO_KEY = 'userInfo'

export function getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken(): void {
    localStorage.removeItem(TOKEN_KEY)
}

export function getUserInfo(): any | null {
    const info = localStorage.getItem(USER_INFO_KEY)
    return info ? JSON.parse(info) : null
}

export function setUserInfo(userInfo: any): void {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo))
}

export function removeUserInfo(): void {
    localStorage.removeItem(USER_INFO_KEY)
}

export function clearAuth(): void {
    removeToken()
    removeUserInfo()
}