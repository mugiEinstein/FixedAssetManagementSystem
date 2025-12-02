<template>
  <div class="internal-borrow-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="申请人">
        <el-input v-model="searchForm.applicantName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="器材名称">
        <el-input v-model="searchForm.equipmentName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待审核" value="pending" />
          <el-option label="已批准" value="approved" />
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
        @add="handleAdd"
        @page-change="handlePageChange"
        :show-export="false"
    >
      <el-table-column prop="borrowId" label="借用单号" width="130" />
      <el-table-column prop="applicantName" label="申请人" width="100" />
      <el-table-column prop="department" label="所属部门" width="120" />
      <el-table-column prop="userType" label="人员类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.userType === 'student' ?  'success' : 'primary'">
            {{ row.userType === 'student' ?  '学生' : row.userType === 'teacher' ? '教师' : '职工' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="equipmentName" label="器材名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="borrowDate" label="借用日期" width="110" />
      <el-table-column prop="returnDate" label="归还日期" width="110" />
      <el-table-column prop="purpose" label="用途" width="120" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <AssetStatusTag :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">详情</el-button>
          <el-button
              v-if="row. status === 'pending'"
              type="success"
              link
              @click="handleApprove(row)"
          >
            审核
          </el-button>
          <el-button
              v-if="row.status === 'in_use'"
              type="warning"
              link
              @click="handleReturn(row)"
          >
            归还
          </el-button>
        </template>
      </el-table-column>
    </DataTable>

    <!-- 新增申请弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增校内借用申请" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
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
        <el-form-item label="人员类型" prop="userType">
          <el-radio-group v-model="form.userType">
            <el-radio value="student">学生</el-radio>
            <el-radio value="teacher">教师</el-radio>
            <el-radio value="staff">职工</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选择器材" prop="equipmentId">
          <el-select v-model="form.equipmentId" placeholder="请选择器材" style="width: 100%">
            <el-option
                v-for="item in equipmentOptions"
                :key="item.equipmentId"
                :label="item.equipmentName"
                :value="item.equipmentId"
            />
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
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归还日期" prop="returnDate">
              <el-date-picker
                  v-model="form.returnDate"
                  type="date"
                  placeholder="选择日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="借用用途" prop="purpose">
          <el-input v-model="form.purpose" type="textarea" :rows="3" placeholder="请输入借用用途" />
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
        <el-descriptions-item label="借用单号">{{ currentRow?. borrowId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <AssetStatusTag v-if="currentRow" :status="currentRow.status" />
        </el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRow?.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="所属部门">{{ currentRow?.department }}</el-descriptions-item>
        <el-descriptions-item label="器材名称" :span="2">{{ currentRow?.equipmentName }}</el-descriptions-item>
        <el-descriptions-item label="借用日期">{{ currentRow?.borrowDate }}</el-descriptions-item>
        <el-descriptions-item label="归还日期">{{ currentRow?.returnDate }}</el-descriptions-item>
        <el-descriptions-item label="借用用途" :span="2">{{ currentRow?.purpose }}</el-descriptions-item>
        <el-descriptions-item label="实际归还">{{ currentRow?.actualReturnDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="违约金">
          <span :style="{ color: currentRow?.penaltyFee ? '#E6A23C' : '' }">
            {{ currentRow?.penaltyFee ? '¥' + currentRow.penaltyFee.toFixed(2) : '-' }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="赔偿金">
          <span :style="{ color: currentRow?.compensationFee ? '#F56C6C' : '' }">
            {{ currentRow?.compensationFee ? '¥' + currentRow.compensationFee.toFixed(2) : '-' }}
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

    <!-- 归还弹窗 -->
    <el-dialog v-model="returnVisible" title="归还验收" width="500px">
      <el-form :model="returnForm" label-width="100px">
        <el-form-item label="器材状态">
          <el-radio-group v-model="returnForm.isNormal">
            <el-radio :value="true">完好</el-radio>
            <el-radio :value="false">损坏</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="!returnForm.isNormal" label="损坏描述">
          <el-input v-model="returnForm.damageDesc" type="textarea" :rows="3" placeholder="请描述损坏情况" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReturn">确认归还</el-button>
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
import { getInternalBorrowList, submitInternalBorrow, reviewInternalPermission, reviewInternalEquipment, returnInternalEquipment } from '@/api/borrow'
import { getIdleEquipments } from '@/api/equipment'
import type { InternalEquipmentBorrow } from '@/types/business'
import type { Equipment } from '@/types/asset'

// 搜索表单
const searchForm = reactive({
  applicantName: '',
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

// 弹窗相关
const dialogVisible = ref(false)
const detailVisible = ref(false)
const approveVisible = ref(false)
const returnVisible = ref(false)
const currentRow = ref<InternalEquipmentBorrow | null>(null)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 器材选项
const equipmentOptions = ref<any[]>([])

// 表单数据
const form = reactive({
  applicantName: '',
  department: '',
  userType: 'student' as 'student' | 'teacher' | 'staff',
  equipmentId: '',
  borrowDate: '',
  returnDate: '',
  purpose: ''
})

// 表单验证规则
const formRules: FormRules = {
  applicantName: [{ required: true, message: '请输入申请人姓名', trigger: 'blur' }],
  department: [{ required: true, message: '请输入所属部门', trigger: 'blur' }],
  userType: [{ required: true, message: '请选择人员类型', trigger: 'change' }],
  equipmentId: [{ required: true, message: '请选择器材', trigger: 'change' }],
  borrowDate: [{ required: true, message: '请选择借用日期', trigger: 'change' }],
  returnDate: [{ required: true, message: '请选择归还日期', trigger: 'change' }],
  purpose: [{ required: true, message: '请输入借用用途', trigger: 'blur' }]
}

// 审核表单
const approveForm = reactive({
  approved: true,
  reason: ''
})

// 归还表单
const returnForm = reactive({
  isNormal: true,
  damageDesc: ''
})

// 加载数据 - 管理员看到所有申请
async function loadData() {
  loading.value = true
  try {
    const res = await getInternalBorrowList({
      ...queryParams,
      ...searchForm
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

// 加载器材选项
async function loadEquipments() {
  try {
    const res = await getIdleEquipments()
    equipmentOptions.value = res.data || []
  } catch (error) {
    console.error('加载器材失败:', error)
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

// 新增
function handleAdd() {
  Object.assign(form, {
    applicantName: '',
    department: '',
    userType: 'student',
    equipmentId: '',
    borrowDate: '',
    returnDate: '',
    purpose: ''
  })
  dialogVisible.value = true
}

// 查看详情
function handleView(row: InternalEquipmentBorrow) {
  currentRow.value = row
  detailVisible.value = true
}

// 审核
function handleApprove(row: InternalEquipmentBorrow) {
  currentRow.value = row
  approveForm.approved = true
  approveForm.reason = ''
  approveVisible.value = true
}

// 提交审核 - 同时完成权限审批和器材审批
async function submitApprove() {
  if (!currentRow.value) return
  try {
    const reviewData = {
      approved: approveForm.approved,
      reason: approveForm.reason
    }
    // 同时调用两个审批接口
    await reviewInternalPermission(currentRow.value.borrowId, reviewData)
    await reviewInternalEquipment(currentRow.value.borrowId, reviewData)
    ElMessage.success('审核完成')
    approveVisible.value = false
    loadData()
  } catch (error: any) {
    console.error('审核失败:', error)
    ElMessage.error(error.message || '审核失败')
  }
}

// 归还
function handleReturn(row: InternalEquipmentBorrow) {
  currentRow.value = row
  returnForm.isNormal = true
  returnForm.damageDesc = ''
  returnVisible.value = true
}

// 提交归还
async function submitReturn() {
  if (!currentRow.value) return
  try {
    await returnInternalEquipment(currentRow.value.borrowId, {
      isNormal: returnForm.isNormal,
      damageDesc: returnForm.damageDesc
    })
    ElMessage.success('归还验收完成')
    returnVisible.value = false
    loadData()
  } catch (error: any) {
    console.error('归还失败:', error)
    ElMessage.error(error.message || '归还失败')
  }
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      await submitInternalBorrow({
        ...form,
        userId: 'admin'
      })
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
  loadEquipments()
})
</script>

<style scoped>
.internal-borrow-page {
  height: 100%;
}
</style>