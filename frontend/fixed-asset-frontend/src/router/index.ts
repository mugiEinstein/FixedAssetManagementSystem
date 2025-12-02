import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { getToken, getUserInfo } from '@/utils/auth'
import type { UserRole } from '@/types/user'

// 扩展 RouteMeta 类型
declare module 'vue-router' {
    interface RouteMeta {
        title?: string
        icon?: string
        hidden?: boolean
        roles?: UserRole[]
    }
}

// 公共路由（无需登录）
const publicRoutes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/index.vue'),
        meta: { title: '登录', hidden: true }
    },
    {
        path: '/404',
        name: 'NotFound',
        component: () => import('@/views/error/404.vue'),
        meta: { title: '404', hidden: true }
    }
]

// 需要登录的路由（按用户要求配置权限）
const protectedRoutes: RouteRecordRaw[] = [
    // 首页 - 所有登录用户可见
    {
        path: '/',
        component: () => import('@/layout/index.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/dashboard/index.vue'),
                meta: { 
                    title: '首页', 
                    icon: 'HomeFilled'
                    // 不设置 roles，所有登录用户可访问
                }
            }
        ]
    },
    // 资产管理 - 超级管理员(4个) + 房产管理员(建筑+房间) + 器材管理员(器材) + 车辆管理员(logistics_admin)
    {
        path: '/asset',
        component: () => import('@/layout/index.vue'),
        redirect: '/asset/building',
        meta: { 
            title: '资产管理', 
            icon: 'OfficeBuilding',
            roles: ['system_admin', 'room_manager', 'equip_manager', 'logistics_admin']
        },
        children: [
            {
                path: 'building',
                name: 'Building',
                component: () => import('@/views/asset/building/index.vue'),
                meta: { title: '建筑管理', icon: 'School', roles: ['system_admin', 'room_manager'] }
            },
            {
                path: 'room',
                name: 'Room',
                component: () => import('@/views/asset/room/index.vue'),
                meta: { title: '房间管理', icon: 'House', roles: ['system_admin', 'room_manager'] }
            },
            {
                path: 'equipment',
                name: 'Equipment',
                component: () => import('@/views/asset/equipment/index.vue'),
                meta: { title: '器材管理', icon: 'Monitor', roles: ['system_admin', 'equip_manager'] }
            },
            {
                path: 'vehicle',
                name: 'Vehicle',
                component: () => import('@/views/asset/vehicle/index.vue'),
                meta: { title: '车辆管理', icon: 'Van', roles: ['system_admin', 'vehicle_manager', 'logistics_admin'] }
            }
        ]
    },
    // 器材借用 - 学生/教师(申请+我的借用)，超级管理员/器材管理员(审批)
    {
        path: '/business/equipment-borrow',
        component: () => import('@/layout/index.vue'),
        redirect: '/business/equipment-borrow/apply',
        meta: { 
            title: '器材借用', 
            icon: 'Goods',
            roles: ['system_admin', 'student', 'teacher', 'equip_manager']
        },
        children: [
            {
                path: 'apply',
                name: 'BorrowApply',
                component: () => import('@/views/business/equipment-borrow/apply/index.vue'),
                meta: { title: '借用申请', icon: 'Edit', roles: ['system_admin', 'student', 'teacher'] }
            },
            {
                path: 'my-borrow',
                name: 'MyBorrow',
                component: () => import('@/views/business/equipment-borrow/my-borrow/index.vue'),
                meta: { title: '我的借用', icon: 'User', roles: ['system_admin', 'student', 'teacher'] }
            },
            {
                path: 'external',
                name: 'ExternalBorrow',
                component: () => import('@/views/business/equipment-borrow/external/index.vue'),
                meta: { title: '校外借用审批', icon: 'Promotion', roles: ['system_admin', 'equip_manager'] }
            },
            {
                path: 'internal',
                name: 'InternalBorrow',
                component: () => import('@/views/business/equipment-borrow/internal/index.vue'),
                meta: { title: '校内借用审批', icon: 'Coordinate', roles: ['system_admin', 'equip_manager'] }
            }
        ]
    },
    // 房产管理 - 超级管理员(5个)，学生(3个)，教师(3个)
    {
        path: '/business/housing',
        component: () => import('@/layout/index.vue'),
        redirect: '/business/housing/classroom-query',
        meta: { 
            title: '房产管理', 
            icon: 'House',
            roles: ['system_admin', 'student', 'teacher', 'room_manager']
        },
        children: [
            {
                path: 'office-room',
                name: 'OfficeRoom',
                component: () => import('@/views/business/office-room/index.vue'),
                meta: { title: '办公用房分配', icon: 'Briefcase', roles: ['system_admin', 'room_manager'] }
            },
            {
                path: 'classroom',
                name: 'Classroom',
                component: () => import('@/views/business/classroom/index.vue'),
                meta: { title: '教室借用', icon: 'Reading', roles: ['system_admin'] }
            },
            {
                path: 'classroom-query',
                name: 'ClassroomQuery',
                component: () => import('@/views/business/classroom/query.vue'),
                meta: { title: '空闲教室查询', icon: 'Search', roles: ['system_admin', 'student', 'teacher'] }
            },
            {
                path: 'my-classroom-applications',
                name: 'MyClassroomApplications',
                component: () => import('@/views/business/classroom/my-applications.vue'),
                meta: { title: '我的教室申请', icon: 'Document', roles: ['system_admin', 'student', 'teacher'] }
            },
            {
                path: 'student-housing',
                name: 'StudentHousing',
                component: () => import('@/views/business/student-housing/index.vue'),
                meta: { title: '学生住宿管理', icon: 'UserFilled', roles: ['system_admin'] }
            },
            {
                path: 'my-housing',
                name: 'MyHousing',
                component: () => import('@/views/business/student-housing/my-housing.vue'),
                meta: { title: '我的住宿', icon: 'House', roles: ['student', 'teacher'] }
            },
            {
                path: 'housing-transfer',
                name: 'HousingTransfer',
                component: () => import('@/views/business/student-housing/transfer.vue'),
                meta: { title: '宿舍调换', icon: 'Switch', roles: ['student', 'teacher'] }
            },
            {
                path: 'staff-housing',
                name: 'StaffHousing',
                component: () => import('@/views/business/staff-housing/index.vue'),
                meta: { title: '教职工住宿', icon: 'Avatar', roles: ['system_admin'] }
            },
            {
                path: 'housing-review',
                name: 'HousingReview',
                component: () => import('@/views/business/student-housing/review.vue'),
                meta: { title: '住宿申请审核', icon: 'Finished', roles: ['system_admin'] }
            }
        ]
    },
    // 活动场地 - 超级管理员 + 学生/教师 + 安保主管(logistics_admin)
    {
        path: '/business/activity-venue',
        component: () => import('@/layout/index.vue'),
        redirect: '/business/activity-venue/internal',
        meta: { 
            title: '活动场地', 
            icon: 'Place',
            roles: ['system_admin', 'student', 'teacher', 'maintenance_staff', 'security_admin', 'logistics_admin']
        },
        children: [
            {
                path: 'external',
                name: 'ExternalActivity',
                component: () => import('@/views/business/activity-venue/external/index.vue'),
                meta: { title: '校外承办', icon: 'Promotion', roles: ['system_admin'] }
            },
            {
                path: 'internal',
                name: 'InternalActivity',
                component: () => import('@/views/business/activity-venue/internal/index.vue'),
                meta: { title: '校内举办', icon: 'Coordinate', roles: ['system_admin', 'student', 'teacher'] }
            },
            {
                path: 'security',
                name: 'ActivitySecurity',
                component: () => import('@/views/business/activity-venue/security/index.vue'),
                meta: { title: '活动安保记录', icon: 'Warning', roles: ['system_admin', 'maintenance_staff', 'security_admin', 'logistics_admin'] }
            }
        ]
    },
    // 排课管理 - 超级管理员(排课管理)，学生/教师(课表显示)，房产管理员(排课管理)
    {
        path: '/business/course',
        component: () => import('@/layout/index.vue'),
        redirect: '/business/course/my-schedule',
        meta: { 
            title: '排课管理', 
            icon: 'Calendar',
            roles: ['system_admin', 'student', 'teacher', 'room_manager']
        },
        children: [
            {
                path: 'scheduling',
                name: 'CourseScheduling',
                component: () => import('@/views/business/course-scheduling/index.vue'),
                meta: { title: '排课管理', icon: 'SetUp', roles: ['system_admin', 'room_manager'] }
            },
            {
                path: 'my-schedule',
                name: 'MySchedule',
                component: () => import('@/views/business/course-scheduling/my-schedule.vue'),
                meta: { title: '课表显示', icon: 'Notebook', roles: ['system_admin', 'student', 'teacher'] }
            }
        ]
    },
    // 维保管理 - 超级管理员(2个)，学生/教师/房产管理员(维保申请)，器材管理员/维保人员(2个)
    {
        path: '/maintenance',
        component: () => import('@/layout/index.vue'),
        redirect: '/maintenance/apply',
        meta: { 
            title: '维保管理', 
            icon: 'Tools',
            roles: ['system_admin', 'student', 'teacher', 'equip_manager', 'room_manager', 'maintenance_staff']
        },
        children: [
            {
                path: 'record',
                name: 'MaintenanceRecord',
                component: () => import('@/views/business/maintenance/index.vue'),
                meta: { title: '维保记录', icon: 'Setting', roles: ['system_admin', 'equip_manager', 'maintenance_staff'] }
            },
            {
                path: 'apply',
                name: 'MaintenanceApply',
                component: () => import('@/views/business/maintenance/apply.vue'),
                meta: { title: '维保申请', icon: 'Edit', roles: ['system_admin', 'student', 'teacher', 'equip_manager', 'room_manager', 'maintenance_staff'] }
            }
        ]
    },
    // 盘点核算 - 超级管理员/财务管理员(2个)
    {
        path: '/inventory',
        component: () => import('@/layout/index.vue'),
        redirect: '/inventory/check',
        meta: { 
            title: '盘点核算', 
            icon: 'Document',
            roles: ['system_admin', 'finance_manager']
        },
        children: [
            {
                path: 'check',
                name: 'InventoryCheck',
                component: () => import('@/views/inventory/inventory-check/index.vue'),
                meta: { title: '盘点计划', icon: 'List', roles: ['system_admin', 'finance_manager'] }
            },
            {
                path: 'value',
                name: 'ValueCalculation',
                component: () => import('@/views/inventory/value-calculation/index.vue'),
                meta: { title: '价值核算', icon: 'Coin', roles: ['system_admin', 'finance_manager'] }
            }
        ]
    },
    // 入库建档
    {
        path: '/archive',
        component: () => import('@/layout/index.vue'),
        redirect: '/archive/building',
        meta: { 
            title: '入库建档', 
            icon: 'FolderAdd',
            roles: ['system_admin', 'room_manager', 'equip_manager', 'logistics_admin']
        },
        children: [
            {
                path: 'building',
                name: 'BuildingArchive',
                component: () => import('@/views/archive/building-archive/index.vue'),
                meta: { title: '房屋建档', icon: 'OfficeBuilding', roles: ['system_admin', 'room_manager'] }
            },
            {
                path: 'room',
                name: 'RoomArchive',
                component: () => import('@/views/archive/room-archive/index.vue'),
                meta: { title: '房间建档', icon: 'House', roles: ['system_admin'] }
            },
            {
                path: 'equipment',
                name: 'EquipmentArchive',
                component: () => import('@/views/archive/equipment-archive/index.vue'),
                meta: { title: '器材档案', icon: 'Box', roles: ['system_admin', 'equip_manager'] }
            },
            {
                path: 'vehicle',
                name: 'VehicleArchive',
                component: () => import('@/views/archive/vehicle-archive/index.vue'),
                meta: { title: '车辆建档', icon: 'Van', roles: ['system_admin', 'logistics_admin'] }
            }
        ]
    },
    // 报表统计
    {
        path: '/report',
        component: () => import('@/layout/index.vue'),
        redirect: '/report/asset-statistics',
        meta: { 
            title: '报表统计', 
            icon: 'DataAnalysis',
            roles: ['system_admin', 'finance_manager']
        },
        children: [
            {
                path: 'asset-statistics',
                name: 'AssetStatistics',
                component: () => import('@/views/report/asset-statistics/index.vue'),
                meta: { title: '资产统计', icon: 'PieChart', roles: ['system_admin', 'finance_manager'] }
            },
            {
                path: 'finance-detail',
                name: 'FinanceDetail',
                component: () => import('@/views/report/finance-detail/index.vue'),
                meta: { title: '财务明细', icon: 'Money', roles: ['system_admin', 'finance_manager'] }
            },
            {
                path: 'fee-calculator',
                name: 'FeeCalculator',
                component: () => import('@/views/report/fee-calculator/index.vue'),
                meta: { title: '费用计算工具', icon: 'Cpu', roles: ['system_admin', 'finance_manager'] }
            }
        ]
    }
]

// 检查用户是否有权限访问路由
function hasPermission(roles: UserRole[] | undefined, userRole: UserRole): boolean {
    if (!roles || roles.length === 0) return true
    return roles.includes(userRole)
}

// 导出路由列表供其他组件使用
export { protectedRoutes, hasPermission }

// 404 匹配
const errorRoute: RouteRecordRaw = {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    meta: { hidden: true }
}

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [...publicRoutes, ...protectedRoutes, errorRoute]
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = getToken()
    document.title = to.meta.title ? `${to.meta.title} - 固定资产管理系统` : '固定资产管理系统'

    if (to.path === '/login') {
        if (token) {
            next('/dashboard')
        } else {
            next()
        }
    } else if (to.path === '/404') {
        next()
    } else {
        if (token) {
            next()
        } else {
            next('/login')
        }
    }
})

export default router