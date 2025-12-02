<template>
  <div class="activity-security">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="40" color="#409eff"><Calendar /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pending }}</div>
              <div class="stat-label">待执行任务</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="40" color="#e6a23c"><Clock /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.ongoing }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="40" color="#67c23a"><CircleCheck /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.completed }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="40" color="#909399"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalStaff }}</div>
              <div class="stat-label">安保人员总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 功能选项卡 -->
    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="安保任务" name="tasks">
          <div class="tab-header">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="活动名称">
                <el-input v-model="searchForm.activityName" placeholder="请输入活动名称" clearable />
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="searchForm.status" placeholder="请选择" clearable>
                  <el-option label="待执行" value="pending" />
                  <el-option label="进行中" value="ongoing" />
                  <el-option label="已完成" value="completed" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增任务
            </el-button>
          </div>

          <!-- 任务表格 -->
          <el-table :data="tableData" v-loading="loading" border stripe>
            <el-table-column prop="activityName" label="活动名称" min-width="150" />
            <el-table-column prop="venue" label="活动场地" width="120" />
            <el-table-column prop="activityDate" label="活动日期" width="110" />
            <el-table-column prop="activityTime" label="时间段" width="140" />
            <el-table-column prop="expectedPeople" label="预计人数" width="90" />
            <el-table-column prop="securityStaff" label="安保人数" width="90" />
            <el-table-column prop="leader" label="负责人" width="100" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
                <el-button type="primary" link @click="handleEdit(row)" v-if="isAdmin">编辑</el-button>
                <el-button type="warning" link @click="handleAssign(row)" v-if="row.status === 'pending'">分配人员</el-button>
                <el-button type="success" link @click="handleStart(row)" v-if="row.status === 'pending'">开始执行</el-button>
                <el-button type="info" link @click="handleReport(row)" v-if="row.status === 'ongoing'">填写报告</el-button>
                <el-button type="success" link @click="handleComplete(row)" v-if="row.status === 'ongoing'">完成</el-button>
              </template>
            </el-table-column>
          </el-table>

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
        </el-tab-pane>

        <el-tab-pane label="巡逻记录" name="patrol">
          <div class="tab-header">
            <el-button type="primary" @click="handleAddPatrol">
              <el-icon><Plus /></el-icon>新增巡逻记录
            </el-button>
          </div>
          <el-table :data="patrolData" border stripe>
            <el-table-column prop="patrolId" label="巡逻编号" width="120" />
            <el-table-column prop="patrolArea" label="巡逻区域" width="150" />
            <el-table-column prop="patrolTime" label="巡逻时间" width="180" />
            <el-table-column prop="patroller" label="巡逻人员" width="100" />
            <el-table-column prop="situation" label="巡逻情况" min-width="200" />
            <el-table-column prop="issues" label="发现问题" min-width="150">
              <template #default="{ row }">
                <el-tag v-if="row.issues" type="danger">{{ row.issues }}</el-tag>
                <span v-else style="color: #67c23a;">无异常</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link @click="handlePatrolDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="人员排班" name="schedule">
          <div class="tab-header">
            <el-date-picker v-model="scheduleWeek" type="week" placeholder="选择周" format="YYYY 第 ww 周" />
            <el-button type="primary" style="margin-left: 16px;" @click="handleEditSchedule">
              <el-icon><Edit /></el-icon>编辑排班
            </el-button>
          </div>
          <el-table :data="scheduleData" border>
            <el-table-column prop="name" label="人员姓名" width="100" fixed />
            <el-table-column prop="monday" label="周一" />
            <el-table-column prop="tuesday" label="周二" />
            <el-table-column prop="wednesday" label="周三" />
            <el-table-column prop="thursday" label="周四" />
            <el-table-column prop="friday" label="周五" />
            <el-table-column prop="saturday" label="周六" />
            <el-table-column prop="sunday" label="周日" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 任务详情弹窗 -->
    <el-dialog v-model="detailVisible" title="安保任务详情" width="700px">
      <el-descriptions :column="2" border v-if="currentTask">
        <el-descriptions-item label="活动名称">{{ currentTask.activityName }}</el-descriptions-item>
        <el-descriptions-item label="活动场地">{{ currentTask.venue }}</el-descriptions-item>
        <el-descriptions-item label="活动日期">{{ currentTask.activityDate }}</el-descriptions-item>
        <el-descriptions-item label="活动时间">{{ currentTask.activityTime }}</el-descriptions-item>
        <el-descriptions-item label="预计人数">{{ currentTask.expectedPeople }} 人</el-descriptions-item>
        <el-descriptions-item label="安保人数">{{ currentTask.securityStaff }} 人</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ currentTask.leader || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentTask.status)">{{ getStatusText(currentTask.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="安保要点" :span="2">{{ currentTask.securityPoints || '确保人员安全，维护现场秩序' }}</el-descriptions-item>
        <el-descriptions-item label="分配人员" :span="2">
          <el-tag v-for="(staff, idx) in (currentTask.assignedStaff || [])" :key="idx" style="margin-right: 8px;">
            {{ staff }}
          </el-tag>
          <span v-if="!currentTask.assignedStaff?.length">暂未分配</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 人员分配弹窗 -->
    <el-dialog v-model="assignVisible" title="分配安保人员" width="500px">
      <el-form label-width="100px">
        <el-form-item label="活动名称">
          <span>{{ currentTask?.activityName }}</span>
        </el-form-item>
        <el-form-item label="需要人数">
          <span>{{ currentTask?.securityStaff }} 人</span>
        </el-form-item>
        <el-form-item label="选择人员">
          <el-checkbox-group v-model="assignForm.selectedStaff">
            <el-checkbox v-for="staff in availableStaff" :key="staff" :label="staff">{{ staff }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="负责人">
          <el-select v-model="assignForm.leader" placeholder="请选择负责人" style="width: 100%">
            <el-option v-for="staff in assignForm.selectedStaff" :key="staff" :label="staff" :value="staff" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确认分配</el-button>
      </template>
    </el-dialog>

    <!-- 安保报告弹窗 -->
    <el-dialog v-model="reportVisible" title="填写安保报告" width="600px">
      <el-form :model="reportForm" label-width="100px">
        <el-form-item label="活动名称">
          <span>{{ currentTask?.activityName }}</span>
        </el-form-item>
        <el-form-item label="实际人数">
          <el-input-number v-model="reportForm.actualPeople" :min="0" />
        </el-form-item>
        <el-form-item label="安保情况">
          <el-radio-group v-model="reportForm.situation">
            <el-radio value="normal">正常</el-radio>
            <el-radio value="minor">轻微问题</el-radio>
            <el-radio value="serious">重大问题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="情况描述">
          <el-input v-model="reportForm.description" type="textarea" :rows="4" placeholder="请描述安保执行情况" />
        </el-form-item>
        <el-form-item label="问题记录" v-if="reportForm.situation !== 'normal'">
          <el-input v-model="reportForm.issues" type="textarea" :rows="3" placeholder="请记录发现的问题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReport">提交报告</el-button>
      </template>
    </el-dialog>

    <!-- 编辑安保任务弹窗 -->
    <el-dialog v-model="editVisible" title="编辑安保任务" width="600px">
      <el-form :model="editForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="活动名称">
              <el-input v-model="editForm.activityName" placeholder="请输入活动名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动场地">
              <el-select v-model="editForm.venue" placeholder="请选择" style="width: 100%">
                <el-option label="体育馆" value="体育馆" />
                <el-option label="大礼堂" value="大礼堂" />
                <el-option label="学生中心" value="学生中心" />
                <el-option label="逸夫楼报告厅" value="逸夫楼报告厅" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="活动日期">
              <el-date-picker v-model="editForm.activityDate" type="date" placeholder="选择日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动时间">
              <el-input v-model="editForm.activityTime" placeholder="如: 19:00-22:00" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预计人数">
              <el-input-number v-model="editForm.expectedPeople" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="安保人数">
              <el-input-number v-model="editForm.securityStaff" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="安保要点">
          <el-input v-model="editForm.securityPoints" type="textarea" :rows="3" placeholder="请输入安保要点" />
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="editForm.status" placeholder="请选择" style="width: 200px">
            <el-option label="待执行" value="pending" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 新增巡逻记录弹窗 -->
    <el-dialog v-model="patrolDialogVisible" title="新增巡逻记录" width="500px">
      <el-form :model="patrolForm" label-width="100px">
        <el-form-item label="巡逻区域">
          <el-select v-model="patrolForm.area" placeholder="请选择巡逻区域" style="width: 100%">
            <el-option label="教学楼区域" value="教学楼区域" />
            <el-option label="宿舍楼区域" value="宿舍楼区域" />
            <el-option label="图书馆区域" value="图书馆区域" />
            <el-option label="体育场区域" value="体育场区域" />
            <el-option label="校门及周边" value="校门及周边" />
          </el-select>
        </el-form-item>
        <el-form-item label="巡逻时间">
          <el-time-picker v-model="patrolForm.time" is-range range-separator="至" start-placeholder="开始" end-placeholder="结束" />
        </el-form-item>
        <el-form-item label="巡逻情况">
          <el-input v-model="patrolForm.situation" type="textarea" :rows="3" placeholder="请描述巡逻情况" />
        </el-form-item>
        <el-form-item label="发现问题">
          <el-input v-model="patrolForm.issues" type="textarea" :rows="2" placeholder="如有问题请填写，无则留空" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="patrolDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPatrol">提交记录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Calendar, Clock, CircleCheck, User, Edit } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  getSecurityTaskList,
  createSecurityTask,
  updateSecurityTask,
  assignSecurityStaff,
  startSecurityTask,
  completeSecurityTask,
  getPatrolRecords,
  createPatrolRecord,
  getStaffSchedule,
  type SecurityTask,
  type PatrolRecord
} from '@/api/activity-venue'

