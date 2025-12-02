<template>
  <div class="student-housing-page">
    <!-- 搜索栏 -->
    <SearchBar @search="handleSearch" @reset="handleReset">
      <el-form-item label="学生姓名">
        <el-input v-model="searchForm.studentName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="学院">
        <el-select v-model="searchForm.college" placeholder="请选择" clearable>
          <el-option label="计算机学院" value="计算机学院" />
          <el-option label="材料学院" value="材料学院" />
          <el-option label="物理学院" value="物理学院" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="在住" value="active" />
          <el-option label="已调换" value="transferred" />
          <el-option label="已退宿" value="exited" />
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
      <template #toolbar-right>
        <el-button type="warning" @click="goToReview">
          <el-icon><Finished /></el-icon>
          审核申请
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
      </template>
      <el-table-column prop="studentId" label="学号" width="120" />
      <el-table-column prop="studentName" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="80">
        <template #default="{ row }">
          {{ row.gender === 'male' ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="department" label="院系" width="150" />
      <el-table-column prop="grade" label="年级" width="100" />
      <el-table-column prop="buildingName" label="宿舍楼" width="150" />
      <el-table-column prop="roomNo" label="房间号" width="100" />
      <el-table-column prop="bedNo" label="床位号" width="80" />
      <el-table-column prop="checkInDate" label="入住日期" width="110" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <AssetStatusTag :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">详情</el-button>
          <el-button
              v-if="isActiveStatus(row.status)"
              type="warning"
              link
              @click="handleTransfer(row)"
          >
            调换
          </el-button>
          <el-button
              v-if="isActiveStatus(row.status)"
              type="danger"
              link
              @click="handleExit(row)"
          >
            退宿
          </el-button>
        </template>
      </el-table-column>
    </DataTable>

    <!-- 分配住宿弹窗 -->
    <el-dialog v-model="dialogVisible" title="分配学生住宿" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="studentName">
              <el-input v-model="form.studentName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio value="male">男</el-radio>
                <el-radio value="female">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级" prop="grade">
              <el-select v-model="form.grade" placeholder="请选择" style="width: 100%">
                <el-option label="2024级" value="2024级" />
                <el-option label="2023级" value="2023级" />
                <el-option label="2022级" value="2022级" />
                <el-option label="2021级" value="2021级" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="院系" prop="department">
              <el-select v-model="form.department" placeholder="请选择" style="width: 100%">
                <el-option label="计算机学院" value="计算机学院" />
                <el-option label="材料学院" value="材料学院" />
                <el-option label="物理学院" value="物理学院" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">分配宿舍</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择宿舍" prop="buildingId">
              <el-select v-model="form.buildingId" placeholder="请选择宿舍" style="width: 100%">
                <el-option label="1斋-101 (剩余2床位)" value="D-1-101" />
                <el-option label="1斋-102 (剩余1床位)" value="D-1-102" />
                <el-option label="2斋-201 (剩余3床位)" value="D-2-201" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="床位号" prop="bedNo">
              <el-select v-model="form.bedNo" placeholder="请选择床位" style="width: 100%">
                <el-option :label="1" :value="1" />
                <el-option :label="2" :value="2" />
                <el-option :label="3" :value="3" />
                <el-option :label="4" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="入住日期" prop="checkInDate">
          <el-date-picker
              v-model="form.checkInDate"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定分配</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="住宿详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学号">{{ currentRow?.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentRow?.studentName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentRow?.gender === 'male' ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ currentRow?.grade }}</el-descriptions-item>
        <el-descriptions-item label="院系">{{ currentRow?.department }}</el-descriptions-item>
        <el-descriptions-item label="宿舍楼">{{ currentRow?.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ currentRow?.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="床位号">{{ currentRow?.bedNo }} 号床</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ currentRow?.checkInDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <AssetStatusTag v-if="currentRow" :status="currentRow.status" />
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 调换弹窗 -->
    <el-dialog v-model="transferVisible" title="宿舍调换" width="500px">
      <el-form :model="transferForm" label-width="100px">
        <el-form-item label="当前宿舍">
          <el-input :value="currentRow?.buildingName + ' ' + currentRow?.roomNo" disabled />
        </el-form-item>
        <el-form-item label="当前床位">
          <el-input :value="currentRow?.bedNo + ' 号床'" disabled />
        </el-form-item>
        <el-divider />
        <el-form-item label="调换至宿舍">
          <el-select v-model="transferForm.newDormId" placeholder="请选择新宿舍" style="width: 100%">
            <el-option label="1斋-103 (剩余2床位)" value="D-1-103" />
            <el-option label="2斋-202 (剩余1床位)" value="D-2-202" />
          </el-select>
        </el-form-item>
        <el-form-item label="新床位号">
          <el-select v-model="transferForm.newBedNumber" placeholder="请选择床位" style="width: 100%">
            <el-option :label="1" :value="1" />
            <el-option :label="2" :value="2" />
            <el-option :label="3" :value="3" />
            <el-option :label="4" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="调换原因">
          <el-input v-model="transferForm.reason" type="textarea" :rows="3" placeholder="请输入调换原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTransfer">确认调换</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Finished, Download } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import SearchBar from '@/components/common/SearchBar.vue'
import DataTable from '@/components/common/DataTable.vue'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'
import { getStudentHousingList, checkInStudent, checkOutStudent, changeDormitory, type StudentDormAlloc } from '@/api/housing'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  studentName: '',
  college: '',
  status: ''
})

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<StudentDormAlloc[]>([])
const total = ref(0)
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const detailVisible = ref(false)
const transferVisible = ref(false)
const currentRow = ref<StudentDormAlloc | null>(null)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const form = reactive({
  studentNo: '',
  studentName: '',
  gender: 'male' as string,
  department: '',
  grade: '',
  buildingId: '',
  roomNo: '',
  bedNo: '',
  checkInDate: ''
})

// 表单验证规则
const formRules: FormRules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  department: [{ required: true, message: '请选择院系', trigger: 'change' }],
  buildingId: [{ required: true, message: '请选择宿舍楼', trigger: 'change' }],
  bedNo: [{ required: true, message: '请选择床位', trigger: 'change' }],
  checkInDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }]
}

