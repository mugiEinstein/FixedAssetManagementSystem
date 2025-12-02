<template>
  <div class="inventory-check-page">
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="盘点计划">
        <el-input v-model="searchForm.planName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="资产类型">
        <el-select v-model="searchForm.assetType" placeholder="请选择" clearable>
          <el-option label="房屋" value="building" />
          <el-option label="器材" value="equipment" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待盘点" value="pending" />
          <el-option label="已完成" value="completed" />
        </el-select>
      </el-form-item>
    </SearchBar>

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
      <el-table-column prop="inventoryId" label="盘点编号" width="120" />
      <el-table-column prop="planName" label="盘点计划" width="150" />
      <el-table-column prop="assetName" label="资产名称" width="150" />
      <el-table-column prop="assetType" label="类型" width="90" />
      <el-table-column prop="inventoryDate" label="盘点时间" width="110" />
      <el-table-column prop="physicalStatus" label="实物状态" width="100" />
      <el-table-column prop="netValue" label="净值(元)" width="110" />
      <el-table-column prop="inventoryStatus" label="状态" width="100">
        <template #default="{ row }">
          <AssetStatusTag :status="row.inventoryStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleComplete(row)" v-if="row.inventoryStatus === 'pending'">
            完成盘点
          </el-button>
          <el-button type="info" link @click="handleView(row)">详情</el-button>
        </template>
      </el-table-column>
    </DataTable>

    <!-- 新增盘点弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增盘点计划" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="盘点计划名" prop="planName">
          <el-input v-model="form.planName" placeholder="请输入盘点计划名称" />
        </el-form-item>
        <el-form-item label="资产类型" prop="assetType">
          <el-select v-model="form.assetType" placeholder="请选择" style="width: 100%">
            <el-option label="房屋" value="building" />
            <el-option label="器材" value="equipment" />
          </el-select>
        </el-form-item>
        <el-form-item label="资产名称" prop="assetName">
          <el-input v-model="form.assetName" placeholder="请输入资产名称" />
        </el-form-item>
        <el-form-item label="盘点日期" prop="inventoryDate">
          <el-date-picker v-model="form.inventoryDate" type="date" placeholder="选择日期" 
            format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实物状态" prop="physicalStatus">
          <el-select v-model="form.physicalStatus" placeholder="请选择" style="width: 100%">
            <el-option label="正常" value="normal" />
            <el-option label="损坏" value="damaged" />
            <el-option label="丢失" value="lost" />
          </el-select>
        </el-form-item>
        <el-form-item label="净值(元)" prop="netValue">
          <el-input-number v-model="form.netValue" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
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
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import SearchBar from '@/components/common/SearchBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'
import request from '@/api/index'

const searchForm = reactive({
  planName: '',
  assetType: '',
  status: ''
})
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

const tableData = ref<any[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive({
  planName: '',
  assetType: 'building',
  assetName: '',
  inventoryDate: '',
  physicalStatus: 'normal',
  netValue: 0,
  remark: ''
})

const formRules: FormRules = {
  planName: [{ required: true, message: '请输入盘点计划名称', trigger: 'blur' }],
  assetType: [{ required: true, message: '请选择资产类型', trigger: 'change' }],
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  inventoryDate: [{ required: true, message: '请选择盘点日期', trigger: 'change' }],
  physicalStatus: [{ required: true, message: '请选择实物状态', trigger: 'change' }]
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/inventory/records', { 
      params: { ...queryParams, ...searchForm } 
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载盘点记录失败:', error)
    // 显示模拟数据
    tableData.value = [
      { inventoryId: 'INV-001', planName: '2024春季学期房屋盘点', assetName: '逸夫楼', assetType: 'building', inventoryDate: '2024-04-01', physicalStatus: 'normal', netValue: 28600000, inventoryStatus: 'completed' },
      { inventoryId: 'INV-002', planName: '2024春季学期器材盘点', assetName: '高精度光谱分析仪', assetType: 'equipment', inventoryDate: '2024-04-12', physicalStatus: 'normal', netValue: 325000, inventoryStatus: 'pending' }
    ]
    total.value = 2
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadData()
}

function handleReset() {
  searchForm.planName = ''
  searchForm.assetType = ''
  searchForm.status = ''
  queryParams.page = 1
  loadData()
}

function handleAdd() {
  Object.assign(form, {
    planName: '',
    assetType: 'building',
    assetName: '',
    inventoryDate: '',
    physicalStatus: 'normal',
    netValue: 0,
    remark: ''
  })
  dialogVisible.value = true
}

function handlePageChange(page: number, size: number) {
  queryParams.page = page
  queryParams.pageSize = size
  loadData()
}

// 提交新增
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      await request.post('/inventory/records', {
        ...form,
        inventoryStatus: 'pending'
      })
      ElMessage.success('新增盘点计划成功')
      dialogVisible.value = false
      loadData()
    } catch (error: any) {
      console.error('新增失败:', error)
      ElMessage.error(error.message || '新增失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 完成盘点
async function handleComplete(row: any) {
  try {
    await request.put(`/inventory/records/${row.inventoryId}`, {
      ...row,
      inventoryStatus: 'completed'
    })
    ElMessage.success('盘点完成')
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

function handleView(row: any) {
  ElMessage.info(`查看盘点详情: ${row.planName}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.inventory-check-page {
  height: 100%;
}
</style>