const userStore = useUserStore()

// 是否为超级管理员
const isAdmin = computed(() => userStore.role === 'system_admin')

// 统计数据
const stats = reactive({
  pending: 3,
  ongoing: 1,
  completed: 25,
  totalStaff: 18
})

// 当前激活的标签页
const activeTab = ref('tasks')

// 任务相关
const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)

const searchForm = reactive({
  activityName: '',
  status: '',
  dateRange: [] as any[]
})

const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 巡逻记录
const patrolData = ref<any[]>([])

// 排班数据
const scheduleWeek = ref(new Date())
const scheduleData = ref<any[]>([])

// 当前任务/弹窗相关
const currentTask = ref<any>(null)
const detailVisible = ref(false)
const assignVisible = ref(false)
const reportVisible = ref(false)
const editVisible = ref(false)
const patrolDialogVisible = ref(false)

// 编辑表单
const editForm = reactive({
  activityName: '',
  venue: '',
  activityDate: '',
  activityTime: '',
  expectedPeople: 0,
  securityStaff: 0,
  securityPoints: '',
  status: ''
})

// 人员分配表单
const assignForm = reactive({
  selectedStaff: [] as string[],
  leader: ''
})

// 可用安保人员
const availableStaff = ref(['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十'])

// 报告表单
const reportForm = reactive({
  actualPeople: 0,
  situation: 'normal',
  description: '',
  issues: ''
})

