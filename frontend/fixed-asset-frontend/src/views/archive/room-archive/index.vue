<template>
  <div class="room-archive">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>房间建档</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增房间
          </el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="房间名称">
          <el-input v-model="searchForm.roomName" placeholder="请输入房间名称" clearable />
        </el-form-item>
        <el-form-item label="所属建筑">
          <el-select v-model="searchForm.buildingId" placeholder="请选择建筑" clearable>
            <el-option v-for="item in buildings" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="roomCode" label="房间编号" width="120" />
        <el-table-column prop="roomName" label="房间名称" />
        <el-table-column prop="buildingName" label="所属建筑" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="roomType" label="房间类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getRoomTypeName(row.roomType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="area" label="面积(㎡)" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'normal' ? 'success' : 'warning'">
              {{ row.status === 'normal' ? '正常' : '维修中' }}
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
      :title="dialogType === 'add' ? '新增房间' : '编辑房间'"
      width="600px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间名称" prop="roomName">
              <el-input v-model="form.roomName" placeholder="请输入房间名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间类型" prop="roomType">
              <el-select v-model="form.roomType" placeholder="请选择" style="width: 100%">
                <el-option label="办公室" value="office" />
                <el-option label="教室" value="classroom" />
                <el-option label="实验室" value="lab" />
                <el-option label="宿舍" value="dormitory" />
                <el-option label="会议室" value="meeting" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属建筑" prop="buildingId">
              <el-select v-model="form.buildingId" placeholder="请选择" style="width: 100%">
                <el-option v-for="item in buildings" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼层" prop="floor">
              <el-input-number v-model="form.floor" :min="1" :max="50" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="面积(㎡)" prop="area">
              <el-input-number v-model="form.area" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="容纳人数" prop="capacity">
              <el-input-number v-model="form.capacity" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择" style="width: 200px">
            <el-option label="正常" value="normal" />
            <el-option label="维修中" value="maintenance" />
          </el-select>
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
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getRoomArchiveList, createRoomArchive, updateRoomArchive, deleteRoomArchive, getAllBuildings } from '@/api/archive'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const buildings = ref<any[]>([])

// 加载建筑列表
async function loadBuildings() {
  try {
    const res = await getAllBuildings()
    buildings.value = (res.data || []).map((item: any) => ({
      id: item.buildingId,
      name: item.buildingName
    }))
  } catch (error) {
    console.error('加载建筑列表失败:', error)
  }
}

const searchForm = reactive({
  roomName: '',
  buildingId: ''
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
  roomName: '',
  roomType: 'office',
  buildingId: '',
  floor: 1,
  area: 0,
  capacity: 0,
  status: 'normal'
})

// 表单验证规则
const formRules: FormRules = {
  roomName: [{ required: true, message: '请输入房间名称', trigger: 'blur' }],
  roomType: [{ required: true, message: '请选择房间类型', trigger: 'change' }],
  buildingId: [{ required: true, message: '请选择所属建筑', trigger: 'change' }]
}

function getRoomTypeName(type: string) {
  const map: Record<string, string> = {
    office: '办公室',
    classroom: '教室',
    lab: '实验室',
    dormitory: '宿舍',
    meeting: '会议室'
  }
  return map[type] || type
}

async function loadData() {
  loading.value = true
  try {
    const res = await getRoomArchiveList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载房屋档案失败:', error)
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
  searchForm.roomName = ''
  searchForm.buildingId = ''
  handleSearch()
}

function handleAdd() {
  dialogType.value = 'add'
  currentId.value = ''
  Object.assign(form, {
    roomName: '',
    roomType: 'office',
    buildingId: '',
    floor: 1,
    area: 0,
    capacity: 0,
    status: 'normal'
  })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  dialogType.value = 'edit'
  currentId.value = row.roomCode || row.roomId
  Object.assign(form, {
    roomName: row.roomName || '',
    roomType: row.roomType || 'office',
    buildingId: row.buildingId || '',
    floor: row.floor || 1,
    area: row.area || 0,
    capacity: row.capacity || 0,
    status: row.status || 'normal'
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
        await createRoomArchive(form)
        ElMessage.success('新增成功')
      } else {
        await updateRoomArchive(currentId.value, form)
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
    await ElMessageBox.confirm(`确定删除房间 "${row.roomName}" 吗？`, '提示', { type: 'warning' })
    await deleteRoomArchive(row.roomCode)
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadBuildings()
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
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
