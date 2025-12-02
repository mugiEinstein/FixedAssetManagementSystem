<template>
  <div class="equipment-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="器材名称">
        <el-input v-model="searchForm.equipmentName" placeholder="请输入器材名称" clearable />
      </el-form-item>
      <el-form-item label="器材类型">
        <el-select v-model="searchForm.equipmentType" placeholder="请选择" clearable>
          <el-option label="通用设备(TY)" value="TY" />
          <el-option label="电子设备(DZ)" value="DZ" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="闲置" value="idle" />
          <el-option label="在用" value="in_use" />
          <el-option label="维修中" value="maintenance" />
          <el-option label="已报废" value="scrapped" />
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
        :show-import="true"
        @add="handleAdd"
        @export="handleExport"
        @import="handleImport"
        @page-change="handlePageChange"
    >
      <el-table-column prop="equipmentId" label="器材编号" width="130" />
      <el-table-column prop="equipmentName" label="器材名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="equipmentType" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.equipmentType === 'TY' ? 'primary' : 'success'">
            {{ row.equipmentType === 'TY' ? '通用' : '电子' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="model" label="型号" width="120" show-overflow-tooltip />
      <el-table-column prop="originalValue" label="原值(元)" width="120">
        <template #default="{ row }">
          {{ row.originalValue.toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="purchaseDate" label="购置日期" width="110" />
      <el-table-column prop="storageLocation" label="存放位置" width="120" show-overflow-tooltip />
      <el-table-column prop="custodian" label="保管人" width="100" />
      <el-table-column prop="dailyRent" label="日租金(元)" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <AssetStatusTag :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleViewNetValue(row)">查看净值</el-button>
          <el-popconfirm title="确定删除该器材吗？" @confirm="handleDelete(row)">
            <template #reference>
              <el-button type="danger" link>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </DataTable>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ?  '新增器材' : '编辑器材'"
        width="650px"
        destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="器材名称" prop="equipmentName">
              <el-input v-model="form.equipmentName" placeholder="请输入器材名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="器材类型" prop="equipmentType">
              <el-select v-model="form.equipmentType" placeholder="请选择" style="width: 100%">
                <el-option label="通用设备(TY)" value="TY" />
                <el-option label="电子设备(DZ)" value="DZ" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input v-model="form.model" placeholder="请输入型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格" prop="specifications">
              <el-input v-model="form.specifications" placeholder="请输入规格" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原值(元)" prop="originalValue">
              <el-input-number v-model="form.originalValue" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="购置日期" prop="purchaseDate">
              <el-date-picker
                  v-model="form.purchaseDate"
                  type="date"
                  placeholder="选择日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生产厂家" prop="manufacturer">
              <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="折旧年限" prop="depreciationYears">
              <el-input-number v-model="form.depreciationYears" :min="1" :max="50" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="存放位置" prop="storageLocation">
              <el-input v-model="form.storageLocation" placeholder="请输入存放位置" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保管人" prop="custodian">
              <el-input v-model="form.custodian" placeholder="请输入保管人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="维保周期(月)" prop="maintenanceCycle">
              <el-input-number v-model="form.maintenanceCycle" :min="1" :max="24" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日租金(元)" prop="dailyRent">
              <el-input-number v-model="form.dailyRent" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="idle">闲置</el-radio>
            <el-radio value="in_use">在用</el-radio>
            <el-radio value="maintenance">维修中</el-radio>
            <el-radio value="scrapped">已报废</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 净值弹窗 -->
    <el-dialog v-model="netValueVisible" title="器材净值信息" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="器材名称">{{ currentRow?.equipmentName }}</el-descriptions-item>
        <el-descriptions-item label="器材编号">{{ currentRow?.equipmentId }}</el-descriptions-item>
        <el-descriptions-item label="原值">¥ {{ currentRow?.originalValue?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="折旧年限">{{ currentRow?.depreciationYears }} 年</el-descriptions-item>
        <el-descriptions-item label="已使用年限">{{ usedYears }} 年</el-descriptions-item>
        <el-descriptions-item label="累计折旧">¥ {{ accumulatedDepreciation.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="当前净值">
          <span style="color: #409eff; font-size: 18px; font-weight: bold;">
            ¥ {{ netValue.toLocaleString() }}
          </span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 导入弹窗 -->
    <el-dialog v-model="importVisible" title="导入器材" width="500px">
      <el-upload
          drag
          action="#"
          :auto-upload="false"
          accept=".xlsx,.xls"
          :limit="1"
      >
        <el-icon class="el-icon--upload"><Upload /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">只能上传 xlsx/xls 文件</div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary">确定导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import SearchBar from '@/components/common/SearchBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'
import { getEquipmentList, createEquipment, updateEquipment, deleteEquipment, exportEquipments } from '@/api/equipment'
import { calculateAnnualDepreciation, calculateNetValue } from '@/utils/fee-calculator'
import type { Equipment } from '@/types/asset'

// 搜索表单
const searchForm = reactive({
  equipmentName: '',
  equipmentType: '',
  status: ''
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<Equipment[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const netValueVisible = ref(false)
const importVisible = ref(false)
const currentRow = ref<Equipment | null>(null)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const form = reactive<Partial<Equipment>>({
  equipmentName: '',
  equipmentType: 'TY',
  model: '',
  specifications: '',
  originalValue: 0,
  purchaseDate: '',
  manufacturer: '',
  depreciationYears: 10,
  storageLocation: '',
  custodian: '',
  maintenanceCycle: 3,
  dailyRent: 0,
  status: 'idle'
})

// 表单验证规则
const formRules: FormRules = {
  equipmentName: [{ required: true, message: '请输入器材名称', trigger: 'blur' }],
  equipmentType: [{ required: true, message: '请选择器材类型', trigger: 'change' }],
  originalValue: [{ required: true, message: '请输入原值', trigger: 'blur' }],
  purchaseDate: [{ required: true, message: '请选择购置日期', trigger: 'change' }]
}

// 计算已使用年限
const usedYears = computed(() => {
  if (!currentRow.value?.purchaseDate) return 0
  const purchaseDate = new Date(currentRow.value.purchaseDate)
  const now = new Date()
  return Math.floor((now.getTime() - purchaseDate.getTime()) / (365 * 24 * 60 * 60 * 1000))
})

// 计算累计折旧
const accumulatedDepreciation = computed(() => {
  if (!currentRow.value) return 0
  const annualDep = calculateAnnualDepreciation(
      currentRow.value.originalValue,
      0.05,
      currentRow.value.depreciationYears
)
  return Math.min(annualDep * usedYears.value, currentRow.value.originalValue * 0.95)
})

// 计算净值
const netValue = computed(() => {
  if (!currentRow.value) return 0
  return calculateNetValue(
      currentRow.value.originalValue,
      calculateAnnualDepreciation(currentRow.value.originalValue, 0.05, currentRow.value.depreciationYears),
  usedYears.value
)
})

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getEquipmentList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('加载器材数据失败:', error)
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
  searchForm.equipmentName = ''
  searchForm.equipmentType = ''
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
  dialogType.value = 'add'
  Object.assign(form, {
    equipmentName: '',
    equipmentType: 'TY',
    model: '',
    specifications: '',
    originalValue: 0,
    purchaseDate: '',
    manufacturer: '',
    depreciationYears: 10,
    storageLocation: '',
    custodian: '',
    maintenanceCycle: 3,
    dailyRent: 0,
    status: 'idle'
  })
  dialogVisible.value = true
}

// 编辑
function handleEdit(row: Equipment) {
  dialogType.value = 'edit'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 查看净值
function handleViewNetValue(row: Equipment) {
  currentRow.value = row
  netValueVisible.value = true
}

// 删除
async function handleDelete(row: Equipment) {
  try {
    await deleteEquipment(row.equipmentId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    console.error('删除失败:', error)
    ElMessage.error(error.message || '删除失败')
  }
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (dialogType.value === 'add') {
        await createEquipment(form)
        ElMessage.success('新增成功')
      } else {
        await updateEquipment(form.equipmentId!, form)
        ElMessage.success('更新成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error: any) {
      console.error('操作失败:', error)
      ElMessage.error(error.message || (dialogType.value === 'add' ? '新增失败' : '更新失败'))
    } finally {
      submitLoading.value = false
    }
  })
}

// 导出
async function handleExport() {
  try {
    const res = await exportEquipments(searchForm)
    const blob = new Blob([res as any], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '器材列表.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.warning('导出功能需要后端支持')
  }
}

// 导入
function handleImport() {
  importVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.equipment-page {
  height: 100%;
}
</style>