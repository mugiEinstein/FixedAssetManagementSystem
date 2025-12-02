<template>
  <div class="navbar">
    <!-- 左侧：折叠按钮 + 面包屑 -->
    <div class="navbar-left">
      <el-icon class="collapse-btn" @click="toggleSidebar">
        <component :is="collapsed ? 'Expand' : 'Fold'" />
      </el-icon>

      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 右侧：通知 + 用户信息 -->
    <div class="navbar-right">
      <!-- 消息通知 -->
      <el-popover placement="bottom" :width="320" trigger="click">
        <template #reference>
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
            <el-icon class="notification-icon" :size="20"><Bell /></el-icon>
          </el-badge>
        </template>
        <div class="notification-panel">
          <div class="notification-header">
            <span>消息通知</span>
            <el-button type="primary" link size="small" @click="markAllRead" v-if="unreadCount > 0">全部已读</el-button>
          </div>
          <el-scrollbar max-height="300px">
            <div v-if="notifications.length === 0" class="notification-empty">
              暂无新消息
            </div>
            <div
                v-for="item in notifications"
                :key="item.id"
                class="notification-item"
                :class="{ unread: !item.read }"
                @click="handleNotificationClick(item)"
            >
              <el-icon :size="16" class="notification-type-icon">
                <component :is="getNotificationIcon(item.type)" />
              </el-icon>
              <div class="notification-content">
                <div class="notification-title">{{ item.title }}</div>
                <div class="notification-desc">{{ item.content }}</div>
                <div class="notification-time">{{ item.time }}</div>
              </div>
            </div>
          </el-scrollbar>
        </div>
      </el-popover>

      <!-- 深浅色切换 -->
      <el-tooltip :content="appStore.isDarkMode ? '切换到浅色模式' : '切换到深色模式'" placement="bottom">
        <el-icon class="theme-toggle" :size="20" @click="appStore.toggleDarkMode">
          <Moon v-if="!appStore.isDarkMode" />
          <Sunny v-else />
        </el-icon>
      </el-tooltip>

      <el-dropdown trigger="click" @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="32" :icon="UserFilled" />
          <span class="user-name">{{ userStore.realName || '用户' }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item disabled>
              <el-icon><User /></el-icon>
              角色：{{ getRoleName(userStore.role) }}
            </el-dropdown-item>
            <el-dropdown-item disabled>
              <el-icon><OfficeBuilding /></el-icon>
              部门：{{ userStore.userInfo?.department || '-' }}
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { UserFilled, ArrowDown, User, OfficeBuilding, SwitchButton, Bell, Document, Tools, House, Warning, Sunny, Moon } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { getClassroomBorrowList } from '@/api/classroom'
import { getMaintenanceList } from '@/api/maintenance'
import { getStaffHousingList } from '@/api/housing'
import { getInternalBorrowList } from '@/api/borrow'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const collapsed = computed(() => appStore.sidebarCollapsed)

// ============ 通知系统 ============
interface Notification {
  id: number
  type: 'approval' | 'maintenance' | 'housing' | 'system' | 'borrow' | 'classroom'
  title: string
  content: string
  time: string
  read: boolean
  link?: string  // 跳转路由
  recordId?: string  // 关联记录ID
}

const notifications = ref<Notification[]>([])
const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)

// 加载真实业务通知数据
async function loadNotifications() {
  const role = userStore.role
  // 只有管理员角色才加载待审批通知
  if (!['system_admin', 'room_manager', 'equip_manager', 'maintenance_staff', 'finance_manager'].includes(role)) {
    return
  }
  
  const newNotifications: Notification[] = []
  let id = 1
  
  try {
    // 获取待审批的器材借用申请
    if (['system_admin', 'equip_manager'].includes(role)) {
      const borrowRes = await getInternalBorrowList({ page: 1, pageSize: 20, status: 'pending' })
      const pendingBorrow = borrowRes.data?.list || []
      pendingBorrow.forEach((item: any) => {
        newNotifications.push({
          id: id++,
          type: 'borrow',
          title: '器材借用申请待审批',
          content: `${item.applicantName || '用户'}申请借用${item.equipmentName || '器材'}`,
          time: item.createdAt || '刚刚',
          read: false,
          link: '/business/equipment-borrow/internal',
          recordId: item.id
        })
      })
    }
    
    // 获取待审批的教室借用申请
    if (['system_admin', 'room_manager'].includes(role)) {
      const classroomRes = await getClassroomBorrowList({ page: 1, pageSize: 10, status: 'pending' })
      const pendingClassroom = classroomRes.data?.list || []
      pendingClassroom.forEach((item: any) => {
        newNotifications.push({
          id: id++,
          type: 'classroom',
          title: '教室借用申请待审批',
          content: `${item.applicantName || '用户'}申请借用${item.roomName || '教室'}`,
          time: item.createdAt || '刚刚',
          read: false,
          link: '/business/housing/classroom',
          recordId: item.id
        })
      })
    }
    
    // 获取待处理的维修申请
    if (['system_admin', 'maintenance_staff'].includes(role)) {
      const maintenanceRes = await getMaintenanceList({ page: 1, pageSize: 10, status: 'pending' })
      const pendingMaintenance = maintenanceRes.data?.list || []
      pendingMaintenance.forEach((item: any) => {
        newNotifications.push({
          id: id++,
          type: 'maintenance',
          title: '维修申请待处理',
          content: `${item.assetName || '设备'}报修申请`,
          time: item.createdAt || '刚刚',
          read: false,
          link: '/maintenance/record',
          recordId: item.id
        })
      })
    }
    
    // 获取待审批的住宿申请
    if (['system_admin', 'room_manager', 'finance_manager'].includes(role)) {
      const housingRes = await getStaffHousingList({ page: 1, pageSize: 10, status: 'pending' })
      const pendingHousing = housingRes.data?.list || []
      pendingHousing.forEach((item: any) => {
        newNotifications.push({
          id: id++,
          type: 'housing',
          title: '住宿申请待审批',
          content: `${item.staffName || '教职工'}申请住宿`,
          time: item.createdAt || '刚刚',
          read: false,
          link: '/business/housing/staff-housing',
          recordId: item.id
        })
      })
    }
    
    notifications.value = newNotifications
  } catch (error) {
    console.error('加载通知失败:', error)
  }
}

