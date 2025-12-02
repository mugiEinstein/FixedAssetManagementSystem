<template>
  <div class="external-borrow-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="申请单位">
        <el-input v-model="searchForm.applicantUnit" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="器材名称">
        <el-input v-model="searchForm.equipmentName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待审核" value="pending" />
          <el-option label="已批准" value="approved" />
          <el-option label="已驳回" value="rejected" />
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
      <el-table-column prop="applicantUnit" label="申请单位" min-width="150" show-overflow-tooltip />
      <el-table-column prop="contactPerson" label="联系人" width="100" />
      <el-table-column prop="equipmentName" label="器材名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="borrowDays" label="借用天数" width="100" />
      <el-table-column prop="depositAmount" label="押金(元)" width="110">
        <template #default="{ row }">
          {{ row.depositAmount.toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="rentAmount" label="租金(元)" width="110">
        <template #default="{ row }">
          {{ row.rentAmount.toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <AssetStatusTag :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="申请时间" width="110" />
      <el-table-column label="操作" width="200" fixed="right">
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
    <el-dialog v-model="dialogVisible" title="新增校外借用申请" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="110px">
        <el-divider content-position="left">申请单位信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请单位" prop="applicantUnit">
              <el-input v-model="form.applicantUnit" placeholder="请输入单位名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="营业执照" prop="businessLicense">
              <el-input v-model="form.businessLicense" placeholder="请输入营业执照号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">借用信息</el-divider>
        <el-form-item label="选择器材" prop="equipmentId">
          <el-select v-model="form.equipmentId" placeholder="请选择器材" style="width: 100%" @change="handleEquipmentChange">
            <el-option
                v-for="item in equipmentOptions"
                :key="item.equipmentId"
                :label="`${item.equipmentName} (日租金: ¥${item.dailyRent})`"
                :value="item.equipmentId"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="借用天数" prop="borrowDays">
              <el-input-number v-model="form.borrowDays" :min="1" :max="365" style="width: 100%" @change="calculateFees" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="借用用途" prop="purpose">
              <el-input v-model="form.purpose" placeholder="请输入借用用途" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">费用预算</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="押金">
              <el-input :value="'¥ ' + form.depositAmount?.toLocaleString()" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="租金">
              <el-input :value="'¥ ' + form.rentAmount?.toLocaleString()" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="费用合计">
              <el-input :value="'¥ ' + ((form.depositAmount || 0) + (form.rentAmount || 0)).toLocaleString()" disabled style="font-weight: bold;" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="借用详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="借用单号">{{ currentRow?.borrowId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <AssetStatusTag v-if="currentRow" :status="currentRow.status" />
        </el-descriptions-item>
        <el-descriptions-item label="申请单位">{{ currentRow?.applicantUnit }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentRow?.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow?.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="营业执照">{{ currentRow?.businessLicense }}</el-descriptions-item>
        <el-descriptions-item label="器材名称" :span="2">{{ currentRow?.equipmentName }}</el-descriptions-item>
        <el-descriptions-item label="借用天数">{{ currentRow?.borrowDays }} 天</el-descriptions-item>
        <el-descriptions-item label="借用用途">{{ currentRow?.purpose }}</el-descriptions-item>
        <el-descriptions-item label="押金">¥ {{ currentRow?.depositAmount?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="租金">¥ {{ currentRow?.rentAmount?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentRow?.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="归还时间">{{ currentRow?.actualReturnDate || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 审批流程 -->
      <div class="approval-flow" style="margin-top: 20px;">
        <h4>审批流程</h4>
        <el-steps :active="getApprovalStep(currentRow)" finish-status="success">
          <el-step title="提交申请" />
          <el-step title="资质审核" />
          <el-step title="器材审核" />
          <el-step title="缴费确认" />
          <el-step title="领取器材" />
          <el-step title="归还验收" />
        </el-steps>
      </div>
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

    <!-- 归还弹窗 -->
    <el-dialog v-model="returnVisible" title="归还验收" width="550px">
      <el-form :model="returnForm" label-width="100px">
        <el-form-item label="器材名称">
          <span>{{ currentRow?.equipmentName }}</span>
        </el-form-item>
        <el-form-item label="押金">
          <span style="color: #409eff; font-weight: bold;">¥{{ currentRow?.depositAmount?.toLocaleString() }}</span>
        </el-form-item>
        <el-form-item label="器材状态">
          <el-radio-group v-model="returnForm.isNormal">
            <el-radio :value="true">完好</el-radio>
            <el-radio :value="false">损坏</el-radio>
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
          <el-input v-model="returnForm.damageDesc" type="textarea" :rows="3" placeholder="请描述损坏情况" />
        </el-form-item>
        <el-form-item v-if="!returnForm.isNormal" label="赔偿金额">
          <el-input-number v-model="returnForm.compensationAmount" :min="0" :precision="2" :step="100" style="width: 200px" />
          <span style="margin-left: 8px; color: #909399;">元</span>
          <el-button type="primary" link @click="calculateCompensation" style="margin-left: 16px;">自动估算</el-button>
        </el-form-item>
        <el-alert v-if="!returnForm.isNormal && returnForm.compensationAmount > 0" type="warning" :closable="false" style="margin-bottom: 16px;">
          <template #title>
            赔偿金额: <strong>¥{{ returnForm.compensationAmount.toFixed(2) }}</strong>，
            将从押金中扣除，剩余押金: <strong>¥{{ Math.max((currentRow?.depositAmount || 0) - returnForm.compensationAmount, 0).toFixed(2) }}</strong>
          </template>
        </el-alert>
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
import { calculateEquipmentDeposit, calculateEquipmentRent } from '@/utils/fee-calculator.ts'
import { getExternalBorrowList, submitExternalBorrow, reviewExternalQualification, reviewExternalEquipment, returnExternalEquipment } from '@/api/borrow'
import { getIdleEquipments } from '@/api/equipment'
import type { ExternalEquipmentBorrow } from '@/types/business.ts'
import type { Equipment } from '@/types/asset.ts'

// 搜索表单
const searchForm = reactive({
  applicantUnit: '',
  equipmentName: '',
  status: ''
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<ExternalEquipmentBorrow[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const detailVisible = ref(false)
const approveVisible = ref(false)
const returnVisible = ref(false)
const currentRow = ref<ExternalEquipmentBorrow | null>(null)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 器材选项
const equipmentOptions = ref<Equipment[]>([])
const selectedEquipment = ref<Equipment | null>(null)

// 表单数据
const form = reactive({
  applicantUnit: '',
  contactPerson: '',
  contactPhone: '',
  businessLicense: '',
  equipmentId: '',
  borrowDays: 1,
  purpose: '',
  depositAmount: 0,
  rentAmount: 0
})

// 表单验证规则
const formRules: FormRules = {
  applicantUnit: [{ required: true, message: '请输入申请单位', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  equipmentId: [{ required: true, message: '请选择器材', trigger: 'change' }],
  borrowDays: [{ required: true, message: '请输入借用天数', trigger: 'blur' }],
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
  damageDesc: '',
  damageLevel: '' as 'minor' | 'moderate' | 'severe' | 'lost' | '',
  compensationAmount: 0
})

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getExternalBorrowList({ ...queryParams, ...searchForm })
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
  searchForm.applicantUnit = ''
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
    applicantUnit: '', contactPerson: '', contactPhone: '', businessLicense: '',
    equipmentId: '', borrowDays: 1, purpose: '', depositAmount: 0, rentAmount: 0
  })
  selectedEquipment.value = null
  dialogVisible.value = true
}

// 器材选择变化
function handleEquipmentChange(equipmentId: string) {
  selectedEquipment.value = equipmentOptions.value.find(e => e.equipmentId === equipmentId) || null
  calculateFees()
}

// 计算费用
function calculateFees() {
  if (selectedEquipment.value) {
    form.depositAmount = calculateEquipmentDeposit(selectedEquipment.value.originalValue)
    form.rentAmount = calculateEquipmentRent(selectedEquipment.value.dailyRent, form.borrowDays)
  }
}

// 查看详情
function handleView(row: ExternalEquipmentBorrow) {
  currentRow.value = row
  detailVisible.value = true
}

// 获取审批步骤
function getApprovalStep(row: ExternalEquipmentBorrow | null): number {
  if (!row) return 0
  switch (row.status) {
    case 'pending': return 1
    case 'approved': return 3
    case 'in_use': return 4
    case 'returned': return 6
    default: return 0
  }
}

// 审核
function handleApprove(row: ExternalEquipmentBorrow) {
  currentRow.value = row
  approveForm.approved = true
  approveForm.reason = ''
  approveVisible.value = true
}

// 提交审核
async function submitApprove() {
  if (!currentRow.value) return
  try {
    const reviewData = { approved: approveForm.approved, reason: approveForm.reason }
    await reviewExternalQualification(currentRow.value.borrowId, reviewData)
    await reviewExternalEquipment(currentRow.value.borrowId, reviewData)
    ElMessage.success('审核完成')
    approveVisible.value = false
    loadData()
  } catch (error: any) {
    console.error('审核失败:', error)
    ElMessage.error(error.message || '审核失败')
  }
}

// 归还
function handleReturn(row: ExternalEquipmentBorrow) {
  currentRow.value = row
  returnForm.isNormal = true
  returnForm.damageDesc = ''
  returnForm.damageLevel = ''
  returnForm.compensationAmount = 0
  returnVisible.value = true
}

// 自动估算赔偿金额
function calculateCompensation() {
  if (!currentRow.value || returnForm.isNormal) {
    returnForm.compensationAmount = 0
    return
  }
  const depositAmount = currentRow.value.depositAmount || 5000
  const damageRates: Record<string, number> = { minor: 0.1, moderate: 0.3, severe: 0.7, lost: 1.0 }
  const rate = damageRates[returnForm.damageLevel] || 0
  returnForm.compensationAmount = Math.round(depositAmount * rate * 100) / 100
  ElMessage.info(`根据损坏程度，预估赔偿${rate * 100}%押金`)
}

// 提交归还
async function submitReturn() {
  if (!currentRow.value) return
  if (!returnForm.isNormal && !returnForm.damageLevel) {
    ElMessage.warning('请选择损坏程度')
    return
  }
  try {
    await returnExternalEquipment(currentRow.value.borrowId, {
      isNormal: returnForm.isNormal,
      damageDesc: returnForm.damageDesc,
      damageLevel: returnForm.damageLevel,
      compensationAmount: returnForm.compensationAmount
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
      await submitExternalBorrow(form)
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
.external-borrow-page {
  height: 100%;
}
</style>