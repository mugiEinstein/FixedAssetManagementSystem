<template>
  <div class="vehicle-dispatch-page">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="36" color="#409eff"><Van /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.available }}</div>
              <div class="stat-label">可用车辆</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="36" color="#e6a23c"><Clock /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pending }}</div>
              <div class="stat-label">待审批</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="36" color="#67c23a"><CaretRight /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.inUse }}</div>
              <div class="stat-label">使用中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="36" color="#909399"><Finished /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.completed }}</div>
              <div class="stat-label">本月完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和操作栏 -->
    <el-card shadow="never" style="margin-top: 16px;">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="申请人">
            <el-input v-model="searchForm.applicant" placeholder="请输入" clearable />
          </el-form-item>
          <el-form-item label="车牌号">
            <el-input v-model="searchForm.vehiclePlate" placeholder="请输入" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择" clearable>
              <el-option label="待审批" value="pending" />
              <el-option label="已通过" value="approved" />
              <el-option label="使用中" value="in_use" />
              <el-option label="已完成" value="completed" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增调度
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="dispatchId" label="调度编号" width="120" />
        <el-table-column prop="vehiclePlate" label="车牌号" width="100" />
        <el-table-column prop="vehicleType" label="车型" width="100" />
        <el-table-column prop="applicant" label="申请人" width="90" />
        <el-table-column prop="department" label="部门" width="120" />
        <el-table-column prop="destination" label="目的地" width="150" />
        <el-table-column prop="purpose" label="用车事由" min-width="150" show-overflow-tooltip />
        <el-table-column label="用车时间" width="180">
          <template #default="{ row }">
            {{ row.startTime }} ~ {{ row.endTime }}
          </template>
        </el-table-column>
        <el-table-column prop="driverName" label="司机" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 'pending'" type="success" link @click="handleApprove(row)">审批</el-button>
            <el-button v-if="row.status === 'approved'" type="warning" link @click="handleStart(row)">开始</el-button>
            <el-button v-if="row.status === 'in_use'" type="info" link @click="handleComplete(row)">完成</el-button>
            <el-button v-if="row.status === 'pending'" type="danger" link @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择车辆" prop="vehicleId">
              <el-select v-model="form.vehicleId" placeholder="请选择车辆" style="width: 100%" @change="handleVehicleChange">
                <el-option 
                  v-for="v in availableVehicles" 
                  :key="v.vehicleId" 
                  :label="`${v.plate} (${v.type})`" 
                  :value="v.vehicleId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请人" prop="applicant">
              <el-input v-model="form.applicant" placeholder="请输入申请人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-input v-model="form.department" placeholder="请输入部门" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目的地" prop="destination">
              <el-input v-model="form.destination" placeholder="请输入目的地" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="form.startTime"
                type="datetime"
                placeholder="选择开始时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="form.endTime"
                type="datetime"
                placeholder="选择结束时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="司机姓名" prop="driverName">
              <el-input v-model="form.driverName" placeholder="请输入司机姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="司机电话" prop="driverPhone">
              <el-input v-model="form.driverPhone" placeholder="请输入司机电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="用车事由" prop="purpose">
          <el-input v-model="form.purpose" type="textarea" :rows="3" placeholder="请输入用车事由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="调度详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="调度编号">{{ currentRow?.dispatchId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRow?.status)">{{ getStatusText(currentRow?.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="车牌号">{{ currentRow?.vehiclePlate }}</el-descriptions-item>
        <el-descriptions-item label="车型">{{ currentRow?.vehicleType }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRow?.applicant }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ currentRow?.department }}</el-descriptions-item>
        <el-descriptions-item label="目的地">{{ currentRow?.destination }}</el-descriptions-item>
        <el-descriptions-item label="用车事由">{{ currentRow?.purpose }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentRow?.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentRow?.endTime }}</el-descriptions-item>
        <el-descriptions-item label="司机">{{ currentRow?.driverName }}</el-descriptions-item>
        <el-descriptions-item label="司机电话">{{ currentRow?.driverPhone }}</el-descriptions-item>
        <el-descriptions-item label="起始里程" v-if="currentRow?.mileageStart">{{ currentRow?.mileageStart }} km</el-descriptions-item>
        <el-descriptions-item label="结束里程" v-if="currentRow?.mileageEnd">{{ currentRow?.mileageEnd }} km</el-descriptions-item>
        <el-descriptions-item label="油费" v-if="currentRow?.fuelCost">¥{{ currentRow?.fuelCost }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 审批弹窗 -->
    <el-dialog v-model="approveVisible" title="审批用车申请" width="450px">
      <el-form :model="approveForm" label-width="80px">
        <el-form-item label="审批意见">
          <el-radio-group v-model="approveForm.approved">
            <el-radio :value="true">通过</el-radio>
            <el-radio :value="false">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" v-if="!approveForm.approved">
          <el-input v-model="approveForm.reason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApprove">确定</el-button>
      </template>
    </el-dialog>

    <!-- 开始用车弹窗 -->
    <el-dialog v-model="startVisible" title="开始用车" width="400px">
      <el-form :model="startForm" label-width="100px">
        <el-form-item label="起始里程(km)">
          <el-input-number v-model="startForm.mileageStart" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="startVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStart">确定</el-button>
      </template>
    </el-dialog>

    <!-- 完成用车弹窗 -->
    <el-dialog v-model="completeVisible" title="完成用车" width="450px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="结束里程(km)">
          <el-input-number v-model="completeForm.mileageEnd" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="油费(元)">
          <el-input-number v-model="completeForm.fuelCost" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="completeForm.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComplete">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Van, Clock, CaretRight, Finished, Plus } from '@element-plus/icons-vue'