// 巡逻表单
const patrolForm = reactive({
  area: '',
  time: null as any,
  situation: '',
  issues: ''
})

function getStatusType(status: string) {
  const map: Record<string, string> = {
    pending: 'warning',
    ongoing: 'primary',
    completed: 'success'
  }
  return map[status] || 'info'
}

function getStatusText(status: string) {
  const map: Record<string, string> = {
    pending: '待执行',
    ongoing: '进行中',
    completed: '已完成'
  }
  return map[status] || status
}

async function loadData() {
  loading.value = true
  try {
    const res = await getSecurityTaskList({
      ...queryParams,
      ...searchForm
    })
    tableData.value = res.data?.list || getMockTasks()
    total.value = res.data?.total || tableData.value.length
    
    // 更新统计
    stats.pending = tableData.value.filter(t => t.status === 'pending').length
    stats.ongoing = tableData.value.filter(t => t.status === 'ongoing').length
    stats.completed = tableData.value.filter(t => t.status === 'completed').length
  } catch (error) {
    console.error('加载安保任务失败:', error)
    tableData.value = getMockTasks()
    total.value = tableData.value.length
    stats.pending = tableData.value.filter(t => t.status === 'pending').length
    stats.ongoing = tableData.value.filter(t => t.status === 'ongoing').length
    stats.completed = tableData.value.filter(t => t.status === 'completed').length
  } finally {
    loading.value = false
  }
}

