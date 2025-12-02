<template>
  <div class="room-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="所属建筑">
        <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable>
          <el-option
              v-for="item in buildingOptions"
              :key="item.buildingId"
              :label="item.buildingName"
              :value="item.buildingId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="房间类型">
        <el-select v-model="searchForm.roomType" placeholder="请选择" clearable>
          <el-option label="教室" value="classroom" />
          <el-option label="办公室" value="office" />
          <el-option label="宿舍" value="dormitory" />
          <el-option label="实验室" value="lab" />
          <el-option label="会议室" value="meeting" />
          <el-option label="活动场地" value="venue" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="闲置" value="idle" />
          <el-option label="在用" value="in_use" />
          <el-option label="维修中" value="maintenance" />
          <el-option label="已预订" value="reserved" />
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
      <el-table-column prop="roomId" label="房间编号" width="120" />
      <el-table-column prop="buildingName" label="所属建筑" width="120" />
      <el-table-column prop="roomNumber" label="房间号" width="100" />
      <el-table-column prop="roomName" label="房间名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="roomType" label="房间类型" width="100">
        <template #default="{ row }">
          {{ getRoomTypeName(row.roomType) }}
        </template>
      </el-table-column>
      <el-table-column prop="floor" label="楼层" width="80" />
      <el-table-column prop="area" label="面积(㎡)" width="100" />
      <el-table-column prop="capacity" label="容量" width="80" />
      <el-table-column prop="hourlyRate" label="时租(元)" width="100" />
      <el-table-column prop="currentUser" label="当前使用人" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <AssetStatusTag :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除该房间吗？" @confirm="handleDelete(row)">
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
        :title="dialogType === 'add' ? '新增房间' : '编辑房间'"
        width="600px"
        destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属建筑" prop="buildingId">
              <el-select v-model="form.buildingId" placeholder="请选择" style="width: 100%">
                <el-option
                    v-for="item in buildingOptions"
                    :key="item.buildingId"
                    :label="item.buildingName"
                    :value="item.buildingId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber">
              <el-input v-model="form.roomNumber" placeholder="如：101、A-201" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间名称" prop="roomName">
              <el-input v-model="form.roomName" placeholder="请输入房间名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间类型" prop="roomType">
              <el-select v-model="form.roomType" placeholder="请选择" style="width: 100%">
                <el-option label="教室" value="classroom" />
                <el-option label="办公室" value="office" />
                <el-option label="宿舍" value="dormitory" />
                <el-option label="实验室" value="lab" />
                <el-option label="会议室" value="meeting" />
                <el-option label="活动场地" value="venue" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼层" prop="floor">
              <el-input-number v-model="form.floor" :min="-5" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面积(㎡)" prop="area">
              <el-input-number v-model="form.area" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="容量(人)" prop="capacity">
              <el-input-number v-model="form.capacity" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时租金(元)" prop="hourlyRate">
              <el-input-number v-model="form.hourlyRate" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="配套设备" prop="equipment">
          <el-input v-model="form.equipment" type="textarea" :rows="2" placeholder="如：投影仪、空调、多媒体设备等" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="idle">闲置</el-radio>
            <el-radio value="in_use">在用</el-radio>
            <el-radio value="maintenance">维修中</el-radio>
          </el-radio-group>
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
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import SearchBar from '@/components/common/SearchBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'
import { exportToExcel } from '@/utils/export'
import { getRoomList, createRoom, updateRoom, deleteRoom } from '@/api/room'
import { getBuildingList } from '@/api/building'
import type { Room, Building } from '@/types/asset'

// 建筑选项
const buildingOptions = ref<Building[]>([])

// 搜索表单
const searchForm = reactive({
  buildingId: '',
  roomType: '',
  status: ''
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<Room[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const form = reactive<Partial<Room>>({
  buildingId: '',
  roomNumber: '',
  roomName: '',
  roomType: 'classroom',
  floor: 1,
  area: 0,
  capacity: 0,
  equipment: '',
  hourlyRate: 0,
  status: 'idle'
})

// 表单验证规则
const formRules: FormRules = {
  buildingId: [{ required: true, message: '请选择所属建筑', trigger: 'change' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  roomName: [{ required: true, message: '请输入房间名称', trigger: 'blur' }],
  roomType: [{ required: true, message: '请选择房间类型', trigger: 'change' }],
  area: [{ required: true, message: '请输入面积', trigger: 'blur' }]
}

// 获取房间类型名称
function getRoomTypeName(type: string): string {
  const map: Record<string, string> = {
    classroom: '教室',
    office: '办公室',
    dormitory: '宿舍',
    lab: '实验室',
    meeting: '会议室',
    venue: '活动场地'
  }
  return map[type] || type
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const params = {
      page: queryParams.page,
      pageSize: queryParams.pageSize,
      buildingId: searchForm.buildingId || undefined,
      roomType: searchForm.roomType || undefined,
      status: searchForm.status || undefined
    }
    const res = await getRoomList(params)
    console.log('房间API响应:', res) // 调试日志
    // res 是 { code, msg, data: { list, total } }
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载房间数据失败:', error)
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
  searchForm.buildingId = ''
  searchForm.roomType = ''
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
    roomNumber: '',
    roomName: '',
    roomType: 'classroom',
    floor: 1,
    area: 0,
    capacity: 0,
    equipment: '',
    hourlyRate: 0,
    status: 'idle'
  })
  dialogVisible.value = true
}

// 编辑
function handleEdit(row: Room) {
  dialogType.value = 'edit'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 删除
async function handleDelete(row: Room) {
  try {
    await deleteRoom(row.roomId)
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
        await createRoom(form)
        ElMessage.success('新增成功')
      } else {
        await updateRoom(form.roomId!, form)
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
    { prop: 'roomId', label: '房间编号' },
    { prop: 'buildingName', label: '所属建筑' },
    { prop: 'roomNumber', label: '房间号' },
    { prop: 'roomName', label: '房间名称' },
    { prop: 'roomType', label: '房间类型' },
    { prop: 'floor', label: '楼层' },
    { prop: 'area', label: '面积(㎡)' },
    { prop: 'capacity', label: '容量' },
    { prop: 'hourlyRate', label: '时租(元)' },
    { prop: 'currentUser', label: '当前使用人' },
    { prop: 'status', label: '状态' }
  ]
  
  // 处理数据格式
  const exportData = tableData.value.map(row => ({
    ...row,
    roomType: getRoomTypeName(row.roomType || ''),
    status: row.status === 'idle' ? '闲置' : row.status === 'in_use' ? '在用' : row.status === 'maintenance' ? '维修中' : '已预订'
  }))
  
  exportToExcel(exportData, columns, '房间列表')
  ElMessage.success('导出成功')
}

// 加载建筑选项
async function loadBuildingOptions() {
  try {
    const res = await getBuildingList({ page: 1, pageSize: 100 })
    buildingOptions.value = res.data?.list || []
  } catch (error) {
    console.error('加载建筑列表失败:', error)
  }
}

onMounted(() => {
  loadBuildingOptions()
  loadData()
})
</script>

<style scoped>
.room-page {
  height: 100%;
}
</style>