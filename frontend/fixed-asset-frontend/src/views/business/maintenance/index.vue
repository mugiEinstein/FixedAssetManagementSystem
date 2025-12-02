<template>
  <div class="maintenance-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="资产名称">
        <el-input v-model="searchForm.assetName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="资产类型">
        <el-select v-model="searchForm.assetType" placeholder="请选择" clearable>
          <el-option label="建筑" value="building" />
          <el-option label="器材" value="equipment" />
          <el-option label="车辆" value="vehicle" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待处理" value="pending" />
          <el-option label="已派单" value="assigned" />
          <el-option label="维修中" value="in_progress" />
          <el-option label="已完成" value="completed" />
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
        :show-export="true"
    >
      <el-table-column prop="maintenanceId" label="维修单号" width="130" />
      <el-table-column prop="assetType" label="资产类型" width="100">
        <template #default="{ row }">
          {{ getAssetTypeName(row.assetType) }}
        </template>
      </el-table-column>
      <el-table-column prop="assetName" label="资产名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="reporterName" label="报修人" width="100" />
      <el-table-column prop="reporterDept" label="报修部门" width="120" />
      <el-table-column prop="faultDescription" label="故障描述" min-width="180" show-overflow-tooltip />
      <el-table-column prop="reportDate" label="报修日期" width="110" />
      <el-table-column prop="assignedTo" label="维修人员" width="100">
        <template #default="{ row }">
          {{ row.assignedTo || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <AssetStatusTag :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">详情</el-button>
          <el-button
              v-if="row.status === 'pending'"
              type="warning"
              link
              @click="handleAssign(row)"
          >
            派单
          </el-button>
          <el-button
              v-if="row.status === 'in_progress'"
              type="success"
              link
              @click="handleComplete(row)"
          >
            完成
          </el-button>
        </template>
      </el-table-column>
    </DataTable>

    <!-- 新增维修单弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增维修单" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="资产类型" prop="assetType">
          <el-radio-group v-model="form.assetType">
            <el-radio value="building">建筑</el-radio>
            <el-radio value="equipment">器材</el-radio>
            <el-radio value="vehicle">车辆</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选择资产" prop="assetId">
          <el-select v-model="form.assetId" placeholder="请选择需要维修的资产" style="width: 100%">
            <el-option label="逸夫楼101教室空调" value="A-001" />
            <el-option label="高精度光谱分析仪" value="A-002" />
            <el-option label="主楼电梯" value="A-003" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报修人" prop="reporterName">
              <el-input v-model="form.reporterName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报修部门" prop="reporterDept">
              <el-input v-model="form.reporterDept" placeholder="请输入部门" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="故障描述" prop="faultDescription">
          <el-input
              v-model="form.faultDescription"
              type="textarea"
              :rows="4"
              placeholder="请详细描述故障现象"
          />
        </el-form-item>
        <el-form-item label="上传图片">
          <el-upload
              action="#"
              :auto-upload="false"
              list-type="picture-card"
              :limit="3"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="维修单详情" width="650px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="维修单号">{{ currentRow?.maintenanceId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <AssetStatusTag v-if="currentRow" :status="currentRow.status" />
        </el-descriptions-item>
        <el-descriptions-item label="资产类型">{{ getAssetTypeName(currentRow?.assetType) }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ currentRow?.assetName }}</el-descriptions-item>
        <el-descriptions-item label="报修人">{{ currentRow?.reporterName }}</el-descriptions-item>
        <el-descriptions-item label="报修部门">{{ currentRow?.reporterDept }}</el-descriptions-item>
        <el-descriptions-item label="报修日期">{{ currentRow?.reportDate }}</el-descriptions-item>
        <el-descriptions-item label="维修人员">{{ currentRow?.assignedTo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ currentRow?.faultDescription }}</el-descriptions-item>
        <el-descriptions-item label="维修结果" :span="2">{{ currentRow?.repairResult || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修费用">
          {{ currentRow?.repairCost ? '¥' + currentRow.repairCost : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="完成日期">{{ currentRow?.repairDate || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 派单弹窗 -->
    <el-dialog v-model="assignVisible" title="派单" width="450px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="维修人员">
          <el-select v-model="assignForm.assignedTo" placeholder="请选择维修人员" style="width: 100%">
            <el-option label="张师傅" value="张师傅" />
            <el-option label="李师傅" value="李师傅" />
            <el-option label="王师傅" value="王师傅" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="assignForm.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确认派单</el-button>
      </template>
    </el-dialog>

    <!-- 完成弹窗 -->
    <el-dialog v-model="completeVisible" title="完成维修" width="500px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="维修结果">
          <el-input v-model="completeForm.repairResult" type="textarea" :rows="3" placeholder="请描述维修结果" />
        </el-form-item>
        <el-form-item label="维修费用">
          <el-input-number v-model="completeForm.repairCost" :min="0" :precision="2" style="width: 200px" />
          <span style="margin-left: 8px; color: #909399;">元</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComplete">确认完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import SearchBar from '@/components/common/SearchBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'
import { getMaintenanceList, submitMaintenanceRequest, assignMaintainer, completeMaintenance, getMaintainerList } from '@/api/maintenance'
import type { MaintenanceRecord } from '@/types/business'

// 搜索表单
const searchForm = reactive({
  assetName: '',
  assetType: '',
  status: ''
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<MaintenanceRecord[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const detailVisible = ref(false)
const assignVisible = ref(false)
const completeVisible = ref(false)
const currentRow = ref<MaintenanceRecord | null>(null)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const form = reactive({
  assetType: 'equipment',
  assetId: '',
  reporterName: '',
  reporterDept: '',
  faultDescription: ''
})

// 表单验证规则
const formRules: FormRules = {
  assetType: [{ required: true, message: '请选择资产类型', trigger: 'change' }],
  assetId: [{ required: true, message: '请选择资产', trigger: 'change' }],
  reporterName: [{ required: true, message: '请输入报修人', trigger: 'blur' }],
  reporterDept: [{ required: true, message: '请输入报修部门', trigger: 'blur' }],
  faultDescription: [{ required: true, message: '请描述故障', trigger: 'blur' }]
}

// 派单表单
const assignForm = reactive({
  assignedTo: '',
  remark: ''
})

// 完成表单
const completeForm = reactive({
  repairResult: '',
  repairCost: 0
})

// 获取资产类型名称
function getAssetTypeName(type?: string): string {
  const map: Record<string, string> = {
    building: '建筑',
    equipment: '器材',
    vehicle: '车辆'
  }
  return type ? map[type] || type : ''
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getMaintenanceList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载维修记录失败:', error)
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
  searchForm.assetName = ''
  searchForm.assetType = ''
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
    assetType: 'equipment',
    assetId: '',
    reporterName: '',
    reporterDept: '',
    faultDescription: ''
  })
  dialogVisible.value = true
}

// 查看详情
function handleView(row: MaintenanceRecord) {
  currentRow.value = row
  detailVisible.value = true
}

// 派单
function handleAssign(row: MaintenanceRecord) {
  currentRow.value = row
  assignForm.assignedTo = ''
  assignForm.remark = ''
  assignVisible.value = true
}

// 提交派单
async function submitAssign() {
  if (!assignForm.assignedTo) {
    ElMessage.warning('请选择维修人员')
    return
  }
  if (!currentRow.value) return
  try {
    await assignMaintainer(currentRow.value.maintenanceId, {
      maintainerId: assignForm.assignedTo,
      maintainerName: assignForm.assignedTo
    })
    ElMessage.success('派单成功')
    assignVisible.value = false
    loadData()
  } catch (error: any) {
    console.error('派单失败:', error)
    ElMessage.error(error.message || '派单失败')
  }
}

// 完成维修
function handleComplete(row: MaintenanceRecord) {
  currentRow.value = row
  completeForm.repairResult = ''
  completeForm.repairCost = 0
  completeVisible.value = true
}

// 提交完成
async function submitComplete() {
  if (!completeForm.repairResult) {
    ElMessage.warning('请填写维修结果')
    return
  }
  if (!currentRow.value) return
  try {
    await completeMaintenance(currentRow.value.maintenanceId, {
      result: completeForm.repairResult,
      cost: completeForm.repairCost
    })
    ElMessage.success('维修完成')
    completeVisible.value = false
    loadData()
  } catch (error: any) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '提交失败')
  }
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      await submitMaintenanceRequest(form as any)
      ElMessage.success('维修单提交成功')
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
.maintenance-page {
  height: 100%;
}
</style>