<template>
  <div class="classroom-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="申请部门">
        <el-input v-model="searchForm.applicantDept" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="教室名称">
        <el-input v-model="searchForm.roomName" placeholder="请输入" clearable />
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
      <el-table-column prop="borrowId" label="借用单号" width="130" />
      <el-table-column prop="applicantDept" label="申请部门" width="120" />
      <el-table-column prop="applicantName" label="申请人" width="100" />
      <el-table-column prop="roomName" label="教室名称" min-width="150" />
      <el-table-column prop="purpose" label="借用用途" width="120" show-overflow-tooltip />
      <el-table-column prop="borrowDate" label="借用日期" width="110" />
      <el-table-column label="借用时段" width="150">
        <template #default="{ row }">
          {{ row.startTime }} - {{ row.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="expectedAttendees" label="预计人数" width="100" />
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
    <el-dialog v-model="dialogVisible" title="教室借用申请" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请部门" prop="applicantDept">
              <el-input v-model="form.applicantDept" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请人" prop="applicantName">
              <el-input v-model="form.applicantName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="选择教室" prop="roomId">
          <el-select v-model="form.roomId" placeholder="请选择教室" style="width: 100%">
            <el-option
                v-for="item in roomOptions"
                :key="item.roomId"
                :label="`${item.roomName} (容量${item.capacity}人)`"
                :value="item.roomId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="借用用途" prop="purpose">
          <el-select v-model="form.purpose" placeholder="请选择用途" style="width: 100%">
            <el-option label="课程教学" value="课程教学" />
            <el-option label="班级活动" value="班级活动" />
            <el-option label="答辩" value="答辩" />
            <el-option label="讲座" value="讲座" />
            <el-option label="考试" value="考试" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="借用日期" prop="borrowDate">
              <el-date-picker
                  v-model="form.borrowDate"
                  type="date"
                  placeholder="选择日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                  :disabled-date="disabledDate"
              />
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
            <el-form-item label="开始时间" prop="startTime">
              <el-time-select
                  v-model="form.startTime"
                  :max-time="form.endTime"
                  placeholder="选择时间"
                  start="08:00"
                  step="00:30"
                  end="21:00"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-select
                  v-model="form.endTime"
                  :min-time="form.startTime"
                  placeholder="选择时间"
                  start="08:00"
                  step="00:30"
                  end="22:00"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="设备需求" prop="equipmentNeeds">
          <el-checkbox-group v-model="equipmentNeedsList">
            <el-checkbox label="投影仪" value="投影仪" />
            <el-checkbox label="音响" value="音响" />
            <el-checkbox label="麦克风" value="麦克风" />
            <el-checkbox label="白板" value="白板" />
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="借用详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="借用单号">{{ currentRow?.borrowId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <AssetStatusTag v-if="currentRow" :status="currentRow.status" />
        </el-descriptions-item>
        <el-descriptions-item label="申请部门">{{ currentRow?.applicantDept }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRow?.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="教室名称">{{ currentRow?.roomName }}</el-descriptions-item>
        <el-descriptions-item label="借用用途">{{ currentRow?.purpose }}</el-descriptions-item>
        <el-descriptions-item label="借用日期">{{ currentRow?.borrowDate }}</el-descriptions-item>
        <el-descriptions-item label="借用时段">{{ currentRow?.startTime }} - {{ currentRow?.endTime }}</el-descriptions-item>
        <el-descriptions-item label="预计人数">{{ currentRow?.expectedAttendees }} 人</el-descriptions-item>
        <el-descriptions-item label="设备需求">{{ currentRow?.equipmentNeeds || '无' }}</el-descriptions-item>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import SearchBar from '@/components/common/SearchBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'
import { getClassroomBorrowList, submitClassroomBorrow, reviewClassroomBorrow } from '@/api/classroom'
import { getRoomList } from '@/api/room'
import type { ClassroomBorrow } from '@/types/business'
import type { Room } from '@/types/asset'

// 搜索表单
const searchForm = reactive({
  applicantDept: '',
  roomName: '',
  status: ''
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<ClassroomBorrow[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const detailVisible = ref(false)
const approveVisible = ref(false)
const currentRow = ref<ClassroomBorrow | null>(null)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 教室选项
const roomOptions = ref<Room[]>([])

// 设备需求列表
const equipmentNeedsList = ref<string[]>([])

// 表单数据
const form = reactive({
  applicantDept: '',
  applicantName: '',
  roomId: '',
  purpose: '',
  borrowDate: '',
  startTime: '',
  endTime: '',
  expectedAttendees: 1,
  equipmentNeeds: ''
})

// 表单验证规则
const formRules: FormRules = {
  applicantDept: [{ required: true, message: '请输入申请部门', trigger: 'blur' }],
  applicantName: [{ required: true, message: '请输入申请人', trigger: 'blur' }],
  roomId: [{ required: true, message: '请选择教室', trigger: 'change' }],
  purpose: [{ required: true, message: '请选择借用用途', trigger: 'change' }],
  borrowDate: [{ required: true, message: '请选择借用日期', trigger: 'change' }],
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

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getClassroomBorrowList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载教室借用记录失败:', error)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 加载教室选项
async function loadRooms() {
  try {
    const res = await getRoomList({ page: 1, pageSize: 100 })
    roomOptions.value = res.data?.list || []
  } catch (error) {
    console.error('加载教室列表失败:', error)
  }
}

// 搜索
function handleSearch() {
  queryParams.page = 1
  loadData()
}

// 重置
function handleReset() {
  searchForm.applicantDept = ''
  searchForm.roomName = ''
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
    applicantDept: '',
    applicantName: '',
    roomId: '',
    purpose: '',
    borrowDate: '',
    startTime: '',
    endTime: '',
    expectedAttendees: 1,
    equipmentNeeds: ''
  })
  equipmentNeedsList.value = []
  dialogVisible.value = true
}

// 查看详情
function handleView(row: ClassroomBorrow) {
  currentRow.value = row
  detailVisible.value = true
}

// 审核
function handleApprove(row: ClassroomBorrow) {
  currentRow.value = row
  approveForm.approved = true
  approveForm.reason = ''
  approveVisible.value = true
}

// 提交审核
async function submitApprove() {
  if (!currentRow.value) return
  try {
    await reviewClassroomBorrow(currentRow.value.borrowId, {
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
      form.equipmentNeeds = equipmentNeedsList.value.join('、')
      await submitClassroomBorrow(form)
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
  loadRooms()
})
</script>

<style scoped>
.classroom-page {
  height: 100%;
}
</style>