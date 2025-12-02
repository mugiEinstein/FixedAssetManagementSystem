<template>
  <div class="my-housing-page">
    <el-row :gutter="20">
      <!-- 住宿信息卡片 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>{{ isStudent ? '我的宿舍信息' : '我的住房信息' }}</span>
              <el-tag v-if="housingInfo || facultyHousingInfo" 
                      :type="(housingInfo?.status || facultyHousingInfo?.status) === 'active' ? 'success' : 'info'">
                {{ (housingInfo?.status || facultyHousingInfo?.status) === 'active' ? '在住' : '已退宿' }}
              </el-tag>
            </div>
          </template>

          <!-- 学生宿舍信息 -->
          <el-descriptions :column="2" border v-if="housingInfo && isStudent">
            <el-descriptions-item label="学号">{{ housingInfo.studentId }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ housingInfo.studentName }}</el-descriptions-item>
            <el-descriptions-item label="学院">{{ housingInfo.college }}</el-descriptions-item>
            <el-descriptions-item label="专业">{{ housingInfo.major }}</el-descriptions-item>
            <el-descriptions-item label="宿舍楼">{{ housingInfo.buildingName }}</el-descriptions-item>
            <el-descriptions-item label="房间号">{{ housingInfo.dormitoryCode }}</el-descriptions-item>
            <el-descriptions-item label="床位号">{{ housingInfo.bedNumber }} 号床</el-descriptions-item>
            <el-descriptions-item label="入住日期">{{ housingInfo.checkInDate }}</el-descriptions-item>
          </el-descriptions>

          <!-- 教职工住房信息 -->
          <el-descriptions :column="2" border v-else-if="facultyHousingInfo && !isStudent">
            <el-descriptions-item label="工号">{{ facultyHousingInfo.userId }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ facultyHousingInfo.staffName }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{ facultyHousingInfo.department }}</el-descriptions-item>
            <el-descriptions-item label="房间ID">{{ facultyHousingInfo.dormId }}</el-descriptions-item>
            <el-descriptions-item label="面积">约 {{ facultyHousingInfo.area }} ㎡</el-descriptions-item>
            <el-descriptions-item label="租金">¥{{ facultyHousingInfo.rentAmount }}/月</el-descriptions-item>
            <el-descriptions-item label="合同开始">{{ facultyHousingInfo.contractStart }}</el-descriptions-item>
            <el-descriptions-item label="合同结束">{{ facultyHousingInfo.contractEnd }}</el-descriptions-item>
          </el-descriptions>

          <el-empty v-else description="暂无住宿信息" />
        </el-card>
      </el-col>

      <!-- 室友信息 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>室友信息</span>
          </template>

          <div class="roommate-list">
            <div class="roommate-item" v-for="mate in roommates" :key="mate.bedNumber">
              <el-avatar :size="40" :icon="UserFilled" />
              <div class="roommate-info">
                <div class="name">{{ mate.name }}</div>
                <div class="detail">{{ mate.major }} · {{ mate.bedNumber }}号床</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <el-card style="margin-top: 20px;" v-if="housingInfo || facultyHousingInfo">
      <template #header>
        <span>住宿服务</span>
      </template>

      <el-row :gutter="20">
        <el-col :span="6">
          <div class="service-item" @click="handleTransferApply">
            <el-icon :size="40" color="#409eff"><Switch /></el-icon>
            <span>申请调换宿舍</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="service-item" @click="handleRepairApply">
            <el-icon :size="40" color="#e6a23c"><Tools /></el-icon>
            <span>报修申请</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="service-item" @click="handleLeaveApply">
            <el-icon :size="40" color="#67c23a"><Document /></el-icon>
            <span>请假外宿</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="service-item" @click="handleExitApply">
            <el-icon :size="40" color="#f56c6c"><CircleClose /></el-icon>
            <span>退宿申请</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 调换申请弹窗 -->
    <el-dialog v-model="transferVisible" title="申请调换宿舍" width="500px">
      <el-form :model="transferForm" label-width="100px">
        <el-form-item label="调换原因">
          <el-select v-model="transferForm.reasonType" placeholder="请选择原因" style="width: 100%">
            <el-option label="与室友作息不合" value="schedule" />
            <el-option label="身体健康原因" value="health" />
            <el-option label="专业调整" value="major" />
            <el-option label="其他原因" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明">
          <el-input v-model="transferForm.reason" type="textarea" :rows="4" placeholder="请详细说明调换原因" />
        </el-form-item>
        <el-form-item label="期望宿舍">
          <el-input v-model="transferForm.preferredDorm" placeholder="如有特定期望可填写（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTransfer">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 报修申请弹窗 -->
    <el-dialog v-model="repairVisible" title="宿舍报修申请" width="550px">
      <el-form :model="repairForm" :rules="repairRules" ref="repairFormRef" label-width="100px">
        <el-form-item label="报修类型" prop="repairType">
          <el-select v-model="repairForm.repairType" placeholder="请选择报修类型" style="width: 100%">
            <el-option label="水电问题" value="water_electric" />
            <el-option label="门窗损坏" value="door_window" />
            <el-option label="家具损坏" value="furniture" />
            <el-option label="网络问题" value="network" />
            <el-option label="空调/暖气" value="hvac" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修位置" prop="location">
          <el-input v-model="repairForm.location" placeholder="如：101室卫生间" />
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input v-model="repairForm.description" type="textarea" :rows="4" placeholder="请详细描述问题" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="repairForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="方便时间">
          <el-select v-model="repairForm.availableTime" placeholder="请选择方便维修的时间" style="width: 100%">
            <el-option label="工作日白天（8:00-17:00）" value="weekday_day" />
            <el-option label="工作日晚间（17:00-21:00）" value="weekday_night" />
            <el-option label="周末全天" value="weekend" />
            <el-option label="随时可以" value="anytime" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="repairVisible = false">取消</el-button>
        <el-button type="primary" :loading="repairLoading" @click="submitRepair">提交报修</el-button>
      </template>
    </el-dialog>

    <!-- 请假外宿弹窗 -->
    <el-dialog v-model="leaveVisible" title="请假外宿申请" width="550px">
      <el-form :model="leaveForm" :rules="leaveRules" ref="leaveFormRef" label-width="100px">
        <el-form-item label="外宿类型" prop="leaveType">
          <el-radio-group v-model="leaveForm.leaveType">
            <el-radio value="temporary">临时外宿</el-radio>
            <el-radio value="longterm">长期外宿</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="leaveForm.startDate" type="date" placeholder="选择开始日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="leaveForm.endDate" type="date" placeholder="选择结束日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="外宿原因" prop="reason">
          <el-select v-model="leaveForm.reason" placeholder="请选择原因" style="width: 100%">
            <el-option label="回家" value="home" />
            <el-option label="实习" value="internship" />
            <el-option label="比赛/活动" value="activity" />
            <el-option label="就医" value="medical" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明" prop="description">
          <el-input v-model="leaveForm.description" type="textarea" :rows="3" placeholder="请说明外宿详情" />
        </el-form-item>
        <el-form-item label="外宿地址" prop="address">
          <el-input v-model="leaveForm.address" placeholder="请输入外宿地址" />
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="leaveForm.emergencyContact" placeholder="姓名 + 电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="leaveVisible = false">取消</el-button>
        <el-button type="primary" :loading="leaveLoading" @click="submitLeave">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 退宿申请弹窗 -->
    <el-dialog v-model="exitVisible" title="退宿申请" width="550px">
      <el-alert type="warning" :closable="false" style="margin-bottom: 16px;">
        <template #title>
          <span>退宿须知：退宿后您将失去宿舍使用权，请确认已清空个人物品并办理相关手续。</span>
        </template>
      </el-alert>
      <el-form :model="exitForm" :rules="exitRules" ref="exitFormRef" label-width="100px">
        <el-form-item label="退宿原因" prop="reason">
          <el-select v-model="exitForm.reason" placeholder="请选择退宿原因" style="width: 100%">
            <el-option label="毕业离校" value="graduation" />
            <el-option label="休学" value="suspension" />
            <el-option label="退学" value="withdrawal" />
            <el-option label="校外租房" value="rent_outside" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="预计退宿日期" prop="exitDate">
          <el-date-picker v-model="exitForm.exitDate" type="date" placeholder="选择退宿日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="补充说明">
          <el-input v-model="exitForm.description" type="textarea" :rows="3" placeholder="如有其他需要说明的情况请填写" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="exitForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="exitForm.confirmed">我已了解退宿相关规定，并确认申请退宿</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="exitVisible = false">取消</el-button>
        <el-button type="danger" :loading="exitLoading" :disabled="!exitForm.confirmed" @click="submitExit">确认退宿</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { UserFilled, Switch, Tools, Document, CircleClose } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { submitMaintenanceRequest } from '@/api/maintenance'