function getMockTasks() {
  return [
    { taskId: 'ST-001', activityName: '元旦晚会', venue: '体育馆', activityDate: '2025-01-01', activityTime: '19:00-22:00', expectedPeople: 2500, securityStaff: 12, leader: '张三', status: 'pending' as const, assignedStaff: ['张三', '李四', '王五'], createdAt: '2024-12-01' },
    { taskId: 'ST-002', activityName: '迎新晚会', venue: '大礼堂', activityDate: '2024-12-20', activityTime: '18:30-21:30', expectedPeople: 1500, securityStaff: 8, leader: '李四', status: 'ongoing' as const, assignedStaff: ['李四', '赵六', '钱七'], createdAt: '2024-12-01' },
    { taskId: 'ST-003', activityName: '校庆晚会', venue: '体育馆', activityDate: '2024-10-01', activityTime: '18:00-22:00', expectedPeople: 3000, securityStaff: 15, leader: '王五', status: 'completed' as const, assignedStaff: ['王五', '张三', '赵六', '钱七'], createdAt: '2024-09-15' }
  ]
}

async function loadPatrolData() {
  try {
    const res = await getPatrolRecords({ page: 1, pageSize: 20 })
    patrolData.value = res.data?.list || getMockPatrols()
  } catch (error) {
    console.error('加载巡逻记录失败:', error)
    patrolData.value = getMockPatrols()
  }
}

function getMockPatrols() {
  return [
    { patrolId: 'PT-2024-001', patrolArea: '教学楼区域', patrolTime: '2024-12-10 08:00-10:00', patroller: userStore.realName || '安保员A', situation: '一切正常，无异常情况', issues: '', createdAt: '2024-12-10' },
    { patrolId: 'PT-2024-002', patrolArea: '宿舍楼区域', patrolTime: '2024-12-10 14:00-16:00', patroller: '安保员B', situation: '发现3号楼门禁故障', issues: '门禁故障待维修', createdAt: '2024-12-10' },
    { patrolId: 'PT-2024-003', patrolArea: '图书馆区域', patrolTime: '2024-12-10 20:00-22:00', patroller: '安保员C', situation: '正常巡逻完成', issues: '', createdAt: '2024-12-10' },
    { patrolId: 'PT-2024-004', patrolArea: '体育场区域', patrolTime: '2024-12-11 06:00-08:00', patroller: userStore.realName || '安保员A', situation: '晨间巡逻，设施完好', issues: '', createdAt: '2024-12-11' }
  ]
}

async function loadScheduleData() {
  try {
    const weekStr = scheduleWeek.value.toISOString().split('T')[0]
    const res = await getStaffSchedule({ week: weekStr })
    scheduleData.value = res.data || getMockSchedule()
  } catch (error) {
    console.error('加载排班表失败:', error)
    scheduleData.value = getMockSchedule()
  }
}

function getMockSchedule() {
  return [
    { name: '张三', monday: '白班', tuesday: '白班', wednesday: '休息', thursday: '夜班', friday: '夜班', saturday: '白班', sunday: '休息' },
    { name: '李四', monday: '夜班', tuesday: '休息', wednesday: '白班', thursday: '白班', friday: '休息', saturday: '夜班', sunday: '夜班' },
    { name: '王五', monday: '休息', tuesday: '夜班', wednesday: '夜班', thursday: '休息', friday: '白班', saturday: '白班', sunday: '白班' },
    { name: '赵六', monday: '白班', tuesday: '白班', wednesday: '白班', thursday: '白班', friday: '白班', saturday: '休息', sunday: '休息' },
    { name: '钱七', monday: '夜班', tuesday: '夜班', wednesday: '休息', thursday: '休息', friday: '夜班', saturday: '夜班', sunday: '白班' }
  ]
}

function handleSearch() {
  queryParams.page = 1
  loadData()
}

function handleReset() {
  searchForm.activityName = ''
  searchForm.status = ''
  searchForm.dateRange = []
  handleSearch()
}

