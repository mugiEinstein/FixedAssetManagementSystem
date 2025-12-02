<template>
  <div class="internal-activity-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="主办部门">
        <el-input v-model="searchForm.organizerDept" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="活动名称">
        <el-input v-model="searchForm.eventName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
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
        @add="handleAdd"
        @page-change="handlePageChange"
        :show-export="false"
    >
      <el-table-column prop="eventId" label="活动编号" width="130" />
      <el-table-column prop="organizerDept" label="主办部门" width="120" />
      <el-table-column prop="applicantName" label="申请人" width="100" />
      <el-table-column prop="eventName" label="活动名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="venueName" label="场地" width="120" />
      <el-table-column prop="eventDate" label="活动日期" width="110" />
      <el-table-column label="时间段" width="130">
        <template #default="{ row }">
          {{ row.startTime }} - {{ row.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="expectedAttendees" label="预计人数" width="100" />
      <el-table-column label="费用(元)" width="150">
        <template #default="{ row }">
          <div>
            <span style="text-decoration: line-through; color: #909399;">¥{{ row.venueRent }}</span>
            <span style="color: #67c23a; margin-left: 8px;">¥{{ row.discountFee }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <AssetStatusTag :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">详情</el-button>
          <el-button
              v-if="row.status === 'pending'"
              type="success"
              link
              @click="handleApprove(row)"
          >
            审核
          </el-button>
        </template>
      </el-table-column>
    </DataTable>

    <!-- 新增申请弹窗 -->
    <el-dialog v-model="dialogVisible" title="校内活动申请" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主办部门" prop="organizerDept">
              <el-select v-model="form.organizerDept" placeholder="请选择" style="width: 100%">
                <el-option label="计算机学院团委" value="计算机学院团委" />
                <el-option label="学生会" value="学生会" />
                <el-option label="社团联合会" value="社团联合会" />
                <el-option label="研究生会" value="研究生会" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请人" prop="applicantName">
              <el-input v-model="form.applicantName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="活动名称" prop="eventName">
          <el-input v-model="form.eventName" placeholder="请输入活动名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择场地" prop="venueId">
              <el-select v-model="form.venueId" placeholder="请选择场地" style="width: 100%" @change="handleVenueChange">
                <el-option label="教职工礼堂 (¥3000/小时)" value="V-001" />
                <el-option label="学术报告厅 (¥2000/小时)" value="V-002" />
                <el-option label="多功能厅 (¥1000/小时)" value="V-004" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计人数" prop="expectedAttendees">
              <el-input-number v-model="form.expectedAttendees" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="活动日期" prop="eventDate">
              <el-date-picker
                  v-model="form.eventDate"
                  type="date"
                  placeholder="选择日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                  :disabled-date="disabledDate"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="开始" prop="startTime">
              <el-time-select
                  v-model="form.startTime"
                  :max-time="form.endTime"
                  placeholder="开始"
                  start="08:00"
                  step="01:00"
                  end="20:00"
                  style="width: 100%"
                  @change="calculateFees"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="结束" prop="endTime">
              <el-time-select
                  v-model="form.endTime"
                  :min-time="form.startTime"
                  placeholder="结束"
                  start="09:00"
                  step="01:00"
                  end="22:00"
                  style="width: 100%"
                  @change="calculateFees"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">费用信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原价">
              <el-input :value="'¥ ' + form.venueRent.toLocaleString()" disabled style="text-decoration: line-through;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优惠价(6折)">
              <el-input :value="'¥ ' + form.discountFee.toLocaleString()" disabled style="color: #67c23a; font-weight: bold;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-alert type="success" :closable="false" show-icon>
          <template #title>
            校内活动享受6折优惠：¥{{ form.venueRent }} × 60% = ¥{{ form.discountFee }}
          </template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="活动详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="活动编号">{{ currentRow?.eventId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <AssetStatusTag v-if="currentRow" :status="currentRow.status" />
        </el-descriptions-item>
        <el-descriptions-item label="主办部门">{{ currentRow?.organizerDept }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRow?.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="活动名称" :span="2">{{ currentRow?.eventName }}</el-descriptions-item>
        <el-descriptions-item label="活动场地">{{ currentRow?.venueName }}</el-descriptions-item>
        <el-descriptions-item label="预计人数">{{ currentRow?.expectedAttendees }} 人</el-descriptions-item>
        <el-descriptions-item label="活动日期">{{ currentRow?.eventDate }}</el-descriptions-item>
        <el-descriptions-item label="活动时段">{{ currentRow?.startTime }} - {{ currentRow?.endTime }}</el-descriptions-item>
        <el-descriptions-item label="原价">¥ {{ currentRow?.venueRent }}</el-descriptions-item>
        <el-descriptions-item label="优惠价">
          <span style="color: #67c23a; font-weight: bold;">¥ {{ currentRow?.discountFee }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog v-model="approveVisible" title="审核" width="500px">
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="approveForm.approved">
            <el-radio :value="true">通过</el-radio>
            <el-radio :value="false">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="!approveForm.approved" label="驳回原因">
          <el-input v-model="approveForm.reason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApprove">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import SearchBar from '@/components/common/SearchBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'
import { calculateInternalActivityFee } from '@/utils/fee-calculator'
import { getInternalEventList, submitInternalEvent, reviewInternalEvent } from '@/api/activity-venue'
import type { InternalEvent } from '@/types/business'

// 场地单价
const venueRates: Record<string, number> = {
  'V-001': 3000,
  'V-002': 2000,
  'V-004': 1000
}

// 搜索表单
const searchForm = reactive({
  organizerDept: '',
  eventName: '',
  status: ''
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<InternalEvent[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const detailVisible = ref(false)
const approveVisible = ref(false)
const currentRow = ref<InternalEvent | null>(null)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const form = reactive({
  organizerDept: '',
  applicantName: '',
  eventName: '',
  venueId: '',
  expectedAttendees: 50,
  eventDate: '',
  startTime: '',
  endTime: '',
  venueRent: 0,
  discountFee: 0
})

// 表单验证规则
const formRules: FormRules = {
  organizerDept: [{ required: true, message: '请选择主办部门', trigger: 'change' }],
  applicantName: [{ required: true, message: '请输入申请人', trigger: 'blur' }],
  eventName: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  venueId: [{ required: true, message: '请选择场地', trigger: 'change' }],
  eventDate: [{ required: true, message: '请选择活动日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

// 审核表单
const approveForm = reactive({
  approved: true,
  reason: ''
})

// 禁用过去的日期
function disabledDate(date: Date) {
  return date.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 场地变化
function handleVenueChange() {
  calculateFees()
}

// 计算费用
function calculateFees() {
  let duration = 0
  if (form.startTime && form.endTime) {
    const start = parseInt(form.startTime.split(':')[0])
    const end = parseInt(form.endTime.split(':')[0])
    duration = end - start
  }

  const rate = venueRates[form.venueId] || 0
  form.venueRent = rate * duration
  form.discountFee = calculateInternalActivityFee(rate, duration)
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getInternalEventList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载活动记录失败:', error)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  queryParams.page = 1
  loadData()
}

// 重置
function handleReset() {
  searchForm.organizerDept = ''
  searchForm.eventName = ''
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

// 新增
function handleAdd() {
  Object.assign(form, {
    organizerDept: '',
    applicantName: '',
    eventName: '',
    venueId: '',
    expectedAttendees: 50,
    eventDate: '',
    startTime: '',
    endTime: '',
    venueRent: 0,
    discountFee: 0
  })
  dialogVisible.value = true
}

// 查看详情
function handleView(row: InternalEvent) {
  currentRow.value = row
  detailVisible.value = true
}

// 审核
function handleApprove(row: InternalEvent) {
  currentRow.value = row
  approveForm.approved = true
  approveForm.reason = ''
  approveVisible.value = true
}

// 提交审核
async function submitApprove() {
  if (!currentRow.value) return
  try {
    await reviewInternalEvent(currentRow.value.eventId, {
      approved: approveForm.approved,
      reason: approveForm.reason
    })
    ElMessage.success('审核完成')
    approveVisible.value = false
    loadData()
  } catch (error: any) {
    console.error('审核失败:', error)
    ElMessage.error(error.message || '审核失败')
  }
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      await submitInternalEvent(form)
      ElMessage.success('申请提交成功')
      dialogVisible.value = false
      loadData()
    } catch (error: any) {
      console.error('提交失败:', error)
      ElMessage.error(error.message || '提交失败')
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.internal-activity-page {
  height: 100%;
}
</style>