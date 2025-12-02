<template>
  <div class="vehicle-maintenance-page">
    <!-- 搜索栏 -->
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="车牌号">
            <el-input v-model="searchForm.vehiclePlate" placeholder="请输入" clearable />
          </el-form-item>
          <el-form-item label="维护类型">
            <el-select v-model="searchForm.maintenanceType" placeholder="请选择" clearable>
              <el-option label="定期保养" value="regular" />
              <el-option label="故障维修" value="repair" />
              <el-option label="年检" value="inspection" />
              <el-option label="保险" value="insurance" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择" clearable>
              <el-option label="待处理" value="pending" />
              <el-option label="进行中" value="in_progress" />
              <el-option label="已完成" value="completed" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增维护
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="maintenanceId" label="维护编号" width="130" />
        <el-table-column prop="vehiclePlate" label="车牌号" width="100" />
        <el-table-column prop="vehicleType" label="车型" width="90" />
        <el-table-column prop="maintenanceType" label="维护类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.maintenanceType)">{{ getTypeName(row.maintenanceType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="维护内容" min-width="180" show-overflow-tooltip />
        <el-table-column prop="maintenanceDate" label="维护日期" width="110" />
        <el-table-column prop="cost" label="费用(元)" width="100">
          <template #default="{ row }">
            ¥{{ row.cost?.toLocaleString() || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="vendor" label="维修厂商" width="120" />
        <el-table-column prop="nextMaintenanceDate" label="下次维护" width="110" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status !== 'completed'" type="warning" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 'in_progress'" type="success" link @click="handleComplete(row)">完成</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择车辆" prop="vehicleId">
              <el-select v-model="form.vehicleId" placeholder="请选择车辆" style="width: 100%" @change="handleVehicleChange">
                <el-option 
                  v-for="v in vehicles" 
                  :key="v.vehicleId" 
                  :label="`${v.plate} (${v.type})`" 
                  :value="v.vehicleId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维护类型" prop="maintenanceType">
              <el-select v-model="form.maintenanceType" placeholder="请选择" style="width: 100%">
                <el-option label="定期保养" value="regular" />
                <el-option label="故障维修" value="repair" />
                <el-option label="年检" value="inspection" />
                <el-option label="保险" value="insurance" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="维护日期" prop="maintenanceDate">
              <el-date-picker
                v-model="form.maintenanceDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预算费用">
              <el-input-number v-model="form.cost" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="维修厂商">
          <el-input v-model="form.vendor" placeholder="请输入维修厂商名称" />
        </el-form-item>
        <el-form-item label="维护内容" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请详细描述维护内容" />
        </el-form-item>
        <el-form-item label="下次维护日期">
          <el-date-picker
            v-model="form.nextMaintenanceDate"
            type="date"
            placeholder="选择日期（选填）"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="维护详情" width="550px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="维护编号">{{ currentRow?.maintenanceId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRow?.status)">{{ getStatusText(currentRow?.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="车牌号">{{ currentRow?.vehiclePlate }}</el-descriptions-item>
        <el-descriptions-item label="车型">{{ currentRow?.vehicleType }}</el-descriptions-item>
        <el-descriptions-item label="维护类型">{{ getTypeName(currentRow?.maintenanceType) }}</el-descriptions-item>
        <el-descriptions-item label="维护日期">{{ currentRow?.maintenanceDate }}</el-descriptions-item>
        <el-descriptions-item label="费用">¥{{ currentRow?.cost?.toLocaleString() || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修厂商">{{ currentRow?.vendor || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维护内容" :span="2">{{ currentRow?.description }}</el-descriptions-item>
        <el-descriptions-item label="下次维护">{{ currentRow?.nextMaintenanceDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ currentRow?.createdBy }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 完成弹窗 -->
    <el-dialog v-model="completeVisible" title="完成维护" width="400px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="实际费用(元)">
          <el-input-number v-model="completeForm.actualCost" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="completeForm.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComplete">确定完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { 
  getVehicleMaintenanceList, 
  addVehicleMaintenance, 
  updateVehicleMaintenance,
  completeMaintenance,
  type VehicleMaintenance 
} from '@/api/vehicle'

// 搜索表单
const searchForm = reactive({
  vehiclePlate: '',
  maintenanceType: '',
  status: ''
})

// 分页参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<VehicleMaintenance[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗状态
const dialogVisible = ref(false)
const dialogTitle = ref('新增维护')
const detailVisible = ref(false)
const completeVisible = ref(false)
const currentRow = ref<VehicleMaintenance | null>(null)
const isEdit = ref(false)

// 表单
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const form = reactive({
  vehicleId: '',
  maintenanceType: '',
  maintenanceDate: '',
  cost: 0,
  vendor: '',
  description: '',
  nextMaintenanceDate: ''
})

const formRules: FormRules = {
  vehicleId: [{ required: true, message: '请选择车辆', trigger: 'change' }],
  maintenanceType: [{ required: true, message: '请选择维护类型', trigger: 'change' }],
  maintenanceDate: [{ required: true, message: '请选择维护日期', trigger: 'change' }],
  description: [{ required: true, message: '请输入维护内容', trigger: 'blur' }]
}

// 车辆列表
const vehicles = ref<any[]>([
  { vehicleId: 'V001', plate: '京A12345', type: '商务车' },
  { vehicleId: 'V002', plate: '京B67890', type: '轿车' },
  { vehicleId: 'V003', plate: '京C11111', type: 'SUV' },
  { vehicleId: 'V004', plate: '京D22222', type: '面包车' }
])

// 完成表单
const completeForm = reactive({
  actualCost: 0,
  remark: ''
})

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getVehicleMaintenanceList({
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
function getMockData(): VehicleMaintenance[] {
  return [
    {
      maintenanceId: 'VM-2024-001',
      vehicleId: 'V001',
      vehiclePlate: '京A12345',
      vehicleType: '商务车',
      maintenanceType: 'regular',
      description: '更换机油、机滤、空气滤芯，检查刹车片磨损情况',
      maintenanceDate: '2024-12-01',
      cost: 800,
      vendor: '4S店',
      nextMaintenanceDate: '2025-06-01',
      status: 'completed',
      createdBy: '车辆管理员',
      createdAt: '2024-11-25'
    },
    {
      maintenanceId: 'VM-2024-002',
      vehicleId: 'V002',
      vehiclePlate: '京B67890',
      vehicleType: '轿车',
      maintenanceType: 'repair',
      description: '前轮胎更换，四轮定位',
      maintenanceDate: '2024-12-02',
      cost: 1500,
      vendor: '轮胎专营店',
      status: 'in_progress',
      createdBy: '车辆管理员',
      createdAt: '2024-12-01'
    },
    {
      maintenanceId: 'VM-2024-003',
      vehicleId: 'V003',
      vehiclePlate: '京C11111',
      vehicleType: 'SUV',
      maintenanceType: 'inspection',
      description: '车辆年度检验',
      maintenanceDate: '2024-12-15',
      cost: 300,
      vendor: '车管所',
      status: 'pending',
      createdBy: '车辆管理员',
      createdAt: '2024-12-01'
    },
    {
      maintenanceId: 'VM-2024-004',
      vehicleId: 'V001',
      vehiclePlate: '京A12345',
      vehicleType: '商务车',
      maintenanceType: 'insurance',
      description: '续保商业险+交强险',
      maintenanceDate: '2024-12-20',
      cost: 5000,
      vendor: '平安保险',
      status: 'pending',
      createdBy: '车辆管理员',
      createdAt: '2024-12-01'
    }
  ]
}

// 搜索
function handleSearch() {
  queryParams.page = 1
  loadData()
}

// 重置
function handleReset() {
  searchForm.vehiclePlate = ''
  searchForm.maintenanceType = ''
  searchForm.status = ''
  queryParams.page = 1
  loadData()
}

// 新增
function handleAdd() {
  dialogTitle.value = '新增维护'
  isEdit.value = false
  Object.assign(form, {
    vehicleId: '',
    maintenanceType: '',
    maintenanceDate: '',
    cost: 0,
    vendor: '',
    description: '',
    nextMaintenanceDate: ''
  })
  dialogVisible.value = true
}

// 编辑
function handleEdit(row: VehicleMaintenance) {
  dialogTitle.value = '编辑维护'
  isEdit.value = true
  currentRow.value = row
  Object.assign(form, {
    vehicleId: row.vehicleId,
    maintenanceType: row.maintenanceType,
    maintenanceDate: row.maintenanceDate,
    cost: row.cost,
    vendor: row.vendor || '',
    description: row.description,
    nextMaintenanceDate: row.nextMaintenanceDate || ''
  })
  dialogVisible.value = true
}

// 车辆选择变化
function handleVehicleChange(vehicleId: string) {
  // 可以做一些联动处理
}

// 查看详情
function handleView(row: VehicleMaintenance) {
  currentRow.value = row
  detailVisible.value = true
}

// 完成维护
function handleComplete(row: VehicleMaintenance) {
  currentRow.value = row
  completeForm.actualCost = row.cost
  completeForm.remark = ''
  completeVisible.value = true
}

// 提交完成
async function submitComplete() {
  if (!currentRow.value) return
  try {
    await completeMaintenance(currentRow.value.maintenanceId, {
      actualCost: completeForm.actualCost,
      remark: completeForm.remark || undefined
    })
    ElMessage.success('维护已完成')
    completeVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value && currentRow.value) {
        await updateVehicleMaintenance(currentRow.value.maintenanceId, form as any)
      } else {
        await addVehicleMaintenance(form as any)
      }
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadData()
    } catch (error: any) {
      ElMessage.error(error.message || '提交失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 维护类型标签
function getTypeTag(type?: string) {
  const map: Record<string, string> = {
    regular: '',
    repair: 'warning',
    inspection: 'info',
    insurance: 'success'
  }
  return map[type || ''] || ''
}

// 维护类型名称
function getTypeName(type?: string) {
  const map: Record<string, string> = {
    regular: '定期保养',
    repair: '故障维修',
    inspection: '年检',
    insurance: '保险'
  }
  return map[type || ''] || type
}

// 状态类型
function getStatusType(status?: string) {
  const map: Record<string, string> = {
    pending: 'warning',
    in_progress: 'primary',
    completed: 'success'
  }
  return map[status || ''] || ''
}

// 状态文本
function getStatusText(status?: string) {
  const map: Record<string, string> = {
    pending: '待处理',
    in_progress: '进行中',
    completed: '已完成'
  }
  return map[status || ''] || status
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.vehicle-maintenance-page {
  height: 100%;
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
