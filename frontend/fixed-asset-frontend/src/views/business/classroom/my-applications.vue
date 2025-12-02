<template>
  <div class="my-applications-page">
    <el-card>
      <template #header>
        <div class="header-content">
          <span>我的教室申请记录</span>
          <el-button type="primary" @click="$router.push('/business/housing/classroom-query')">
            <el-icon><Plus /></el-icon>
            新建申请
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="borrowId" label="申请单号" width="140" />
        <el-table-column prop="classroomId" label="教室" width="150">
          <template #default="{ row }">
            {{ row.roomName || row.classroomId }}
          </template>
        </el-table-column>
        <el-table-column prop="borrowPurpose" label="借用用途" width="120" />
        <el-table-column prop="borrowTime" label="借用时间" min-width="200" />
        <el-table-column prop="status" label="审批状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && tableData.length === 0" description="暂无申请记录" />
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申请详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="申请单号">{{ currentRow.borrowId }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="getStatusType(currentRow.status)">
            {{ getStatusText(currentRow.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="教室">{{ currentRow.roomName || currentRow.classroomId }}</el-descriptions-item>
        <el-descriptions-item label="借用用途">{{ currentRow.borrowPurpose }}</el-descriptions-item>
        <el-descriptions-item label="借用时间" :span="2">{{ currentRow.borrowTime }}</el-descriptions-item>
        <el-descriptions-item label="申请部门">{{ currentRow.applicantDept }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatDate(currentRow.createdAt) }}</el-descriptions-item>
      </el-descriptions>

      <!-- 审批进度 -->
      <div class="approval-progress" v-if="currentRow">
        <h4>审批进度</h4>
        <el-steps :active="getProgressStep(currentRow.status)" align-center>
          <el-step title="已提交" description="等待审批" />
          <el-step title="审批中" description="管理员审核" />
          <el-step :title="currentRow.status === 'rejected' ? '已驳回' : '已通过'" 
                   :description="currentRow.status === 'rejected' ? '申请未通过' : '可以使用教室'" 
                   :status="currentRow.status === 'rejected' ? 'error' : undefined" />
        </el-steps>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getClassroomBorrowList } from '@/api/classroom'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const tableData = ref<any[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const currentRow = ref<any | null>(null)

// 加载数据 - 使用userId进行精确筛选
async function loadData() {
  loading.value = true
  try {
    const res = await getClassroomBorrowList({ 
      page: 1, 
      pageSize: 50,
      // 优先使用userId进行精确筛选，确保数据隔离
      applicantId: userStore.userId || userStore.userInfo?.id,
      applicantName: userStore.realName || userStore.userInfo?.username
    })
    tableData.value = res.data?.list || []
  } catch (error) {
    console.error('加载申请记录失败:', error)
    tableData.value = []
  } finally {
    loading.value = false
  }
}

// 获取状态文本
function getStatusText(status: string): string {
  const map: Record<string, string> = {
    pending: '待审批',
    approved: '已通过',
    rejected: '已驳回'
  }
  return map[status] || status
}

// 获取状态标签类型
function getStatusType(status: string): string {
  const map: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return map[status] || 'info'
}

// 获取进度步骤
function getProgressStep(status: string): number {
  const map: Record<string, number> = {
    pending: 1,
    approved: 3,
    rejected: 3
  }
  return map[status] || 1
}

// 格式化日期
function formatDate(dateStr: string): string {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 查看详情
function handleView(row: any) {
  currentRow.value = row
  detailVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.my-applications-page {
  height: 100%;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.approval-progress {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.approval-progress h4 {
  margin-bottom: 16px;
  color: #303133;
}
</style>
