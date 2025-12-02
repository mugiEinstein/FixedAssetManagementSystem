<template>
  <div class="classroom-query-page">
    <!-- 查询条件 -->
    <el-card class="query-card">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="查询日期">
          <el-date-picker
              v-model="queryForm.date"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="时间段">
          <el-select v-model="queryForm.timeSlot" placeholder="请选择" style="width: 150px">
            <el-option label="第1-2节 (08:00-09:40)" value="1-2" />
            <el-option label="第3-4节 (10:00-11:40)" value="3-4" />
            <el-option label="第5-6节 (14:00-15:40)" value="5-6" />
            <el-option label="第7-8节 (16:00-17:40)" value="7-8" />
            <el-option label="第9-10节 (19:00-20:40)" value="9-10" />
          </el-select>
        </el-form-item>
        <el-form-item label="教学楼">
          <el-select v-model="queryForm.building" placeholder="请选择" clearable style="width: 120px">
            <el-option label="逸夫楼" value="逸夫楼" />
            <el-option label="主楼" value="主楼" />
            <el-option label="图书馆" value="图书馆" />
          </el-select>
        </el-form-item>
        <el-form-item label="容量要求">
          <el-input-number v-model="queryForm.minCapacity" :min="0" placeholder="最小容量" style="width: 120px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 查询结果 -->
    <el-card class="result-card">
      <template #header>
        <div class="result-header">
          <span>查询结果</span>
          <el-tag type="success">共找到 {{ classroomList.length }} 间空闲教室</el-tag>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="6" v-for="room in classroomList" :key="room.roomId">
          <el-card shadow="hover" class="room-card">
            <div class="room-info">
              <div class="room-name">{{ room.roomName }}</div>
              <div class="room-location">
                <el-icon><Location /></el-icon>
                {{ room.building }} {{ room.floor }}层
              </div>
              <div class="room-details">
                <el-tag size="small">容量: {{ room.capacity }}人</el-tag>
                <el-tag size="small" type="info">{{ room.area }}㎡</el-tag>
              </div>
              <div class="room-equipment">
                <el-icon><Monitor /></el-icon>
                {{ room.equipment || '基础设施' }}
              </div>
            </div>
            <div class="room-actions">
              <el-button type="primary" size="small" @click="handleApply(room)">
                申请借用
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="classroomList.length === 0 && hasQueried" description="未找到符合条件的空闲教室" />
    </el-card>

    <!-- 申请借用弹窗 -->
    <el-dialog v-model="applyDialogVisible" title="教室借用申请" width="500px">
      <el-form :model="applyForm" :rules="applyRules" ref="applyFormRef" label-width="100px">
        <el-form-item label="教室">
          <el-input :value="selectedRoom?.roomName" disabled />
        </el-form-item>
        <el-form-item label="借用日期">
          <el-input :value="queryForm.date" disabled />
        </el-form-item>
        <el-form-item label="借用时段">
          <el-input :value="getTimeSlotText(queryForm.timeSlot)" disabled />
        </el-form-item>
        <el-form-item label="申请人" prop="applicantName">
          <el-input v-model="applyForm.applicantName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="applyForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="借用用途" prop="purpose">
          <el-select v-model="applyForm.purpose" placeholder="请选择" style="width: 100%">
            <el-option label="课程教学" value="课程教学" />
            <el-option label="班级活动" value="班级活动" />
            <el-option label="答辩" value="答辩" />
            <el-option label="讲座" value="讲座" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="预计人数" prop="expectedAttendees">
          <el-input-number v-model="applyForm.expectedAttendees" :min="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitApply">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Location, Monitor } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getClassroomOccupancy, submitClassroomBorrow } from '@/api/classroom'

const userStore = useUserStore()

interface ClassroomInfo {
  roomId: string
  roomName: string
  building: string
  floor: number
  capacity: number
  area: number
  equipment: string
}

// 查询表单
const queryForm = reactive({
  date: '',
  timeSlot: '',
  building: '',
  minCapacity: 0
})

// 查询结果
const classroomList = ref<ClassroomInfo[]>([])
const hasQueried = ref(false)