// 调换表单
const transferForm = reactive({
  newDormId: '',
  newBedNumber: 1,
  reason: ''
})

// 判断是否为在住状态（兼容多种状态值）
function isActiveStatus(status: string): boolean {
  const activeStatuses = ['active', 'in_use', 'occupied', '在住', '入住中']
  return activeStatuses.includes(status)
}

// 跳转到审核页面
function goToReview() {
  router.push('/business/housing/housing-review')
}

// 导出数据
function handleExport() {
  ElMessage.info('导出功能开发中...')
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getStudentHousingList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载学生住宿记录失败:', error)
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
  searchForm.studentName = ''
  searchForm.college = ''
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
    studentNo: '',
    studentName: '',
    gender: 'male',
    department: '',
    grade: '',
    buildingId: '',
    roomNo: '',
    bedNo: '',
    checkInDate: ''
  })
  dialogVisible.value = true
}

// 查看详情
function handleView(row: StudentDormAlloc) {
  currentRow.value = row
  detailVisible.value = true
}

// 调换
function handleTransfer(row: StudentDormAlloc) {
  currentRow.value = row
  transferForm.newDormId = ''
  transferForm.newBedNumber = 1
  transferForm.reason = ''
  transferVisible.value = true
}

// 提交调换
async function submitTransfer() {
  if (!transferForm.newDormId) {
    ElMessage.warning('请选择新宿舍')
    return
  }
  if (!currentRow.value) return
  try {
    await changeDormitory(currentRow.value.allocId, {
      newBuildingId: transferForm.newDormId,
      newRoomNo: transferForm.newDormId,
      newBedNo: String(transferForm.newBedNumber),
      reason: transferForm.reason
    })
    ElMessage.success('宿舍调换成功')
    transferVisible.value = false
    loadData()
  } catch (error: any) {
    console.error('调换失败:', error)
    ElMessage.error(error.message || '调换失败')
  }
}

// 退宿
async function handleExit(row: StudentDormAlloc) {
  try {
    await ElMessageBox.confirm(`确定要为学生 ${row.studentName} 办理退宿吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await checkOutStudent(row.allocId, { reason: '正常退宿' })
    ElMessage.success('退宿办理成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('退宿失败:', error)
      ElMessage.error(error.message || '退宿失败')
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
      await checkInStudent(form as any)
      ElMessage.success('住宿分配成功')
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
.student-housing-page {
  height: 100%;
}
</style>