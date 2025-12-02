<template>
  <div class="external-activity-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="主办单位">
        <el-input v-model="searchForm.organizer" placeholder="请输入" clearable />
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
      <el-table-column prop="organizer" label="主办单位" min-width="150" show-overflow-tooltip />
      <el-table-column prop="eventName" label="活动名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="venueName" label="场地" width="120" />
      <el-table-column prop="eventDate" label="活动日期" width="110" />
      <el-table-column label="时间段" width="130">
        <template #default="{ row }">
          {{ row.startTime }} - {{ row.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="expectedAttendees" label="预计人数" width="100" />
      <el-table-column prop="totalFee" label="费用总计(元)" width="120">
        <template #default="{ row }">
          ¥ {{ row.totalFee ? Number(row.totalFee).toLocaleString() : '0' }}
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
              v-if="row.status === 'pending' || row.status === '申请中'"
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
    <el-dialog v-model="dialogVisible" title="校外活动申请" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="110px">
        <el-divider content-position="left">主办单位信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主办单位" prop="organizer">
              <el-input v-model="form.organizer" placeholder="请输入单位名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" style="width: 50%" />
        </el-form-item>

        <el-divider content-position="left">活动信息</el-divider>
        <el-form-item label="活动名称" prop="eventName">
          <el-input v-model="form.eventName" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动简介" prop="eventDescription">
          <el-input v-model="form.eventDescription" type="textarea" :rows="3" placeholder="请简要描述活动内容" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择场地" prop="venueId">
              <el-select v-model="form.venueId" placeholder="请选择场地" style="width: 100%" @change="handleVenueChange">
                <el-option label="教职工礼堂 (¥3000/小时)" value="V-001" />
                <el-option label="学术报告厅 (¥2000/小时)" value="V-002" />
                <el-option label="体育馆 (¥5000/小时)" value="V-003" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计人数" prop="expectedAttendees">
              <el-input-number v-model="form.expectedAttendees" :min="1" style="width: 100%" @change="calculateFees" />
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
            <el-form-item label="开始时间" prop="startTime">
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
            <el-form-item label="结束时间" prop="endTime">
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

        <el-divider content-position="left">费用明细</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="场地租金">
              <el-input :value="'¥ ' + form.venueRent.toLocaleString()" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设备使用费">
              <el-input-number v-model="form.equipmentFee" :min="0" style="width: 100%" @change="calculateTotalFee" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="安保服务费">
              <el-input :value="'¥ ' + form.securityFee.toLocaleString()" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="费用合计">
          <el-input :value="'¥ ' + form.totalFee.toLocaleString()" disabled style="font-size: 18px; font-weight: bold; width: 200px;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="活动详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="活动编号">{{ currentRow?.eventId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <AssetStatusTag v-if="currentRow" :status="currentRow.status" />
        </el-descriptions-item>
        <el-descriptions-item label="主办单位">{{ currentRow?.organizer }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentRow?.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="活动名称" :span="2">{{ currentRow?.eventName }}</el-descriptions-item>
        <el-descriptions-item label="活动场地">{{ currentRow?.venueName }}</el-descriptions-item>
        <el-descriptions-item label="预计人数">{{ currentRow?.expectedAttendees }} 人</el-descriptions-item>
        <el-descriptions-item label="活动日期">{{ currentRow?.eventDate }}</el-descriptions-item>
        <el-descriptions-item label="活动时段">{{ currentRow?.startTime }} - {{ currentRow?.endTime }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">费用明细</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="场地租金">¥ {{ currentRow?.venueRent?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="设备使用费">¥ {{ currentRow?.equipmentFee?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="安保服务费">¥ {{ currentRow?.securityFee?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="费用合计">
          <span style="font-size: 18px; font-weight: bold; color: #409eff;">
            ¥ {{ currentRow?.totalFee?.toLocaleString() }}
          </span>
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
        <el-form-item v-if="! approveForm.approved" label="驳回原因">
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
import { calculateSecurityFee } from '@/utils/fee-calculator'
import { getExternalEventList, submitExternalEvent, reviewExternalQualification, reviewExternalVenue, type ExternalEventBooking } from '@/api/activity-venue'

// 场地单价
const venueRates: Record<string, number> = {
  'V-001': 3000,
  'V-002': 2000,
  'V-003': 5000
}

// 搜索表单
const searchForm = reactive({
  organizer: '',
  eventName: '',
  status: ''
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<ExternalEventBooking[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const detailVisible = ref(false)
const approveVisible = ref(false)
const currentRow = ref<ExternalEventBooking | null>(null)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const form = reactive({
  organizer: '',
  contactPerson: '',
  contactPhone: '',
  eventName: '',
  eventDescription: '',
  venueId: '',
  expectedAttendees: 100,
  eventDate: '',
  startTime: '',
  endTime: '',
  venueRent: 0,
  equipmentFee: 0,
  securityFee: 0,
  totalFee: 0
})

// 表单验证规则
const formRules: FormRules = {
  organizer: [{ required: true, message: '请输入主办单位', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
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
  // 计算时长
  let duration = 0
  if (form.startTime && form.endTime) {
    const start = parseInt(form.startTime.split(':')[0])
    const end = parseInt(form.endTime.split(':')[0])
    duration = end - start
  }

  // 场地租金
  const rate = venueRates[form.venueId] || 0
  form.venueRent = rate * duration

  // 安保费
  form.securityFee = calculateSecurityFee(form.expectedAttendees)

  // 总费用
  calculateTotalFee()
}

// 计算总费用
function calculateTotalFee() {
  form.totalFee = form.venueRent + form.equipmentFee + form.securityFee
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getExternalEventList({
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
  searchForm.organizer = ''
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
    organizer: '',
    contactPerson: '',
    contactPhone: '',
    eventName: '',
    eventDescription: '',
    venueId: '',
    expectedAttendees: 100,
    eventDate: '',
    startTime: '',
    endTime: '',
    venueRent: 0,
    equipmentFee: 0,
    securityFee: 0,
    totalFee: 0
  })
  dialogVisible.value = true
}

// 查看详情
function handleView(row: ExternalEventBooking) {
  currentRow.value = row
  detailVisible.value = true
}

// 审核
function handleApprove(row: ExternalEventBooking) {
  currentRow.value = row
  approveForm.approved = true
  approveForm.reason = ''
  approveVisible.value = true
}

// 提交审核
async function submitApprove() {
  if (!currentRow.value) return
  try {
    const reviewData = {
      approved: approveForm.approved,
      reason: approveForm.reason
    }
    await reviewExternalQualification(currentRow.value.eventId, reviewData)
    await reviewExternalVenue(currentRow.value.eventId, reviewData)
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
      await submitExternalEvent(form)
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
.external-activity-page {
  height: 100%;
}
</style>