// 申请弹窗
const applyDialogVisible = ref(false)
const selectedRoom = ref<ClassroomInfo | null>(null)
const applyFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// 申请表单
const applyForm = reactive({
  applicantName: '',
  phone: '',
  purpose: '',
  expectedAttendees: 1
})

// 申请表单规则
const applyRules: FormRules = {
  applicantName: [{ required: true, message: '请输入申请人姓名', trigger: 'blur' }],
  purpose: [{ required: true, message: '请选择借用用途', trigger: 'change' }],
  expectedAttendees: [{ required: true, message: '请输入预计人数', trigger: 'blur' }]
}

// 获取时间段文本
function getTimeSlotText(slot: string): string {
  const map: Record<string, string> = {
    '1-2': '第1-2节 (08:00-09:40)',
    '3-4': '第3-4节 (10:00-11:40)',
    '5-6': '第5-6节 (14:00-15:40)',
    '7-8': '第7-8节 (16:00-17:40)',
    '9-10': '第9-10节 (19:00-20:40)'
  }
  return map[slot] || slot
}

// 禁用过去的日期
function disabledDate(date: Date) {
  return date.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 查询
async function handleQuery() {
  if (!queryForm.date) {
    ElMessage.warning('请选择查询日期')
    return
  }
  if (!queryForm.timeSlot) {
    ElMessage.warning('请选择时间段')
    return
  }

  hasQueried.value = true

  try {
    const res = await getClassroomOccupancy({
      date: queryForm.date,
      timeSlot: queryForm.timeSlot,
      building: queryForm.building || undefined,
      minCapacity: queryForm.minCapacity || undefined
    })
    classroomList.value = (res.data || []).filter((room: any) => room.isAvailable !== false)
  } catch (error) {
    console.error('查询失败:', error)
    classroomList.value = []
  }
}

// 重置
function handleReset() {
  queryForm.date = ''
  queryForm.timeSlot = ''
  queryForm.building = ''
  queryForm.minCapacity = 0
  classroomList.value = []
  hasQueried.value = false
}

// 申请借用 - 打开弹窗而不是跳转
function handleApply(room: ClassroomInfo) {
  selectedRoom.value = room
  applyForm.applicantName = userStore.realName || ''
  applyForm.phone = ''
  applyForm.purpose = ''
  applyForm.expectedAttendees = 1
  applyDialogVisible.value = true
}

// 提交申请
async function submitApply() {
  if (!applyFormRef.value) return
  
  const valid = await applyFormRef.value.validate()
  if (!valid) return

  if (!selectedRoom.value) return

  submitLoading.value = true
  try {
    // 根据时间段计算开始和结束时间
    const timeMap: Record<string, { start: string; end: string }> = {
      '1-2': { start: '08:00', end: '09:40' },
      '3-4': { start: '10:00', end: '11:40' },
      '5-6': { start: '14:00', end: '15:40' },
      '7-8': { start: '16:00', end: '17:40' },
      '9-10': { start: '19:00', end: '20:40' }
    }
    const times = timeMap[queryForm.timeSlot] || { start: '08:00', end: '10:00' }

    await submitClassroomBorrow({
      roomId: selectedRoom.value.roomId,
      roomName: selectedRoom.value.roomName,
      applicantDept: '',
      applicantName: applyForm.applicantName,
      purpose: applyForm.purpose,
      borrowDate: queryForm.date,
      startTime: times.start,
      endTime: times.end,
      expectedAttendees: applyForm.expectedAttendees,
      equipmentNeeds: ''
    })
    ElMessage.success('申请已提交，请等待审批')
    applyDialogVisible.value = false
  } catch (error: any) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped>
.classroom-query-page {
  height: 100%;
}

.query-card {
  margin-bottom: 20px;
}

.result-card {
  min-height: 400px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-card {
  margin-bottom: 20px;
}

.room-info {
  margin-bottom: 15px;
}

.room-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.room-location {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.room-details {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.room-equipment {
  color: #606266;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.room-actions {
  text-align: center;
}
</style>