import { 
  getVehicleDispatchList, 
  applyVehicle, 
  approveDispatch, 
  startDispatch, 
  completeDispatch,
  getIdleVehicles,
  type VehicleDispatch 
} from '@/api/vehicle'

// 统计数据
const stats = reactive({
  available: 8,
  pending: 3,
  inUse: 2,
  completed: 15
})

// 搜索表单
const searchForm = reactive({
  applicant: '',
  vehiclePlate: '',
  status: ''
})

// 分页参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<VehicleDispatch[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗状态
const dialogVisible = ref(false)
const dialogTitle = ref('新增调度')
const detailVisible = ref(false)
const approveVisible = ref(false)
const startVisible = ref(false)
const completeVisible = ref(false)
const currentRow = ref<VehicleDispatch | null>(null)

// 表单
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const form = reactive({
  vehicleId: '',
  applicant: '',
  department: '',
  destination: '',
  startTime: '',
  endTime: '',
  driverName: '',
  driverPhone: '',
  purpose: ''
})

const formRules: FormRules = {
  vehicleId: [{ required: true, message: '请选择车辆', trigger: 'change' }],
  applicant: [{ required: true, message: '请输入申请人', trigger: 'blur' }],
  department: [{ required: true, message: '请输入部门', trigger: 'blur' }],
  destination: [{ required: true, message: '请输入目的地', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  driverName: [{ required: true, message: '请输入司机姓名', trigger: 'blur' }],
  purpose: [{ required: true, message: '请输入用车事由', trigger: 'blur' }]
}

// 可用车辆
const availableVehicles = ref<any[]>([])

// 审批表单
const approveForm = reactive({
  approved: true,
  reason: ''
})

// 开始用车表单
const startForm = reactive({
  mileageStart: 0
})

// 完成用车表单
const completeForm = reactive({
  mileageEnd: 0,
  fuelCost: 0,
  remark: ''
})

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getVehicleDispatchList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data?.list || getMockData()
    total.value = res.data?.total || tableData.value.length
  } catch (error) {
    console.error('加载数据失败:', error)
    tableData.value = getMockData()
    total.value = tableData.value.length
  } finally {
    loading.value = false
  }
}

// 模拟数据
function getMockData(): VehicleDispatch[] {
  return [
    {
      dispatchId: 'VD-2024-001',
      vehicleId: 'V001',
      vehiclePlate: '京A12345',
      vehicleType: '商务车',
      driverName: '张师傅',
      driverPhone: '13800138001',
      applicant: '李明',
      department: '行政部',
      purpose: '接送客户参观',
      destination: '机场',
      startTime: '2024-12-02 08:00',
      endTime: '2024-12-02 12:00',
      status: 'pending',
      applyTime: '2024-12-01 15:30'
    },
    {
      dispatchId: 'VD-2024-002',
      vehicleId: 'V002',
      vehiclePlate: '京B67890',
      vehicleType: '轿车',
      driverName: '王师傅',
      driverPhone: '13800138002',
      applicant: '张华',
      department: '市场部',
      purpose: '外出洽谈业务',
      destination: '朝阳区某公司',
      startTime: '2024-12-02 14:00',
      endTime: '2024-12-02 18:00',
      mileageStart: 15000,
      status: 'in_use',
      applyTime: '2024-12-01 10:00',
      approver: '管理员',
      approveTime: '2024-12-01 11:00'
    },
    {
      dispatchId: 'VD-2024-003',
      vehicleId: 'V003',
      vehiclePlate: '京C11111',
      vehicleType: 'SUV',
      driverName: '陈师傅',
      driverPhone: '13800138003',
      applicant: '王芳',
      department: '财务部',
      purpose: '银行对账',
      destination: '海淀区银行',
      startTime: '2024-12-01 09:00',
      endTime: '2024-12-01 11:00',
      mileageStart: 8000,
      mileageEnd: 8050,
      fuelCost: 150,
      status: 'completed',
      applyTime: '2024-11-30 16:00',
      approver: '管理员',
      approveTime: '2024-11-30 17:00'
    }
  ]
}

