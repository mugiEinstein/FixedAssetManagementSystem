<template>
  <div class="office-room-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="申请人">
        <el-input v-model="searchForm.applicantName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="职称等级">
        <el-select v-model="searchForm.staffLevel" placeholder="请选择" clearable>
          <el-option label="教授" value="professor" />
          <el-option label="副教授" value="associate_prof" />
          <el-option label="讲师" value="lecturer" />
          <el-option label="处级正职" value="dept_leader" />
          <el-option label="处级副职" value="dept_deputy" />
          <el-option label="其他人员" value="staff" />
        </el-select>
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
      <el-table-column prop="allocationId" label="申请编号" width="130" />
      <el-table-column prop="applicantName" label="申请人" width="100" />
      <el-table-column prop="department" label="所属部门" width="120" />
      <el-table-column prop="staffLevel" label="职称等级" width="100">
        <template #default="{ row }">
          {{ getStaffLevelName(row.staffLevel) }}
        </template>
      </el-table-column>
      <el-table-column label="基准面积(㎡)" width="120">
        <template #default="{ row }">
          {{ row.baseMinArea }} - {{ row.baseMaxArea }}
        </template>
      </el-table-column>
      <el-table-column prop="actualArea" label="分配面积(㎡)" width="120" />
      <el-table-column prop="excessArea" label="超面积(㎡)" width="110">
        <template #default="{ row }">
          <span :style="{ color: row.excessArea > 0 ? '#e6a23c' : '' }">
            {{ row.excessArea }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="excessFee" label="超面积费(元/月)" width="130">
        <template #default="{ row }">
          <span :style="{ color: row.excessFee > 0 ? '#e6a23c' : '' }">
            {{ row.excessFee > 0 ? row.excessFee : '-' }}
          </span>
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
    <el-dialog v-model="dialogVisible" title="办公用房申请" width="650px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请人" prop="applicantName">
              <el-input v-model="form.applicantName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门" prop="department">
              <el-input v-model="form.department" placeholder="请输入部门" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="职称等级" prop="staffLevel">
          <el-select v-model="form.staffLevel" placeholder="请选择职称" style="width: 100%" @change="handleStaffLevelChange">
            <el-option label="教授" value="professor" />
            <el-option label="副教授" value="associate_prof" />
            <el-option label="讲师" value="lecturer" />
            <el-option label="校级正职" value="school_leader" />
            <el-option label="校级副职" value="school_deputy" />
            <el-option label="处级正职" value="dept_leader" />
            <el-option label="处级副职" value="dept_deputy" />
            <el-option label="其他人员" value="staff" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">面积信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="基准面积范围">
              <el-input :value="`${form.baseMinArea} - ${form.baseMaxArea} ㎡`" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请面积(㎡)" prop="actualArea">
              <el-input-number
                  v-model="form.actualArea"
                  :min="form.baseMinArea"
                  :precision="2"
                  style="width: 100%"
                  @change="calculateExcessFee"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">费用预算</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="超出面积">
              <el-input :value="`${form.excessArea} ㎡`" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="超面积费用">
              <el-input
                  :value="form.excessFee > 0 ? `¥${form.excessFee}/月` : '无需缴费'"
                  disabled
                  :style="{ color: form.excessFee > 0 ?  '#e6a23c' : '#67c23a' }"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-alert
            v-if="form.excessFee > 0"
            type="warning"
            :closable="false"
            show-icon
        >
          <template #title>
            超面积费用计算：({{ form.actualArea }} - {{ form.baseMaxArea }}) × 50元/㎡/月 = {{ form.excessFee }}元/月
          </template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申请详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请编号">{{ currentRow?.allocationId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <AssetStatusTag v-if="currentRow" :status="currentRow.status" />
        </el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRow?.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="所属部门">{{ currentRow?.department }}</el-descriptions-item>
        <el-descriptions-item label="职称等级">{{ getStaffLevelName(currentRow?.staffLevel) }}</el-descriptions-item>
        <el-descriptions-item label="基准面积">{{ currentRow?.baseMinArea }} - {{ currentRow?.baseMaxArea }} ㎡</el-descriptions-item>
        <el-descriptions-item label="分配面积">{{ currentRow?.actualArea }} ㎡</el-descriptions-item>
        <el-descriptions-item label="超出面积">{{ currentRow?.excessArea }} ㎡</el-descriptions-item>
        <el-descriptions-item label="超面积费用">
          <span :style="{ color: (currentRow?.excessFee || 0) > 0 ? '#e6a23c' : '' }">
            {{ (currentRow?.excessFee || 0) > 0 ? `¥${currentRow?.excessFee}/月` : '无' }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="分配房间">{{ currentRow?.roomName || '待分配' }}</el-descriptions-item>
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
        <el-form-item v-if="approveForm.approved" label="分配房间">
          <el-select v-model="approveForm.roomId" placeholder="请选择房间" style="width: 100%">
            <el-option label="主楼501室 (18㎡)" value="RM-501" />
            <el-option label="主楼502室 (15㎡)" value="RM-502" />
            <el-option label="主楼503室 (12㎡)" value="RM-503" />
          </el-select>
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
import { getBaseAreaByLevel, calculateExcessAreaFee } from '@/utils/fee-calculator'
import { getOfficeRoomList, submitOfficeRoomApplication, reviewOfficeRoomApplication } from '@/api/office-room'
import type { OfficeRoomAllocation } from '@/types/business'

// 搜索表单
const searchForm = reactive({
  applicantName: '',
  staffLevel: '',
  status: ''
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<OfficeRoomAllocation[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const detailVisible = ref(false)
const approveVisible = ref(false)
const currentRow = ref<OfficeRoomAllocation | null>(null)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const form = reactive({
  applicantName: '',
  department: '',
  staffLevel: '',
  baseMinArea: 0,
  baseMaxArea: 0,
  actualArea: 0,
  excessArea: 0,
  excessFee: 0
})

// 表单验证规则
const formRules: FormRules = {
  applicantName: [{ required: true, message: '请输入申请人姓名', trigger: 'blur' }],
  department: [{ required: true, message: '请输入所属部门', trigger: 'blur' }],
  staffLevel: [{ required: true, message: '请选择职称等级', trigger: 'change' }],
  actualArea: [{ required: true, message: '请输入申请面积', trigger: 'blur' }]
}

// 审核表单
const approveForm = reactive({
  approved: true,
  roomId: '',
  reason: ''
})

// 获取职称等级名称
function getStaffLevelName(level?: string): string {
  const map: Record<string, string> = {
    professor: '教授',
    associate_prof: '副教授',
    lecturer: '讲师',
    school_leader: '校级正职',
    school_deputy: '校级副职',
    dept_leader: '处级正职',
    dept_deputy: '处级副职',
    staff: '其他人员'
  }
  return level ? map[level] || level : ''
}

// 职称变化时更新基准面积
function handleStaffLevelChange(level: string) {
  const { min, max } = getBaseAreaByLevel(level)
  form.baseMinArea = min
  form.baseMaxArea = max
  form.actualArea = min
  calculateExcessFee()
}

// 计算超面积费用
function calculateExcessFee() {
  form.excessArea = Math.max(form.actualArea - form.baseMaxArea, 0)
  form.excessFee = calculateExcessAreaFee(form.actualArea, form.baseMaxArea, 1)
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getOfficeRoomList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载办公用房记录失败:', error)
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
  searchForm.applicantName = ''
  searchForm.staffLevel = ''
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
    applicantName: '',
    department: '',
    staffLevel: '',
    baseMinArea: 0,
    baseMaxArea: 0,
    actualArea: 0,
    excessArea: 0,
    excessFee: 0
  })
  dialogVisible.value = true
}

// 查看详情
function handleView(row: OfficeRoomAllocation) {
  currentRow.value = row
  detailVisible.value = true
}

// 审核
function handleApprove(row: OfficeRoomAllocation) {
  currentRow.value = row
  approveForm.approved = true
  approveForm.roomId = ''
  approveForm.reason = ''
  approveVisible.value = true
}

// 提交审核
async function submitApprove() {
  if (!currentRow.value) return
  try {
    await reviewOfficeRoomApplication(currentRow.value.allocationId, {
      approved: approveForm.approved,
      roomId: approveForm.roomId,
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
      await submitOfficeRoomApplication(form as any)
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
.office-room-page {
  height: 100%;
}
</style>