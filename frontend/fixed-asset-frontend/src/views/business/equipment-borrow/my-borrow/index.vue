<template>
  <div class="my-borrow-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="器材名称">
        <el-input v-model="searchForm.equipmentName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待审核" value="pending" />
          <el-option label="使用中" value="in_use" />
          <el-option label="已归还" value="returned" />
          <el-option label="已逾期" value="overdue" />
        </el-select>
      </el-form-item>
    </SearchBar>

    <!-- 数据表格 -->
    <DataTable
        :data="tableData"
        :loading="loading"
        :total="total"
        :page="queryParams.page"
        :size="queryParams.pageSize"
        @page-change="handlePageChange"
        :show-export="false"
        :show-add="false"
    >
      <el-table-column prop="borrowId" label="借用单号" width="130" />
      <el-table-column prop="equipmentName" label="器材名称" min-width="180" show-overflow-tooltip />
      <el-table-column prop="borrowDate" label="借用日期" width="110" />
      <el-table-column prop="returnDate" label="应还日期" width="110" />
      <el-table-column prop="actualReturnDate" label="实际归还" width="110">
        <template #default="{ row }">
          {{ row.actualReturnDate || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="purpose" label="用途" min-width="150" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <AssetStatusTag :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">详情</el-button>
          <el-button
              v-if="row.status === 'in_use' || row.status === 'approved'"
              type="success"
              link
              @click="handleReturn(row)"
          >
            归还
          </el-button>
          <el-button
              v-if="row.status === 'pending'"
              type="danger"
              link
              @click="handleCancel(row)"
          >
            取消
          </el-button>
        </template>
      </el-table-column>
    </DataTable>

    <!-- 归还弹窗 -->
    <el-dialog v-model="returnDialogVisible" title="归还器材" width="500px">
      <el-form :model="returnForm" label-width="100px">
        <el-form-item label="器材名称">
          <span>{{ currentRow?.equipmentName }}</span>
        </el-form-item>
        <el-form-item label="器材状态">
          <el-radio-group v-model="returnForm.isNormal">
            <el-radio :value="true">完好无损</el-radio>
            <el-radio :value="false">有损坏</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="!returnForm.isNormal" label="损坏程度">
          <el-select v-model="returnForm.damageLevel" placeholder="请选择损坏程度" style="width: 100%">
            <el-option label="轻微损坏（可维修）" value="minor" />
            <el-option label="中度损坏（需更换部件）" value="moderate" />
            <el-option label="严重损坏（无法使用）" value="severe" />
            <el-option label="遗失" value="lost" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!returnForm.isNormal" label="损坏描述">
          <el-input v-model="returnForm.damageDesc" type="textarea" :rows="3" placeholder="请详细描述损坏情况" />
        </el-form-item>
        <el-form-item v-if="!returnForm.isNormal" label="赔偿金额">
          <el-input-number 
            v-model="returnForm.compensationAmount" 
            :min="0" 
            :precision="2"
            :step="100"
            style="width: 200px"
          />
          <span style="margin-left: 8px; color: #909399;">元</span>
          <el-button type="primary" link @click="calculateCompensation" style="margin-left: 16px;">
            自动估算
          </el-button>
        </el-form-item>
        <el-alert 
          v-if="!returnForm.isNormal && returnForm.compensationAmount > 0" 
          type="warning" 
          :closable="false"
          style="margin-bottom: 16px;"
        >
          <template #title>
            根据损坏程度，预估赔偿金额为 <strong>¥{{ returnForm.compensationAmount.toFixed(2) }}</strong>，
            实际赔偿金额以管理员审核为准。
          </template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="returnLoading" @click="confirmReturn">确认归还</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="借用详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="借用单号">{{ currentRow?.borrowId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <AssetStatusTag v-if="currentRow" :status="currentRow.status" />
        </el-descriptions-item>
        <el-descriptions-item label="器材名称" :span="2">{{ currentRow?.equipmentName }}</el-descriptions-item>
        <el-descriptions-item label="借用日期">{{ currentRow?.borrowDate }}</el-descriptions-item>
        <el-descriptions-item label="应还日期">{{ currentRow?.returnDate }}</el-descriptions-item>
        <el-descriptions-item label="实际归还">{{ currentRow?.actualReturnDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="违约金">
          <span :style="{ color: currentRow?.penaltyFee ? '#f56c6c' : '' }">
            {{ currentRow?.penaltyFee ? '¥' + currentRow.penaltyFee : '-' }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="借用用途" :span="2">{{ currentRow?.purpose }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentRow?.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="权限审核">
          <AssetStatusTag v-if="currentRow" :status="currentRow.permissionApproval" />
        </el-descriptions-item>
      </el-descriptions>

      <!-- 审批进度 -->
      <div style="margin-top: 20px;">
        <h4>审批进度</h4>
        <el-steps :active="getApprovalStep(currentRow)" finish-status="success" simple>
          <el-step title="提交申请" />
          <el-step title="权限审核" />
          <el-step title="器材确认" />
          <el-step title="领取使用" />
          <el-step title="归还验收" />
        </el-steps>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SearchBar from '@/components/common/SearchBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'
import { getMyBorrowList, cancelMyBorrow, returnMyEquipment } from '@/api/borrow'
import { useUserStore } from '@/stores/user'
import type { InternalEquipmentBorrow } from '@/types/business'

const userStore = useUserStore()

// 搜索表单
const searchForm = reactive({
  equipmentName: '',
  status: ''
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<InternalEquipmentBorrow[]>([])
const total = ref(0)
const loading = ref(false)

// 详情弹窗
const detailVisible = ref(false)
const currentRow = ref<InternalEquipmentBorrow | null>(null)

// 归还弹窗
const returnDialogVisible = ref(false)
const returnLoading = ref(false)
const returnForm = reactive({
  isNormal: true,
  damageLevel: '' as 'minor' | 'moderate' | 'severe' | 'lost' | '',
  damageDesc: '',
  compensationAmount: 0
})

// 加载数据 - 使用真实API，按当前用户ID过滤
async function loadData() {
  loading.value = true
  try {
    const res = await getMyBorrowList({
      ...queryParams,
      ...searchForm,
      applicantId: userStore.userId  // 按当前用户ID过滤
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载借用记录失败:', error)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 获取审批步骤
function getApprovalStep(row: InternalEquipmentBorrow | null): number {
  if (!row) return 0
  switch (row.status) {
    case 'pending': return 1
    case 'approved': return 3
    case 'in_use': return 4
    case 'returned': return 5
    default: return 0
  }
}

// 搜索
function handleSearch() {
  queryParams.page = 1
  loadData()
}

// 重置
function handleReset() {
  searchForm.equipmentName = ''
  searchForm.status = ''
  queryParams.page = 1
  loadData()
}

// 分页变化
function handlePageChange(page: number, size: number) {
  queryParams.page = page
  queryParams.pageSize = size
  loadData()
}

// 查看详情
function handleView(row: InternalEquipmentBorrow) {
  currentRow.value = row
  detailVisible.value = true
}

// 归还器材
function handleReturn(row: InternalEquipmentBorrow) {
  currentRow.value = row
  returnForm.isNormal = true
  returnForm.damageLevel = ''
  returnForm.damageDesc = ''
  returnForm.compensationAmount = 0
  returnDialogVisible.value = true
}

// 自动估算赔偿金额
function calculateCompensation() {
  if (!currentRow.value || returnForm.isNormal) {
    returnForm.compensationAmount = 0
    return
  }
  
  // 假设器材原值（实际应从器材信息中获取）
  const originalValue = 5000 // 模拟原值
  
  // 根据损坏程度计算赔偿比例
  const damageRates: Record<string, number> = {
    minor: 0.1,      // 轻微损坏：10%
    moderate: 0.3,   // 中度损坏：30%
    severe: 0.7,     // 严重损坏：70%
    lost: 1.0        // 遗失：100%
  }
  
  const rate = damageRates[returnForm.damageLevel] || 0
  returnForm.compensationAmount = Math.round(originalValue * rate * 100) / 100
  
  ElMessage.info(`根据${getDamageLevelText(returnForm.damageLevel)}，预估赔偿${rate * 100}%原值`)
}

// 获取损坏程度文本
function getDamageLevelText(level: string): string {
  const map: Record<string, string> = {
    minor: '轻微损坏',
    moderate: '中度损坏',
    severe: '严重损坏',
    lost: '遗失'
  }
  return map[level] || level
}

// 确认归还
async function confirmReturn() {
  if (!currentRow.value) return
  
  // 有损坏但未填写赔偿信息时提示
  if (!returnForm.isNormal && !returnForm.damageLevel) {
    ElMessage.warning('请选择损坏程度')
    return
  }
  
  returnLoading.value = true
  try {
    await returnMyEquipment(currentRow.value.borrowId, {
      isNormal: returnForm.isNormal,
      damageLevel: returnForm.damageLevel,
      damageDesc: returnForm.damageDesc,
      compensationAmount: returnForm.compensationAmount
    })
    
    if (!returnForm.isNormal && returnForm.compensationAmount > 0) {
      ElMessage.success(`归还成功，需赔偿 ¥${returnForm.compensationAmount.toFixed(2)}，请等待管理员确认`)
    } else {
      ElMessage.success('归还成功')
    }
    returnDialogVisible.value = false
    loadData()
  } catch (error: any) {
    console.error('归还失败:', error)
    ElMessage.error(error.message || '归还失败')
  } finally {
    returnLoading.value = false
  }
}

// 取消申请
async function handleCancel(row: InternalEquipmentBorrow) {
  try {
    await ElMessageBox.confirm('确定要取消该借用申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelMyBorrow(row.borrowId)
    ElMessage.success('已取消申请')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消失败:', error)
      ElMessage.error(error.message || '取消失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.my-borrow-page {
  height: 100%;
}
</style>