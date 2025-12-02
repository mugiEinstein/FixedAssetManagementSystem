<template>
  <div class="dashboard">
    <!-- 欢迎信息 -->
    <div class="welcome-card">
      <h2>欢迎回来，{{ userStore.realName }}</h2>
      <p>{{ currentTime }}</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #409eff;">
            <el-icon :size="28"><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.buildingCount }}</div>
            <div class="stat-label">建筑总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #67c23a;">
            <el-icon :size="28"><Monitor /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.equipmentCount }}</div>
            <div class="stat-label">器材总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #e6a23c;">
            <el-icon :size="28"><Coin /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ formatValue(stats.totalValue) }}</div>
            <div class="stat-label">资产总值</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #f56c6c;">
            <el-icon :size="28"><Van /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.vehicleCount }}</div>
            <div class="stat-label">车辆总数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>资产状态分布</span>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>各楼宇资产价值排名</span>
          </template>
          <div ref="barChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-card shadow="hover" class="quick-links">
      <template #header>
        <span>快捷入口</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="4" v-for="link in quickLinks" :key="link.path">
          <div class="quick-link-item" @click="router.push(link.path)">
            <el-icon :size="32" :color="link.color">
              <component :is="link.icon" />
            </el-icon>
            <span>{{ link.title }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getDashboardStats } from '@/api/dashboard'
import * as echarts from 'echarts'

const router = useRouter()
const userStore = useUserStore()

// 当前时间
const currentTime = ref('')
let timer: number

// 统计数据（匹配后端返回的数据结构）
const stats = reactive({
  buildingCount: 0,
  equipmentCount: 0,
  vehicleCount: 0,
  totalValue: 0,
  statusDistribution: [] as Array<{ name: string; value: number }>
})

// 加载统计数据
async function loadStats() {
  try {
    const res = await getDashboardStats()
    if (res.data) {
      stats.buildingCount = res.data.buildingCount || 0
      stats.equipmentCount = res.data.equipmentCount || 0
      stats.vehicleCount = res.data.vehicleCount || 0
      stats.totalValue = Number(res.data.totalValue) || 0
      stats.statusDistribution = res.data.statusDistribution || []
      // 更新图表数据
      updateCharts()
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
    // 使用默认模拟数据
    stats.buildingCount = 56
    stats.equipmentCount = 1280
    stats.vehicleCount = 45
    stats.totalValue = 125800000
    stats.statusDistribution = [
      { name: '在用', value: 680 },
      { name: '闲置', value: 320 },
      { name: '维修中', value: 100 }
    ]
  }
}

// 图表容器
const pieChartRef = ref<HTMLElement>()
const barChartRef = ref<HTMLElement>()
let pieChart: echarts.ECharts
let barChart: echarts.ECharts

// 快捷入口（根据用户角色过滤）
const allQuickLinks = [
  { path: '/asset/building', title: '建筑管理', icon: 'OfficeBuilding', color: '#409eff', roles: ['system_admin', 'room_manager'] },
  { path: '/asset/equipment', title: '器材管理', icon: 'Monitor', color: '#67c23a', roles: ['system_admin', 'equip_manager'] },
  { path: '/business/equipment-borrow/apply', title: '借用申请', icon: 'Edit', color: '#e6a23c', roles: ['system_admin', 'student', 'teacher'] },
  { path: '/business/equipment-borrow/my-borrow', title: '我的借用', icon: 'Goods', color: '#e6a23c', roles: ['system_admin', 'student', 'teacher'] },
  { path: '/business/housing/classroom-query', title: '教室查询', icon: 'Search', color: '#909399', roles: ['system_admin', 'student', 'teacher'] },
  { path: '/maintenance/apply', title: '维保申请', icon: 'Tools', color: '#f56c6c', roles: ['system_admin', 'student', 'teacher', 'equip_manager', 'room_manager', 'maintenance_staff'] },
  { path: '/report/asset-statistics', title: '资产统计', icon: 'DataAnalysis', color: '#9c27b0', roles: ['system_admin', 'finance_manager'] },
  { path: '/inventory/check', title: '盘点计划', icon: 'List', color: '#00bcd4', roles: ['system_admin', 'finance_manager'] },
  { path: '/archive/equipment', title: '器材建档', icon: 'Box', color: '#4caf50', roles: ['system_admin', 'equip_manager'] },
  { path: '/archive/building', title: '房屋建档', icon: 'OfficeBuilding', color: '#2196f3', roles: ['system_admin', 'room_manager'] }
]

const quickLinks = computed(() => {
  const userRole = userStore.role
  if (!userRole) return []
  
  // 根据角色过滤快捷入口，最多显示6个
  return allQuickLinks
    .filter(link => link.roles.includes(userRole as any))
    .slice(0, 6)
})

// 格式化金额
function formatValue(value: number): string {
  if (value >= 100000000) {
    return (value / 100000000).toFixed(2) + '亿'
  } else if (value >= 10000) {
    return (value / 10000).toFixed(2) + '万'
  }
  return value.toString()
}

// 更新时间
function updateTime() {
  const now = new Date()
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long',
    hour: '2-digit',
    minute: '2-digit'
  }
  currentTime.value = now.toLocaleDateString('zh-CN', options)
}

