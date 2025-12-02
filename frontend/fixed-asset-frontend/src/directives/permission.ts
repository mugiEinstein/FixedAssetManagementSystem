import type { App, Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'
import type { UserRole } from '@/types/user'

// 权限配置：定义每个角色可以访问的权限
const rolePermissions: Record<UserRole, string[]> = {
    system_admin: ['*'], // 管理员拥有所有权限
    student: [
        'my-borrow',
        'classroom-query',
        'my-housing',
        'housing-transfer',
        'classroom-apply',
        'activity-apply',
        'my-schedule'
    ],
    teacher: [
        'office-room-apply',
        'staff-housing-apply',
        'activity-apply',
        'maintenance-apply',
        'my-schedule'
    ],
    equip_manager: [
        'equipment-manage',
        'equipment-borrow-review',
        'equipment-return',
        'maintenance-manage'
    ],
    room_manager: [
        'building-manage',
        'room-manage',
        'office-room-manage',
        'classroom-manage',
        'housing-manage',
        'activity-venue-manage'
    ],
    finance_manager: [
        'fee-manage',
        'deposit-manage',
        'finance-report'
    ],
    maintenance_staff: [
        'maintenance-handle'
    ]
}

// 检查权限
function checkPermission(permission: string | string[]): boolean {
    const userStore = useUserStore()
    const userRole = userStore.role as UserRole

    if (!userRole) return false

    const permissions = rolePermissions[userRole] || []

    // 管理员拥有所有权限
    if (permissions.includes('*')) return true

    // 检查具体权限
    if (Array.isArray(permission)) {
        return permission.some(p => permissions.includes(p))
    }

    return permissions.includes(permission)
}

// v-permission 指令
const permissionDirective: Directive = {
    mounted(el: HTMLElement, binding: DirectiveBinding) {
        const { value } = binding

        if (value) {
            const hasPermission = checkPermission(value)

            if (!hasPermission) {
                el.parentNode?.removeChild(el)
            }
        }
    }
}

// v-role 指令：根据角色显示/隐藏元素
const roleDirective: Directive = {
    mounted(el: HTMLElement, binding: DirectiveBinding) {
        const { value } = binding
        const userStore = useUserStore()

        if (value) {
            const roles = Array.isArray(value) ? value : [value]
            const hasRole = userStore.hasRole(roles as UserRole[])

            if (!hasRole) {
                el.parentNode?.removeChild(el)
            }
        }
    }
}

// 注册指令
export function setupPermissionDirective(app: App) {
    app.directive('permission', permissionDirective)
    app.directive('role', roleDirective)
}

// 导出检查函数供组合式 API 使用
export { checkPermission }