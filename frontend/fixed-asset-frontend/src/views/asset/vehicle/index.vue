<template>
  <div class="vehicle-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="车牌号">
        <el-input v-model="searchForm.plateNo" placeholder="请输入车牌号" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="正常" :value="1" />
          <el-option label="维修中" :value="2" />
          <el-option label="使用中" :value="3" />
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
      <el-table-column prop="vehicleId" label="车辆编号" width="140" />
      <el-table-column prop="plateNo" label="车牌号" width="120" />
      <el-table-column prop="model" label="品牌型号" min-width="180" />
      <el-table-column prop="seatNum" label="座位数" width="80" />
      <el-table-column prop="purchaseDate" label="购置日期" width="110" />
      <el-table-column prop="originalValue" label="原值(元)" width="120">
        <template #default="{ row }">
          {{ row.originalValue ? `¥${row.originalValue.toLocaleString()}` : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="mileAge" label="行驶里程(km)" width="120">
        <template #default="{ row }">
          {{ row.mileAge ? row.mileAge.toLocaleString() : '0' }}
        </template>
      </el-table-column>
      <el-table-column prop="standardFuel" label="基准油耗" width="100">
        <template #default="{ row }">
          {{ row.standardFuel ? `${row.standardFuel}L` : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除该车辆吗？" @confirm="handleDelete(row)">
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
        :title="dialogType === 'add' ?  '新增车辆' : '编辑车辆'"
        width="600px"
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
              <el-input v-model="form.model" placeholder="请输入品牌型号" />
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
            <el-form-item label="行驶里程" prop="mileAge">
              <el-input-number v-model="form.mileAge" :min="0" style="width: 100%" />
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
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
                <el-option label="正常" :value="1" />
                <el-option label="维修中" :value="2" />
                <el-option label="使用中" :value="3" />
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
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import SearchBar from '@/components/common/SearchBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import { getVehicleList, createVehicle, updateVehicle, deleteVehicle } from '@/api/vehicle'
import type { Vehicle } from '@/types/asset'

// 搜索表单
const searchForm = reactive({
  plateNo: '',
  status: null as number | null
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<Vehicle[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const form = reactive<Partial<Vehicle>>({
  plateNo: '',
  model: '',
  seatNum: 1,
  purchaseDate: '',
  originalValue: 0,
  mileAge: 0,
  standardFuel: 0,
  status: 1
})

// 表单验证规则
const formRules: FormRules = {
  plateNo: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
  model: [{ required: true, message: '请输入品牌型号', trigger: 'blur' }],
  seatNum: [{ required: true, message: '请输入座位数', trigger: 'blur' }],
  originalValue: [{ required: true, message: '请输入原值', trigger: 'blur' }]
}

// 获取状态文本
function getStatusText(status: number): string {
  const map: Record<number, string> = {
    1: '正常',
    2: '维修中',
    3: '使用中'
  }
  return map[status] || '未知'
}

// 获取状态标签类型
function getStatusType(status: number): string {
  const map: Record<number, string> = {
    1: 'success',
    2: 'warning',
    3: 'info'
  }
  return map[status] || 'info'
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getVehicleList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载车辆数据失败:', error)
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
  searchForm.plateNo = ''
  searchForm.status = null
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
    plateNo: '',
    model: '',
    seatNum: 5,
    purchaseDate: '',
    originalValue: 0,
    mileAge: 0,
    standardFuel: 0,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
function handleEdit(row: Vehicle) {
  dialogType.value = 'edit'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 删除
async function handleDelete(row: Vehicle) {
  try {
    await deleteVehicle(row.vehicleId)
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
        await createVehicle(form)
        ElMessage.success('新增成功')
      } else {
        await updateVehicle(form.vehicleId!, form)
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
  ElMessage.warning('导出功能需要后端支持')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.vehicle-page {
  height: 100%;
}
</style>