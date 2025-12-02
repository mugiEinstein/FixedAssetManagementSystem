<template>
  <div class="transfer-page">
    <!-- 我的调换申请 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的调换申请</span>
          <el-button type="primary" size="small" @click="handleNewApply">新建申请</el-button>
        </div>
      </template>

      <el-table :data="myApplications" border>
        <el-table-column prop="applyId" label="申请编号" width="130" />
        <el-table-column prop="currentDorm" label="当前宿舍" width="120" />
        <el-table-column prop="targetDorm" label="目标宿舍" width="120">
          <template #default="{ row }">
            {{ row.targetDorm || '待分配' }}
          </template>
        </el-table-column>
        <el-table-column prop="reasonType" label="调换原因" width="150" show-overflow-tooltip />
        <el-table-column prop="applyDate" label="申请日期" width="110" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <AssetStatusTag :status="row.status" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">详情</el-button>
            <el-button
                v-if="row.status === 'pending'"
                type="danger"
                link
                @click="handleCancel(row)"
            >
              撤销
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="myApplications.length === 0" description="暂无调换申请" />
    </el-card>

    <!-- 新建申请弹窗 -->
    <el-dialog v-model="dialogVisible" title="申请宿舍调换" width="550px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="当前宿舍">
          <el-input value="1斋-101室 1号床" disabled />
        </el-form-item>
        <el-form-item label="调换类型" prop="transferType">
          <el-radio-group v-model="form.transferType">
            <el-radio value="dorm">换宿舍</el-radio>
            <el-radio value="bed">换床位</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调换原因" prop="reasonType">
          <el-select v-model="form.reasonType" placeholder="请选择" style="width: 100%">
            <el-option label="与室友作息时间不合" value="与室友作息时间不合" />
            <el-option label="身体健康原因需要特殊照顾" value="身体健康原因需要特殊照顾" />
            <el-option label="专业调整需要就近住宿" value="专业调整需要就近住宿" />
            <el-option label="其他原因" value="其他原因" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明" prop="reason">
          <el-input
              v-model="form.reason"
              type="textarea"
              :rows="4"
              placeholder="请详细说明调换原因，以便审批"
          />
        </el-form-item>
        <el-form-item label="期望宿舍">
          <el-input v-model="form.preferredDorm" placeholder="如有特定期望可填写，如：2斋（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申请详情" width="550px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请编号">{{ currentRow?.applyId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <AssetStatusTag v-if="currentRow" :status="currentRow.status" />
        </el-descriptions-item>
        <el-descriptions-item label="当前宿舍">{{ currentRow?.currentDorm }}</el-descriptions-item>
        <el-descriptions-item label="目标宿舍">{{ currentRow?.targetDorm || '待分配' }}</el-descriptions-item>
        <el-descriptions-item label="调换原因" :span="2">{{ currentRow?.reasonType }}</el-descriptions-item>
        <el-descriptions-item label="详细说明" :span="2">{{ currentRow?.reason }}</el-descriptions-item>
        <el-descriptions-item label="申请日期">{{ currentRow?.applyDate }}</el-descriptions-item>
        <el-descriptions-item label="审批意见">{{ currentRow?.approveComment || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'

interface TransferApplication {
  applyId: string
  currentDorm: string
  targetDorm?: string
  reasonType: string
  reason: string
  applyDate: string
  status: string
  approveComment?: string
}

// 我的申请列表
const myApplications = ref<TransferApplication[]>([])

// 弹窗相关
const dialogVisible = ref(false)
const detailVisible = ref(false)
const currentRow = ref<TransferApplication | null>(null)
const formRef = ref<FormInstance>()

// 表单数据
const form = reactive({
  transferType: 'dorm',
  reasonType: '',
  reason: '',
  preferredDorm: ''
})

// 表单验证规则
const formRules: FormRules = {
  transferType: [{ required: true, message: '请选择调换类型', trigger: 'change' }],
  reasonType: [{ required: true, message: '请选择调换原因', trigger: 'change' }],
  reason: [{ required: true, message: '请填写详细说明', trigger: 'blur' }]
}

// 加载数据
function loadData() {
  myApplications.value = [
    {
      applyId: 'TR-2024-001',
      currentDorm: '1斋-101室',
      targetDorm: '2斋-205室',
      reasonType: '与室友作息时间不合',
      reason: '室友经常熬夜打游戏，影响休息',
      applyDate: '2024-11-20',
      status: 'approved',
      approveComment: '已协调安排'
    },
    {
      applyId: 'TR-2024-002',
      currentDorm: '1斋-101室',
      reasonType: '专业调整需要就近住宿',
      reason: '转专业后需要去新教学楼上课，希望换到距离较近的宿舍',
      applyDate: '2024-11-28',
      status: 'pending'
    }
  ]
}

// 新建申请
function handleNewApply() {
  Object.assign(form, {
    transferType: 'dorm',
    reasonType: '',
    reason: '',
    preferredDorm: ''
  })
  dialogVisible.value = true
}

// 查看详情
function handleViewDetail(row: TransferApplication) {
  currentRow.value = row
  detailVisible.value = true
}

// 撤销申请
async function handleCancel(row: TransferApplication) {
  try {
    await ElMessageBox.confirm('确定要撤销该调换申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    ElMessage.success('已撤销申请')
    loadData()
  } catch {
    // 取消操作
  }
}

// 提交申请
async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    ElMessage.success('调换申请已提交，请等待审核')
    dialogVisible.value = false
    loadData()
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.transfer-page {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>