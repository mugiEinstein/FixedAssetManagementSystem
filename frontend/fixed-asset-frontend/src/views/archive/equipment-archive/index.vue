<template>
  <div class="equipment-archive-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>器材档案</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增档案</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="器材名称">
          <el-input v-model="searchForm.equipmentName" placeholder="请输入器材名称" clearable />
        </el-form-item>
        <el-form-item label="器材类型">
          <el-select v-model="searchForm.equipmentType" placeholder="请选择" clearable>
            <el-option label="通用设备" value="TY" />
            <el-option label="电子设备" value="DZ" />
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
        <el-table-column prop="equipmentId" label="器材编号" width="120" />
        <el-table-column prop="equipmentName" label="器材名称" min-width="150" />
        <el-table-column prop="equipmentType" label="器材类型" width="100">
          <template #default="{ row }">
            {{ row.equipmentType === 'TY' ? '通用设备' : '电子设备' }}
          </template>
        </el-table-column>
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="specifications" label="规格" width="120" />
        <el-table-column prop="originalValue" label="原值(元)" width="120">
          <template #default="{ row }">
            {{ row.originalValue.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="购置日期" width="120" />
        <el-table-column prop="storageLocation" label="存放位置" width="150" show-overflow-tooltip />
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
        :title="dialogType === 'add' ? '新增器材档案' : '编辑器材档案'"
        width="700px"
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
                <el-option label="通用设备" value="TY" />
                <el-option label="电子设备" value="DZ" />
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
            <el-form-item label="存放位置" prop="storageLocation">
              <el-input v-model="form.storageLocation" placeholder="请输入存放位置" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="折旧年限" prop="depreciationYears">
              <el-input-number v-model="form.depreciationYears" :min="1" :max="50" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保管人" prop="custodian">
              <el-input v-model="form.custodian" placeholder="请输入保管人" />
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
    <el-dialog v-model="detailVisible" title="器材档案详情" width="650px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="器材编号">{{ currentRow.equipmentId }}</el-descriptions-item>
        <el-descriptions-item label="器材名称">{{ currentRow.equipmentName }}</el-descriptions-item>
        <el-descriptions-item label="器材类型">{{ currentRow.equipmentType === 'TY' ? '通用设备' : '电子设备' }}</el-descriptions-item>
        <el-descriptions-item label="型号">{{ currentRow.model || '-' }}</el-descriptions-item>
        <el-descriptions-item label="规格">{{ currentRow.specifications || '-' }}</el-descriptions-item>
        <el-descriptions-item label="原值">{{ currentRow.originalValue?.toFixed(2) || '0.00' }} 元</el-descriptions-item>
        <el-descriptions-item label="购置日期">{{ currentRow.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="生产厂家">{{ currentRow.manufacturer || '-' }}</el-descriptions-item>
        <el-descriptions-item label="存放位置">{{ currentRow.storageLocation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="折旧年限">{{ currentRow.depreciationYears }} 年</el-descriptions-item>
        <el-descriptions-item label="保管人">{{ currentRow.custodian || '-' }}</el-descriptions-item>
        <el-descriptions-item label="建档状态">
          <el-tag :type="currentRow.archiveStatus === 'completed' ? 'success' : 'warning'">
            {{ currentRow.archiveStatus === 'completed' ? '已建档' : '待完善' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRow.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { getEquipmentArchiveList, createEquipmentArchive, updateEquipmentArchive, deleteEquipmentArchive } from '@/api/archive'

// 搜索表单
const searchForm = reactive({
  equipmentName: '',
  equipmentType: ''
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
  equipmentName: '',
  equipmentType: 'TY',
  model: '',
  specifications: '',
  originalValue: 0,
  purchaseDate: '',
  manufacturer: '',
  storageLocation: '',
  depreciationYears: 10,
  custodian: '',
  remark: ''
})

// 表单验证规则
const formRules: FormRules = {
  equipmentName: [{ required: true, message: '请输入器材名称', trigger: 'blur' }],
  equipmentType: [{ required: true, message: '请选择器材类型', trigger: 'change' }],
  originalValue: [{ required: true, message: '请输入原值', trigger: 'blur' }],
  purchaseDate: [{ required: true, message: '请选择购置日期', trigger: 'change' }]
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getEquipmentArchiveList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载器材档案失败:', error)
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
  queryParams.page = 1
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
    storageLocation: '',
    depreciationYears: 10,
    custodian: '',
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
    await ElMessageBox.confirm('确定要删除该器材档案吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteEquipmentArchive(row.equipmentId)
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
        await createEquipmentArchive(form)
        ElMessage.success('新增成功')
      } else {
        await updateEquipmentArchive((form as any).equipmentId, form)
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.equipment-archive-page {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
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