// 轮询定时器
let notificationTimer: ReturnType<typeof setInterval> | null = null

// 页面加载时获取通知并启动轮询
onMounted(() => {
  loadNotifications()
  // 每30秒刷新一次通知
  notificationTimer = setInterval(() => {
    loadNotifications()
  }, 30000)
})

// 组件卸载时清理定时器
onUnmounted(() => {
  if (notificationTimer) {
    clearInterval(notificationTimer)
    notificationTimer = null
  }
})

function getNotificationIcon(type: string) {
  const iconMap: Record<string, any> = {
    approval: Document,
    borrow: Document,
    classroom: Document,
    maintenance: Tools,
    housing: House,
    system: Warning
  }
  return iconMap[type] || Bell
}

function handleNotificationClick(item: Notification) {
  item.read = true
  // 如果有跳转链接，则跳转到对应页面
  if (item.link) {
    router.push(item.link)
  }
}

function markAllRead() {
  notifications.value.forEach(n => n.read = true)
  ElMessage.success('已全部标记为已读')
}

// 面包屑
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta?.title)
  return matched.map(item => ({
    path: item.path,
    title: item.meta?.title as string
  })).slice(1) // 去掉第一个（首页）
})

// 切换侧边栏
function toggleSidebar() {
  appStore.toggleSidebar()
}

// 获取角色名称
function getRoleName(role: string): string {
  const roleMap: Record<string, string> = {
    system_admin: '系统管理员',
    student: '学生',
    teacher: '教师',
    equip_manager: '器材管理员',
    room_manager: '房产管理员',
    finance_manager: '财务管理员',
    maintenance_staff: '维保人员'
  }
  return roleMap[role] || role
}

// 下拉菜单命令
async function handleCommand(command: string) {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch {
      // 取消退出
    }
  }
}
</script>

<style scoped>
.navbar {
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
  transition: color 0.3s;
}

.collapse-btn:hover {
  color: #409eff;
}

.navbar-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

  .user-info:hover {
    background-color: #f5f7fa;
  }

  .user-name {
    font-size: 14px;
    color: #606266;
  }

  /* 通知样式 */
  .notification-badge {
    margin-right: 20px;
    cursor: pointer;
  }

  .notification-icon {
    color: #606266;
    transition: color 0.3s;
  }

  .notification-icon:hover {
    color: #409eff;
  }

  .notification-panel {
    margin: -12px;
  }

  .notification-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    border-bottom: 1px solid #eee;
    font-weight: 500;
  }

  .notification-empty {
    text-align: center;
    padding: 40px 20px;
    color: #999;
  }

  .notification-item {
    display: flex;
    gap: 12px;
    padding: 12px 16px;
    cursor: pointer;
    transition: background-color 0.2s;
    border-bottom: 1px solid #f5f5f5;
  }

  .notification-item:hover {
    background-color: #f5f7fa;
  }

  .notification-item.unread {
    background-color: #ecf5ff;
  }

  .notification-type-icon {
    flex-shrink: 0;
    margin-top: 2px;
    color: #409eff;
  }

  .notification-content {
    flex: 1;
    min-width: 0;
  }

  .notification-title {
    font-size: 14px;
    color: #303133;
    margin-bottom: 4px;
  }

  .notification-desc {
    font-size: 12px;
    color: #909399;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

.notification-time {
  font-size: 12px;
  color: #c0c4cc;
}

/* 主题切换按钮 */
.theme-toggle {
  margin-right: 20px;
  cursor: pointer;
  color: #606266;
  transition: color 0.3s, transform 0.3s;
}

.theme-toggle:hover {
  color: #409eff;
  transform: rotate(15deg);
}
</style>