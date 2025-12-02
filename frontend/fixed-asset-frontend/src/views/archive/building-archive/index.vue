<template>
  <div class="building-archive-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>房屋建档</span>
          <div class="header-actions">
            <el-upload
              ref="uploadRef"
              :auto-upload="false"
              :show-file-list="false"
              accept=".csv"
              :on-change="handleFileChange"
            >
              <el-button :icon="Upload">批量导入(CSV)</el-button>
            </el-upload>
            <el-button :icon="Download" @click="handleDownloadTemplate">下载模板</el-button>
            <el-button :icon="Download" @click="handleExport">导出数据</el-button>
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增档案</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="建筑名称">
          <el-input v-model="searchForm.buildingName" placeholder="请输入建筑名称" clearable />
        </el-form-item>
        <el-form-item label="建筑类型">
          <el-select v-model="searchForm.buildingType" placeholder="请选择" clearable>
            <el-option label="教学用房" value="teaching" />
            <el-option label="办公用房" value="office" />
            <el-option label="生活用房" value="living" />
            <el-option label="其他用房" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="buildingId" label="建筑编号" width="120" />
        <el-table-column prop="buildingName" label="建筑名称" min-width="150" />
        <el-table-column prop="buildingType" label="建筑类型" width="120">
          <template #default="{ row }">
            {{ getBuildingTypeName(row.buildingType) }}
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="totalArea" label="总面积(㎡)" width="120" />
        <el-table-column prop="floors" label="楼层数" width="80" />
        <el-table-column prop="buildYear" label="建成年份" width="100" />
        <el-table-column prop="originalValue" label="原值(万元)" width="120">
          <template #default="{ row }">
            {{ (row.originalValue / 10000).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="archiveStatus" label="建档状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.archiveStatus === 'completed' ? 'success' : 'warning'">
              {{ row.archiveStatus === 'completed' ? '已建档' : '待完善' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-popconfirm title="确定删除该档案吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadData"
            @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '新增房屋档案' : '编辑房屋档案'"
        width="700px"
        destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="建筑名称" prop="buildingName">
              <el-input v-model="form.buildingName" placeholder="请输入建筑名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="建筑类型" prop="buildingType">
              <el-select v-model="form.buildingType" placeholder="请选择" style="width: 100%">
                <el-option label="教学用房" value="teaching" />
                <el-option label="办公用房" value="office" />
                <el-option label="生活用房" value="living" />
                <el-option label="其他用房" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总面积(㎡)" prop="totalArea">
              <el-input-number v-model="form.totalArea" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼层数" prop="floors">
              <el-input-number v-model="form.floors" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="建成年份" prop="buildYear">
              <el-date-picker
                  v-model="form.buildYear"
                  type="year"
                  placeholder="选择年份"
                  format="YYYY"
                  value-format="YYYY"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原值(元)" prop="originalValue">
              <el-input-number v-model="form.originalValue" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="折旧年限" prop="depreciationYears">
              <el-input-number v-model="form.depreciationYears" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产权归属" prop="propertyOwner">
              <el-input v-model="form.propertyOwner" placeholder="请输入产权归属" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="建筑档案详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="建筑编号">{{ currentRow.buildingId }}</el-descriptions-item>
        <el-descriptions-item label="建筑名称">{{ currentRow.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="建筑类型">{{ getBuildingTypeName(currentRow.buildingType) }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ currentRow.address }}</el-descriptions-item>
        <el-descriptions-item label="总面积">{{ currentRow.totalArea }} ㎡</el-descriptions-item>
        <el-descriptions-item label="楼层数">{{ currentRow.floors }} 层</el-descriptions-item>
        <el-descriptions-item label="建成年份">{{ currentRow.buildYear }}</el-descriptions-item>
        <el-descriptions-item label="原值">{{ (currentRow.originalValue / 10000).toFixed(2) }} 万元</el-descriptions-item>
        <el-descriptions-item label="折旧年限">{{ currentRow.depreciationYears }} 年</el-descriptions-item>
        <el-descriptions-item label="产权归属">{{ currentRow.propertyOwner || '-' }}</el-descriptions-item>
        <el-descriptions-item label="建档状态" :span="2">
          <el-tag :type="currentRow.archiveStatus === 'completed' ? 'success' : 'warning'">
            {{ currentRow.archiveStatus === 'completed' ? '已建档' : '待完善' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile } from 'element-plus'
import { Plus, Search, Refresh, Upload, Download } from '@element-plus/icons-vue'
import { exportToExcel, parseImportFile, downloadTemplate } from '@/utils/export'
import { getBuildingArchiveList, createBuildingArchive, updateBuildingArchive, deleteBuildingArchive } from '@/api/archive'

// 搜索表单
const searchForm = reactive({
  buildingName: '',
  buildingType: ''
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<any[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const detailVisible = ref(false)
const currentRow = ref<any>(null)

// 表单数据
const form = reactive({
  buildingName: '',
  buildingType: 'teaching',
  address: '',
  totalArea: 0,
  floors: 1,
  buildYear: new Date().getFullYear(),
  originalValue: 0,
  depreciationYears: 50,
  propertyOwner: '',
  remark: ''
})

// 表单验证规则
const formRules: FormRules = {
  buildingName: [{ required: true, message: '请输入建筑名称', trigger: 'blur' }],
  buildingType: [{ required: true, message: '请选择建筑类型', trigger: 'change' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  totalArea: [{ required: true, message: '请输入总面积', trigger: 'blur' }]
}

// 获取建筑类型名称
function getBuildingTypeName(type: string): string {
  const map: Record<string, string> = {
    teaching: '教学用房',
    office: '办公用房',
    living: '生活用房',
    other: '其他用房'
  }
  return map[type] || type
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getBuildingArchiveList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载建筑档案失败:', error)
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
  searchForm.buildingName = ''
  searchForm.buildingType = ''
  queryParams.page = 1
  loadData()
}

// 新增
function handleAdd() {
  dialogType.value = 'add'
  Object.assign(form, {
    buildingName: '',
    buildingType: 'teaching',
    address: '',
    totalArea: 0,
    floors: 1,
    buildYear: new Date().getFullYear(),
    originalValue: 0,
    depreciationYears: 50,
    propertyOwner: '',
    remark: ''
  })
  dialogVisible.value = true
}

// 编辑
function handleEdit(row: any) {
  dialogType.value = 'edit'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 查看详情
function handleView(row: any) {
  currentRow.value = row
  detailVisible.value = true
}

// 删除
async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除该建筑档案吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteBuildingArchive(row.buildingId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
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
        await createBuildingArchive(form)
        ElMessage.success('新增成功')
      } else {
        await updateBuildingArchive((form as any).buildingId, form)
        ElMessage.success('更新成功')
      }
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

// 导出列定义
const exportColumns = [
  { prop: 'buildingName', label: '建筑名称' },
  { prop: 'buildingType', label: '建筑类型' },
  { prop: 'address', label: '地址' },
  { prop: 'totalArea', label: '总面积(㎡)' },
  { prop: 'floors', label: '楼层数' },
  { prop: 'buildYear', label: '建成年份' },
  { prop: 'originalValue', label: '原值(元)' },
  { prop: 'depreciationYears', label: '折旧年限' },
  { prop: 'propertyOwner', label: '产权归属' }
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
    
    // 批量调用API导入
    let successCount = 0
    let failCount = 0
    for (const row of data) {
      try {
        // 映射字段名
        const buildingData = {
          buildingName: row['建筑名称'] || row.buildingName,
          buildingType: mapBuildingType(row['建筑类型'] || row.buildingType),
          address: row['地址'] || row.address,
          totalArea: parseFloat(row['总面积(㎡)'] || row.totalArea || 0),
          floors: parseInt(row['楼层数'] || row.floors || 1),
          buildYear: parseInt(row['建成年份'] || row.buildYear || new Date().getFullYear()),
          originalValue: parseFloat(row['原值(元)'] || row.originalValue || 0),
          depreciationYears: parseInt(row['折旧年限'] || row.depreciationYears || 50),
          propertyOwner: row['产权归属'] || row.propertyOwner || ''
        }
        await createBuildingArchive(buildingData)
        successCount++
      } catch (e) {
        failCount++
        console.error('导入单条记录失败:', e)
      }
    }
    
    if (failCount === 0) {
      ElMessage.success(`成功导入 ${successCount} 条记录`)
    } else {
      ElMessage.warning(`导入完成: 成功 ${successCount} 条, 失败 ${failCount} 条`)
    }
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '文件解析失败')
  }
}

// 映射建筑类型
function mapBuildingType(type: string): string {
  const map: Record<string, string> = {
    '教学用房': 'teaching',
    '办公用房': 'office',
    '生活用房': 'living',
    '其他用房': 'other'
  }
  return map[type] || type || 'other'
}

// 下载导入模板(带示例数据)
function handleDownloadTemplate() {
  const header = exportColumns.map(col => col.label).join(',')
  const exampleRow = '示例教学楼,教学用房,北京市海淀区,5000,6,2015,50000000,50,学校'
  const csvContent = '\uFEFF' + header + '\n' + exampleRow + '\n'
  
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = '房屋建档导入模板.csv'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
  ElMessage.success('模板下载成功，请按照示例行格式填写数据')
}

// 导出数据
function handleExport() {
  if (tableData.value.length === 0) {
    ElMessage.warning('没有可导出的数据')
    return
  }
  
  const exportData = tableData.value.map(row => ({
    ...row,
    buildingType: getBuildingTypeName(row.buildingType)
  }))
  
  exportToExcel(exportData, exportColumns, '房屋建档数据')
  ElMessage.success('导出成功')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.building-archive-page {
  height: 100%;
}

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

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