import { getMyHousingInfo, getRoommateInfo, applyDormTransfer, applyLeave, applyCheckout } from '@/api/housing'

const userStore = useUserStore()

// 判断是否为学生
const isStudent = computed(() => userStore.role === 'student')

// 学生宿舍信息
const housingInfo = ref<any>(null)

// 教职工住房信息
const facultyHousingInfo = ref<any>(null)

// 室友信息（仅学生）
const roommates = ref<any[]>([])

// 调换弹窗
const transferVisible = ref(false)
const transferLoading = ref(false)
const transferForm = reactive({
  reasonType: '',
  reason: '',
  preferredDorm: ''
})

// 报修弹窗
const repairVisible = ref(false)
const repairLoading = ref(false)
const repairFormRef = ref<FormInstance>()
const repairForm = reactive({
  repairType: '',
  location: '',
  description: '',
  phone: '',
  availableTime: 'anytime'
})
const repairRules: FormRules = {
  repairType: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  location: [{ required: true, message: '请输入报修位置', trigger: 'blur' }],
  description: [{ required: true, message: '请描述问题', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

// 请假外宿弹窗
const leaveVisible = ref(false)
const leaveLoading = ref(false)
const leaveFormRef = ref<FormInstance>()
const leaveForm = reactive({
  leaveType: 'temporary',
  startDate: '',
  endDate: '',
  reason: '',
  description: '',
  address: '',
  emergencyContact: ''
})
const leaveRules: FormRules = {
  leaveType: [{ required: true, message: '请选择外宿类型', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  reason: [{ required: true, message: '请选择外宿原因', trigger: 'change' }],
  address: [{ required: true, message: '请输入外宿地址', trigger: 'blur' }],
  emergencyContact: [{ required: true, message: '请输入紧急联系人', trigger: 'blur' }]
}

// 退宿弹窗
const exitVisible = ref(false)
const exitLoading = ref(false)
const exitFormRef = ref<FormInstance>()
const exitForm = reactive({
  reason: '',
  exitDate: '',
  description: '',
  phone: '',
  confirmed: false
})
const exitRules: FormRules = {
  reason: [{ required: true, message: '请选择退宿原因', trigger: 'change' }],
  exitDate: [{ required: true, message: '请选择退宿日期', trigger: 'change' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

// 加载住宿信息
function loadHousingInfo() {
  if (isStudent.value) {
    // 学生宿舍信息（学生看这个）
    housingInfo.value = {
      allocationId: 'SH-2024-001',
      studentId: userStore.userId || '2024001001',
      studentName: userStore.realName || '张三',
      gender: 'male',
      college: '计算机学院',
      major: '软件工程',
      grade: '2024级',
      dormId: 'D-1-101',
      buildingName: '学生公寓1斋',
      dormitoryCode: '101室',
      bedNumber: 1,
      checkInDate: '2024-09-01',
      status: 'active'
    }
    // 加载室友信息
    roommates.value = [
      { name: '李四', major: '软件工程', bedNumber: 2 },
      { name: '王五', major: '计算机科学', bedNumber: 3 },
      { name: '赵六', major: '人工智能', bedNumber: 4 }
    ]
  } else {
    // 教职工住房信息（教师看这个）
    facultyHousingInfo.value = {
      allocationId: 'FH-2024-001',
      userId: userStore.userId || 'TEA-001',
      staffName: userStore.realName || '王教授',
      department: '计算机学院',
      dormId: 'FD-A-301',
      area: 45,
      rentAmount: 800,
      contractStart: '2024-01-01',
      contractEnd: '2025-12-31',
      status: 'active'
    }
  }
}

// 申请调换
function handleTransferApply() {
  transferForm.reasonType = ''
  transferForm.reason = ''
  transferForm.preferredDorm = ''
  transferVisible.value = true
}

// 提交调换申请
async function submitTransfer() {
  if (!transferForm.reasonType || !transferForm.reason) {
    ElMessage.warning('请填写完整信息')
    return
  }
  transferLoading.value = true
  try {
    await applyDormTransfer({
      reasonType: transferForm.reasonType,
      reason: transferForm.reason,
      preferredDorm: transferForm.preferredDorm || undefined
    })
    ElMessage.success('调换申请已提交，请等待审核')
    transferVisible.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    transferLoading.value = false
  }
}

// 报修申请
function handleRepairApply() {
  Object.assign(repairForm, {
    repairType: '',
    location: housingInfo.value?.dormitoryCode || facultyHousingInfo.value?.dormId || '',
    description: '',
    phone: '',
    availableTime: 'anytime'
  })
  repairVisible.value = true
}

// 提交报修
async function submitRepair() {
  if (!repairFormRef.value) return
  await repairFormRef.value.validate(async (valid) => {
    if (!valid) return
    repairLoading.value = true
    try {
      await submitMaintenanceRequest({
        assetType: 'building',
        assetId: housingInfo.value?.dormId || facultyHousingInfo.value?.dormId || 'DORM',
        assetName: `${housingInfo.value?.buildingName || '宿舍'} ${repairForm.location}`,
        faultDescription: `[${getRepairTypeText(repairForm.repairType)}] ${repairForm.description}`,
        reporterName: userStore.realName || '用户',
        reporterDept: housingInfo.value?.college || facultyHousingInfo.value?.department || '住宿管理'
      })
      ElMessage.success('报修申请已提交，维修人员会尽快联系您')
      repairVisible.value = false
    } catch (error: any) {
      ElMessage.error(error.message || '提交失败')
    } finally {
      repairLoading.value = false
    }
  })
}

function getRepairTypeText(type: string): string {
  const map: Record<string, string> = {
    water_electric: '水电问题',
    door_window: '门窗损坏',
    furniture: '家具损坏',
    network: '网络问题',
    hvac: '空调/暖气',
    other: '其他'
  }
  return map[type] || type
}

// 请假外宿
function handleLeaveApply() {
  Object.assign(leaveForm, {
    leaveType: 'temporary',
    startDate: '',
    endDate: '',
    reason: '',
    description: '',
    address: '',
    emergencyContact: ''
  })
  leaveVisible.value = true
}

// 提交请假外宿
async function submitLeave() {
  if (!leaveFormRef.value) return
  await leaveFormRef.value.validate(async (valid) => {
    if (!valid) return
    leaveLoading.value = true
    try {
      // 模拟提交（实际项目需要对接后端API）
      await new Promise(resolve => setTimeout(resolve, 500))
      ElMessage.success('外宿申请已提交，请等待辅导员审批')
      leaveVisible.value = false
    } catch (error: any) {
      ElMessage.error(error.message || '提交失败')
    } finally {
      leaveLoading.value = false
    }
  })
}

// 退宿申请
function handleExitApply() {
  Object.assign(exitForm, {
    reason: '',
    exitDate: '',
    description: '',
    phone: '',
    confirmed: false
  })
  exitVisible.value = true
}

// 提交退宿
async function submitExit() {
  if (!exitFormRef.value) return
  if (!exitForm.confirmed) {
    ElMessage.warning('请先确认已了解退宿规定')
    return
  }
  await exitFormRef.value.validate(async (valid) => {
    if (!valid) return
    exitLoading.value = true
    try {
      // 模拟提交（实际项目需要对接后端API）
      await new Promise(resolve => setTimeout(resolve, 500))
      ElMessage.success('退宿申请已提交，请按时办理退宿手续')
      exitVisible.value = false
    } catch (error: any) {
      ElMessage.error(error.message || '提交失败')
    } finally {
      exitLoading.value = false
    }
  })
}

onMounted(() => {
  loadHousingInfo()
})
</script>

<style scoped>
.my-housing-page {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.roommate-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.roommate-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
}

.roommate-info .name {
  font-weight: 500;
  margin-bottom: 4px;
}

.roommate-info .detail {
  font-size: 12px;
  color: #909399;
}

.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.service-item:hover {
  background: #ecf5ff;
  transform: translateY(-2px);
}

.service-item span {
  margin-top: 12px;
  font-size: 14px;
  color: #606266;
}
</style>