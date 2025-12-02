<template>
  <div class="sidebar" :class="{ 'is-collapsed': collapsed }">
    <!-- Logo -->
    <div class="logo">
      <img src="@/assets/school-logo.png" alt="校徽" class="logo-img" />
      <span v-show="!collapsed" class="logo-text">固定资产管理</span>
    </div>

    <!-- 菜单 -->
    <el-scrollbar>
      <el-menu
          :default-active="activeMenu"
          :collapse="collapsed"
          :unique-opened="true"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          router
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <!-- 没有子菜单 -->
          <el-menu-item
              v-if="!route.children || route.children.length === 1"
              :index="getMenuIndex(route)"
          >
            <el-icon v-if="getMenuIcon(route)">
              <component :is="getMenuIcon(route)" />
            </el-icon>
            <template #title>{{ getMenuTitle(route) }}</template>
          </el-menu-item>

          <!-- 有子菜单 -->
          <el-sub-menu v-else :index="route.path">
            <template #title>
              <el-icon v-if="route.meta?.icon">
                <component :is="route.meta.icon" />
              </el-icon>
              <span>{{ route.meta?.title }}</span>
            </template>
            <el-menu-item
                v-for="child in route.children"
                :key="child.path"
                :index="`${route.path}/${child.path}`"
            >
              <el-icon v-if="child.meta?.icon">
                <component :is="child.meta.icon" />
              </el-icon>
              <template #title>{{ child.meta?.title }}</template>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import type { RouteRecordRaw } from 'vue-router'
import type { UserRole } from '@/types/user'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const collapsed = computed(() => appStore.sidebarCollapsed)
const activeMenu = computed(() => route.path)

// 检查用户是否有权限访问路由
function hasPermission(roles: UserRole[] | undefined): boolean {
  if (!roles || roles.length === 0) return true  // 没有设置角色限制，所有人可访问
  const userRole = userStore.role as UserRole
  if (!userRole) return false
  return roles.includes(userRole)
}

// 过滤有权限的子路由
function filterChildren(children: RouteRecordRaw[] | undefined): RouteRecordRaw[] {
  if (!children) return []
  return children.filter(child => {
    const roles = child.meta?.roles as UserRole[] | undefined
    return hasPermission(roles)
  })
}

// 获取菜单路由（过滤掉隐藏的路由和无权限的路由）
const menuRoutes = computed(() => {
  const routes = router.options.routes
  const userRole = userStore.role as UserRole
  
  return routes.filter(r => {
    // 过滤隐藏路由
    if (r.meta?.hidden) return false
    // 过滤登录、404等页面
    if (['/login', '/404'].includes(r.path)) return false
    // 过滤没有 component 的路由（如重定向）
    if (!r.component && !r.children) return false
    
    // 检查父级路由权限
    const parentRoles = r.meta?.roles as UserRole[] | undefined
    if (parentRoles && parentRoles.length > 0 && userRole) {
      if (!parentRoles.includes(userRole)) return false
    }
    
    // 检查是否有至少一个可访问的子路由
    if (r.children && r.children.length > 0) {
      const accessibleChildren = filterChildren(r.children)
      if (accessibleChildren.length === 0) return false
    }
    
    return true
  }).map(r => {
    // 过滤子路由
    if (r.children) {
      return {
        ...r,
        children: filterChildren(r.children)
      }
    }
    return r
  })
})

// 获取菜单索引
function getMenuIndex(route: RouteRecordRaw): string {
  if (route.children && route.children.length === 1) {
    const basePath = route.path === '/' ? '' : route.path
    return `${basePath}/${route.children[0].path}`
  }
  return route.redirect as string || route.path
}

// 获取菜单图标
function getMenuIcon(route: RouteRecordRaw): string | undefined {
  if (route.children && route.children.length === 1) {
    return route.children[0].meta?.icon as string
  }
  return route.meta?.icon as string
}

// 获取菜单标题
function getMenuTitle(route: RouteRecordRaw): string {
  if (route.children && route.children.length === 1) {
    return route.children[0].meta?.title as string || ''
  }
  return route.meta?.title as string || ''
}
</script>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 220px;
  background-color: #304156;
  transition: width 0.3s ease;
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.sidebar.is-collapsed {
  width: 64px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  background-color: #263445;
  color: #fff;
  gap: 10px;
  overflow: hidden;
}

.logo-img {
  width: 36px;
  height: 36px;
  object-fit: contain;
  border-radius: 4px;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.el-menu {
  border-right: none;
}

.el-scrollbar {
  flex: 1;
}

:deep(.el-menu--collapse) {
  width: 64px;
}

:deep(.el-sub-menu__title),
:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}
</style>