// 加载可用车辆
async function loadAvailableVehicles() {
  try {
    const res = await getIdleVehicles()
    availableVehicles.value = res.data || [
      { vehicleId: 'V001', plate: '京A12345', type: '商务车' },
      { vehicleId: 'V002', plate: '京B67890', type: '轿车' },
      { vehicleId: 'V003', plate: '京C11111', type: 'SUV' },
      { vehicleId: 'V004', plate: '京D22222', type: '面包车' }
    ]
  } catch (error) {
    availableVehicles.value = [
      { vehicleId: 'V001', plate: '京A12345', type: '商务车' },
      { vehicleId: 'V002', plate: '京B67890', type: '轿车' },
      { vehicleId: 'V003', plate: '京C11111', type: 'SUV' },
      { vehicleId: 'V004', plate: '京D22222', type: '面包车' }
    ]
  }
}

// 搜索
function handleSearch() {
  queryParams.page = 1
  loadData()
}

// 重置
function handleReset() {
  searchForm.applicant = ''
  searchForm.vehiclePlate = ''
  searchForm.status = ''
  queryParams.page = 1
  loadData()
}

// 新增
function handleAdd() {
  dialogTitle.value = '新增调度'
  Object.assign(form, {
    vehicleId: '',
    applicant: '',
    department: '',
    destination: '',
    startTime: '',
    endTime: '',
    driverName: '',
    driverPhone: '',
    purpose: ''
  })
  loadAvailableVehicles()
  dialogVisible.value = true
}

// 车辆选择变化
function handleVehicleChange(vehicleId: string) {
  const vehicle = availableVehicles.value.find(v => v.vehicleId === vehicleId)
  if (vehicle) {
    // 可以自动填充司机信息等
  }
}

// 查看详情
function handleView(row: VehicleDispatch) {
  currentRow.value = row
  detailVisible.value = true
}

// 审批
function handleApprove(row: VehicleDispatch) {
  currentRow.value = row
  approveForm.approved = true
  approveForm.reason = ''
  approveVisible.value = true
}

// 提交审批
async function submitApprove() {
  if (!currentRow.value) return
  try {
    await approveDispatch(currentRow.value.dispatchId, {
      approved: approveForm.approved,
      reason: approveForm.reason || undefined
    })
    ElMessage.success(approveForm.approved ? '审批通过' : '已驳回')
    approveVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 开始用车
function handleStart(row: VehicleDispatch) {
  currentRow.value = row
  startForm.mileageStart = 0
  startVisible.value = true
}

// 提交开始
async function submitStart() {
  if (!currentRow.value) return
  try {
    await startDispatch(currentRow.value.dispatchId, {
      mileageStart: startForm.mileageStart
    })
    ElMessage.success('已开始用车')
    startVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 完成用车
function handleComplete(row: VehicleDispatch) {
  currentRow.value = row
  completeForm.mileageEnd = row.mileageStart || 0
  completeForm.fuelCost = 0
  completeForm.remark = ''
  completeVisible.value = true
}

// 提交完成
async function submitComplete() {
  if (!currentRow.value) return
  try {
    await completeDispatch(currentRow.value.dispatchId, {
      mileageEnd: completeForm.mileageEnd,
      fuelCost: completeForm.fuelCost || undefined,
      remark: completeForm.remark || undefined
    })
    ElMessage.success('用车已完成')
    completeVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 取消
async function handleCancel(row: VehicleDispatch) {
  try {
    await ElMessageBox.confirm('确定要取消该用车申请吗？', '提示', {
      type: 'warning'
    })
    // 调用取消API
    ElMessage.success('已取消')
    loadData()
  } catch (error) {
    // 取消操作
  }
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      await applyVehicle(form as any)
      ElMessage.success('提交成功')
      dialogVisible.value = false
      loadData()
    } catch (error: any) {
      ElMessage.error(error.message || '提交失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 状态类型
function getStatusType(status?: string) {
  const map: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    in_use: 'primary',
    completed: 'info',
    cancelled: 'danger'
  }
  return map[status || ''] || ''
}

// 状态文本
function getStatusText(status?: string) {
  const map: Record<string, string> = {
    pending: '待审批',
    approved: '已通过',
    in_use: '使用中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status || ''] || status
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.vehicle-dispatch-page {
  height: 100%;
}

.stat-cards .el-card {
  border-radius: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px;
}

.stat-info .stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.stat-info .stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.search-bar .el-form {
  flex: 1;
}
</style>