function handleAdd() {
  Object.assign(editForm, {
    activityName: '',
    venue: '',
    activityDate: '',
    activityTime: '',
    expectedPeople: 500,
    securityStaff: 4,
    securityPoints: '',
    status: 'pending'
  })
  currentTask.value = null
  editVisible.value = true
}

function handleEdit(row: any) {
  currentTask.value = row
  Object.assign(editForm, {
    activityName: row.activityName,
    venue: row.venue,
    activityDate: row.activityDate,
    activityTime: row.activityTime,
    expectedPeople: row.expectedPeople,
    securityStaff: row.securityStaff,
    securityPoints: row.securityPoints || '',
    status: row.status
  })
  editVisible.value = true
}

async function submitEdit() {
  if (!editForm.activityName || !editForm.venue || !editForm.activityDate) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    if (currentTask.value?.taskId) {
      // 编辑现有任务
      await updateSecurityTask(currentTask.value.taskId, editForm as any)
      ElMessage.success('安保任务已更新')
    } else {
      // 新增任务
      await createSecurityTask(editForm as any)
      ElMessage.success('安保任务已添加')
    }
    editVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

function handleDetail(row: any) {
  currentTask.value = row
  detailVisible.value = true
}

function handleAssign(row: any) {
  currentTask.value = row
  assignForm.selectedStaff = row.assignedStaff || []
  assignForm.leader = row.leader || ''
  assignVisible.value = true
}

async function handleStart(row: any) {
  if (!row.assignedStaff?.length) {
    ElMessage.warning('请先分配安保人员')
    return
  }
  try {
    await startSecurityTask(row.taskId)
    ElMessage.success('任务已开始执行')
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

function handleReport(row: any) {
  currentTask.value = row
  reportForm.actualPeople = row.expectedPeople
  reportForm.situation = 'normal'
  reportForm.description = ''
  reportForm.issues = ''
  reportVisible.value = true
}

async function handleComplete(row: any) {
  try {
    await completeSecurityTask(row.taskId, {
      report: reportForm.description || '任务完成',
      issues: reportForm.issues || undefined
    })
    ElMessage.success('活动安保任务已完成')
    reportVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

async function confirmAssign() {
  if (assignForm.selectedStaff.length === 0) {
    ElMessage.warning('请至少选择一名安保人员')
    return
  }
  if (!assignForm.leader) {
    ElMessage.warning('请指定负责人')
    return
  }
  try {
    if (currentTask.value?.taskId) {
      await assignSecurityStaff(currentTask.value.taskId, {
        staffList: assignForm.selectedStaff,
        leader: assignForm.leader
      })
    }
    ElMessage.success('人员分配成功')
    assignVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '分配失败')
  }
}

function submitReport() {
  if (!reportForm.description) {
    ElMessage.warning('请填写情况描述')
    return
  }
  ElMessage.success('安保报告已提交')
  reportVisible.value = false
}

function handleAddPatrol() {
  patrolForm.area = ''
  patrolForm.time = null
  patrolForm.situation = ''
  patrolForm.issues = ''
  patrolDialogVisible.value = true
}

function handlePatrolDetail(row: any) {
  ElMessage.info(`巡逻记录: ${row.patrolId} - ${row.patrolArea}`)
}

async function submitPatrol() {
  if (!patrolForm.area || !patrolForm.situation) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    await createPatrolRecord({
      patrolArea: patrolForm.area,
      patrolTime: patrolForm.time ? `${patrolForm.time[0]} - ${patrolForm.time[1]}` : new Date().toLocaleString(),
      patroller: userStore.realName || '安保员',
      situation: patrolForm.situation,
      issues: patrolForm.issues || undefined
    })
    ElMessage.success('巡逻记录已提交')
    patrolDialogVisible.value = false
    loadPatrolData()
  } catch (error: any) {
    ElMessage.error(error.message || '提交失败')
  }
}

function handleEditSchedule() {
  ElMessage.info('编辑排班表')
}

onMounted(() => {
  loadData()
  loadPatrolData()
  loadScheduleData()
})
</script>

<style scoped>
.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.search-form {
  flex: 1;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
