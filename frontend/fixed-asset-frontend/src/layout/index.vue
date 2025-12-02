<template>
  <div class="app-layout">
    <!-- 侧边栏 -->
    <Sidebar />

    <!-- 主内容区 -->
    <div class="main-container" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <!-- 顶部导航 -->
      <Navbar />

      <!-- 内容区 -->
      <AppMain />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'
import AppMain from './AppMain.vue'

const appStore = useAppStore()
const sidebarCollapsed = computed(() => appStore.sidebarCollapsed)
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
  width: 100%;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 220px;
  transition: margin-left 0.3s ease;
  overflow: hidden;
}

.main-container.sidebar-collapsed {
  margin-left: 64px;
}
</style>