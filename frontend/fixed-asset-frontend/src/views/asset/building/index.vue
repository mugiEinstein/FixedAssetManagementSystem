<template>
  <div class="building-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="建筑名称">
        <el-input v-model="searchForm.buildingName" placeholder="请输入建筑名称" clearable />
      </el-form-item>
      <el-form-item label="建筑类型">
        <el-select v-model="searchForm.buildingType" placeholder="请选择" clearable>
          <el-option label="教学楼" value="教学楼" />
          <el-option label="办公楼" value="办公楼" />
          <el-option label="宿舍楼" value="宿舍楼" />
          <el-option label="实验楼" value="实验楼" />
          <el-option label="生活用房" value="生活用房" />
          <el-option label="医疗建筑" value="医疗建筑" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="正常" value="正常" />
          <el-option label="维修中" value="维修中" />
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
        @export="handleExport"
        @page-change="handlePageChange"
    >
      <el-table-column prop="buildingId" label="建筑编号" width="120" />
      <el-table-column prop="buildingName" label="建筑名称" min-width="150" />
      <el-table-column prop="buildingType" label="建筑类型" width="120" />
      <el-table-column prop="area" label="建筑面积(㎡)" width="120" />
      <el-table-column prop="buildYear" label="建成年份" width="100" />
      <el-table-column prop="depreciationYears" label="折旧年限" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '正常' ? 'success' : 'warning'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleView(row)">详情</el-button>
          <el-popconfirm title="确定删除该建筑吗？" @confirm="handleDelete(row)">
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
        :title="dialogType === 'add' ? '新增建筑' : '编辑建筑'"
        width="600px"
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
                <el-option label="教学楼" value="教学楼" />
                <el-option label="办公楼" value="办公楼" />
                <el-option label="宿舍楼" value="宿舍楼" />
                <el-option label="实验楼" value="实验楼" />
                <el-option label="生活用房" value="生活用房" />
                <el-option label="医疗建筑" value="医疗建筑" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="建筑面积(㎡)" prop="area">
              <el-input-number v-model="form.area" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="建成年份" prop="buildYear">
              <el-input-number v-model="form.buildYear" :min="1900" :max="2100" style="width: 100%" />
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
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
                <el-option label="正常" value="正常" />
                <el-option label="维修中" value="维修中" />
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="建筑详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="建筑编号">{{ currentRow?.buildingId }}</el-descriptions-item>
        <el-descriptions-item label="建筑名称">{{ currentRow?.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="建筑类型">{{ currentRow?.buildingType }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentRow?.status === '正常' ? 'success' : 'warning'">{{ currentRow?.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="建筑面积">{{ currentRow?.area }} ㎡</el-descriptions-item>
        <el-descriptions-item label="建成年份">{{ currentRow?.buildYear }} 年</el-descriptions-item>
        <el-descriptions-item label="折旧年限">{{ currentRow?.depreciationYears }} 年</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRow?.createdAt }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import SearchBar from '@/components/common/SearchBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'
import { getBuildingList, createBuilding, updateBuilding, deleteBuilding } from '@/api/building'
import { exportToExcel } from '@/utils/export'
import type { Building } from '@/types/asset'

// 搜索表单
const searchForm = reactive({
  buildingName: '',
  buildingType: '',
  status: ''
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<Building[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const detailVisible = ref(false)
const currentRow = ref<Building | null>(null)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const form = reactive<Partial<Building>>({
  buildingName: '',
  buildingType: '教学楼',
  area: 0,
  buildYear: new Date().getFullYear(),
  depreciationYears: 50,
  status: '正常'
})

// 表单验证规则
const formRules: FormRules = {
  buildingName: [{ required: true, message: '请输入建筑名称', trigger: 'blur' }],
  buildingType: [{ required: true, message: '请选择建筑类型', trigger: 'change' }],
  area: [{ required: true, message: '请输入建筑面积', trigger: 'blur' }],
  buildYear: [{ required: true, message: '请输入建成年份', trigger: 'blur' }],
  depreciationYears: [{ required: true, message: '请输入折旧年限', trigger: 'blur' }]
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const params = {
      page: queryParams.page,
      pageSize: queryParams.pageSize,
      name: searchForm.buildingName || undefined,
      type: searchForm.buildingType || undefined,
      status: searchForm.status || undefined
    }
    const res = await getBuildingList(params)
    console.log('建筑API响应:', res) // 调试日志
    // res 是 { code, msg, data: { list, total } }
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载建筑数据失败:', error)
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
    buildingId: '',
    buildingName: '',
    buildingType: '教学楼',
    area: 0,
    buildYear: new Date().getFullYear(),
    depreciationYears: 50,
    status: '正常'
  })
  dialogVisible.value = true
}

// 编辑
function handleEdit(row: Building) {
  dialogType.value = 'edit'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 查看详情
function handleView(row: Building) {
  currentRow.value = row
  detailVisible.value = true
}

// 删除
async function handleDelete(row: Building) {
  try {
    await deleteBuilding(row.buildingId)
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
        await createBuilding(form)
        ElMessage.success('新增成功')
      } else {
        await updateBuilding(form.buildingId!, form)
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
function handleExport() {
  if (tableData.value.length === 0) {
    ElMessage.warning('没有可导出的数据')
    return
  }
  
  const columns = [
    { prop: 'buildingId', label: '建筑编号' },
    { prop: 'buildingName', label: '建筑名称' },
    { prop: 'buildingType', label: '建筑类型' },
    { prop: 'area', label: '建筑面积(㎡)' },
    { prop: 'buildYear', label: '建成年份' },
    { prop: 'depreciationYears', label: '折旧年限' },
    { prop: 'status', label: '状态' }
  ]
  
  exportToExcel(tableData.value, columns, '建筑列表')
  ElMessage.success('导出成功')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.building-page {
  height: 100%;
}
</style>