// 初始化饼图
function initPieChart() {
  if (!pieChartRef.value) return

  pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '10%',
      top: 'center'
    },
    series: [
      {
        name: '资产状态',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        data: [
          { value: 680, name: '在用', itemStyle: { color: '#409eff' } },
          { value: 320, name: '闲置', itemStyle: { color: '#67c23a' } },
          { value: 180, name: '维修中', itemStyle: { color: '#e6a23c' } },
          { value: 100, name: '已报废', itemStyle: { color: '#f56c6c' } }
        ]
      }
    ]
  })
}

// 初始化柱状图
function initBarChart() {
  if (!barChartRef.value) return

  barChart = echarts.init(barChartRef.value)
  barChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: ['逸夫楼', '主楼', '图书馆', '实验楼', '体育馆', '学生中心']
    },
    yAxis: {
      type: 'value',
      name: '万元',
      axisLabel: {
        formatter: (value: number) => (value / 10000).toFixed(0)
      }
    },
    series: [
      {
        name: '资产价值',
        type: 'bar',
        data: [28500000, 24000000, 18500000, 15000000, 12000000, 8500000],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409eff' },
            { offset: 1, color: '#67c23a' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      }
    ]
  })
}

// 更新图表数据
function updateCharts() {
  if (pieChart && stats.statusDistribution.length > 0) {
    const colorMap: Record<string, string> = {
      '在用': '#409eff',
      '闲置': '#67c23a',
      '维修中': '#e6a23c',
      '已报废': '#f56c6c'
    }
    pieChart.setOption({
      series: [{
        data: stats.statusDistribution.map(item => ({
          value: item.value,
          name: item.name,
          itemStyle: { color: colorMap[item.name] || '#909399' }
        }))
      }]
    })
  }
}

// 窗口大小变化时重绘图表
function handleResize() {
  pieChart?.resize()
  barChart?.resize()
}

onMounted(() => {
  updateTime()
  timer = window.setInterval(updateTime, 60000)
  
  // 加载统计数据
  loadStats()

  // 初始化图表
  setTimeout(() => {
    initPieChart()
    initBarChart()
  }, 100)

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  clearInterval(timer)
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  barChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  min-height: 100%;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.welcome-card h2 {
  margin: 0 0 10px;
  font-size: 24px;
}

.welcome-card p {
  margin: 0;
  opacity: 0.8;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 16px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

.quick-links {
  margin-bottom: 20px;
}

.quick-link-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
}

.quick-link-item:hover {
  background: #f5f7fa;
}

.quick-link-item span {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}
</style>