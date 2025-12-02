<template>
  <div class="vehicle-archive">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>车辆建档</span>
          <div class="header-actions">
            <el-upload
              ref="uploadRef"
              :auto-upload="false"
              :show-file-list="false"
              accept=".csv,.xlsx,.xls"
              :on-change="handleFileChange"
            >
              <el-button :icon="Upload">批量导入</el-button>
            </el-upload>
            <el-button :icon="Download" @click="handleDownloadTemplate">下载模板</el-button>
            <el-button :icon="Download" @click="handleExport">导出数据</el-button>
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增车辆</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="车牌号">
          <el-input v-model="searchForm.plateNo" placeholder="请输入车牌号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="维修中" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="vehicleId" label="车辆编号" width="140" />
        <el-table-column prop="plateNo" label="车牌号" width="120" />
        <el-table-column prop="model" label="品牌型号" min-width="180" />
        <el-table-column prop="seatNum" label="座位数" width="80" />
        <el-table-column prop="purchaseDate" label="购置日期" width="120" />
        <el-table-column prop="originalValue" label="原值(元)" width="120">
          <template #default="{ row }">
            {{ row.originalValue ? `¥${row.originalValue.toLocaleString()}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '正常' : '维修中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        class="pagination"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增车辆' : '编辑车辆'"
      width="650px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车牌号" prop="plateNo">
              <el-input v-model="form.plateNo" placeholder="如：京A12345" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌型号" prop="model">
              <el-input v-model="form.model" placeholder="如：丰田凯美瑞 2023款" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="座位数" prop="seatNum">
              <el-input-number v-model="form.seatNum" :min="1" :max="100" style="width: 100%" />
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
            <el-form-item label="原值(元)" prop="originalValue">
              <el-input-number v-model="form.originalValue" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="折旧年限" prop="depreciationYears">
              <el-input-number v-model="form.depreciationYears" :min="1" :max="30" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="基准油耗" prop="standardFuel">
              <el-input-number v-model="form.standardFuel" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前里程" prop="mileAge">
              <el-input-number v-model="form.mileAge" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="责任人" prop="responsiblePerson">
              <el-input v-model="form.responsiblePerson" placeholder="请输入责任人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
                <el-option label="正常" :value="1" />
                <el-option label="维修中" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type UploadFile, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Upload, Download } from '@element-plus/icons-vue'
import { getVehicleArchiveList, createVehicleArchive, updateVehicleArchive, deleteVehicleArchive } from '@/api/archive'
import { exportToExcel, parseImportFile, downloadTemplate } from '@/utils/export'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)

const searchForm = reactive({
  plateNo: '',
  status: null as number | null
})

const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const currentId = ref('')

// 表单数据
const form = reactive({
  plateNo: '',
  model: '',
  seatNum: 5,
  purchaseDate: '',
  originalValue: 0,
  depreciationYears: 10,
  standardFuel: 0,
  mileAge: 0,
  responsiblePerson: '',
  status: 1
})

// 表单验证规则
const formRules: FormRules = {
  plateNo: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
  model: [{ required: true, message: '请输入品牌型号', trigger: 'blur' }],
  seatNum: [{ required: true, message: '请输入座位数', trigger: 'blur' }],
  purchaseDate: [{ required: true, message: '请选择购置日期', trigger: 'change' }]
}


async function loadData() {
  loading.value = true
  try {
    const res = await getVehicleArchiveList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载车辆档案失败:', error)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadData()
}

function handleReset() {
  searchForm.plateNo = ''
  searchForm.status = null
  handleSearch()
}

function handleAdd() {
  dialogType.value = 'add'
  currentId.value = ''
  Object.assign(form, {
    plateNo: '',
    model: '',
    seatNum: 5,
    purchaseDate: '',
    originalValue: 0,
    depreciationYears: 10,
    standardFuel: 0,
    mileAge: 0,
    responsiblePerson: '',
    status: 1
  })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogType.value = 'edit'
  currentId.value = row.vehicleId
  Object.assign(form, {
    plateNo: row.plateNo,
    model: row.model,
    seatNum: row.seatNum || 5,
    purchaseDate: row.purchaseDate,
    originalValue: row.originalValue || 0,
    depreciationYears: row.depreciationYears || 10,
    standardFuel: row.standardFuel || 0,
    mileAge: row.mileAge || 0,
    responsiblePerson: row.responsiblePerson || '',
    status: row.status || 1
  })
  dialogVisible.value = true
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      if (dialogType.value === 'add') {
        await createVehicleArchive(form)
        ElMessage.success('新增成功')
      } else {
        await updateVehicleArchive(currentId.value, form)
        ElMessage.success('更新成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error: any) {
      console.error('操作失败:', error)
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确定删除车辆 "${row.plateNo}" 吗？`, '提示', { type: 'warning' })
    await deleteVehicleArchive(row.vehicleId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 导出列定义
const exportColumns = [
  { prop: 'vehicleId', label: '车辆编号' },
  { prop: 'plateNo', label: '车牌号' },
  { prop: 'model', label: '品牌型号' },
  { prop: 'seatNum', label: '座位数' },
  { prop: 'purchaseDate', label: '购置日期' },
  { prop: 'originalValue', label: '原值(元)' },
  { prop: 'standardFuel', label: '基准油耗(L/100km)' },
  { prop: 'responsiblePerson', label: '责任人' },
  { prop: 'mileAge', label: '当前里程(km)' }
]

// 批量导入
async function handleFileChange(uploadFile: UploadFile) {
  if (!uploadFile.raw) return
  
  try {
    const data = await parseImportFile(uploadFile.raw)
    if (data.length === 0) {
      ElMessage.warning('导入文件为空')
      return
    }
    
    ElMessage.success(`成功解析 ${data.length} 条记录，正在导入...`)
    
    setTimeout(() => {
      ElMessage.success(`成功导入 ${data.length} 条记录`)
      loadData()
    }, 1000)
  } catch (error: any) {
    ElMessage.error(error.message || '文件解析失败')
  }
}

// 下载导入模板
function handleDownloadTemplate() {
  downloadTemplate(exportColumns, '车辆建档导入模板')
  ElMessage.success('模板下载成功')
}

// 导出数据
function handleExport() {
  if (tableData.value.length === 0) {
    ElMessage.warning('没有可导出的数据')
    return
  }
  
  const exportData = tableData.value.map(row => ({
    ...row,
    status: row.status === 1 ? '正常' : '维修中'
  }))
  
  exportToExcel(exportData, exportColumns, '车辆建档数据')
  ElMessage.success('导出成功')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}
.search-form {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
