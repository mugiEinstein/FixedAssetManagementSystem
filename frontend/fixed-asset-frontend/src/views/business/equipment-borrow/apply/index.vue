<template>
  <div class="borrow-apply">
    <el-card shadow="never">
      <template #header>
        <span>器材借用申请</span>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="借用器材" prop="equipmentId">
              <el-select v-model="form.equipmentId" placeholder="请选择器材" filterable style="width: 100%">
                <el-option v-for="item in equipments" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="借用数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="借用开始" prop="startDate">
              <el-date-picker v-model="form.startDate" type="datetime" placeholder="选择开始时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归还时间" prop="endDate">
              <el-date-picker v-model="form.endDate" type="datetime" placeholder="选择归还时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="借用用途" prop="purpose">
          <el-input v-model="form.purpose" type="textarea" :rows="3" placeholder="请详细描述借用用途" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交申请</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 我的申请记录 -->
    <el-card shadow="never" class="mt-20">
      <template #header>
        <span>我的申请记录</span>
      </template>

      <el-table :data="records" border stripe>
        <el-table-column prop="equipmentName" label="器材名称" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="startDate" label="借用时间" width="160" />
        <el-table-column prop="endDate" label="归还时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getIdleEquipments } from '@/api/equipment'
import { submitInternalBorrow, getMyBorrowList } from '@/api/borrow'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const formRef = ref()
const equipments = ref<any[]>([])
const records = ref<any[]>([])
const submitLoading = ref(false)

const form = reactive({
  equipmentId: '',
  quantity: 1,
  startDate: '',
  endDate: '',
  purpose: '',
  phone: ''
})

const rules = {
  equipmentId: [{ required: true, message: '请选择器材', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择归还时间', trigger: 'change' }],
  purpose: [{ required: true, message: '请输入借用用途', trigger: 'blur' }]
}

function getStatusType(status: string) {
  const map: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    returned: 'info',
    in_use: 'primary'
  }
  return map[status] || 'info'
}

function getStatusText(status: string) {
  const map: Record<string, string> = {
    pending: '待审批',
    approved: '已批准',
    rejected: '已拒绝',
    returned: '已归还',
    in_use: '使用中'
  }
  return map[status] || status
}

async function handleSubmit() {
  const valid = await formRef.value?.validate()
  if (!valid) return

  submitLoading.value = true
  try {
    await submitInternalBorrow({
      equipmentId: form.equipmentId,
      userId: userStore.userId,
      applicantName: userStore.realName,
      userType: userStore.role,
      borrowDate: form.startDate,
      returnDate: form.endDate,
      purpose: form.purpose
    })
    ElMessage.success('申请提交成功，等待审批')
    handleReset()
    loadRecords()
  } catch (error: any) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitLoading.value = false
  }
}

function handleReset() {
  formRef.value?.resetFields()
}

// 加载可借用器材
async function loadEquipments() {
  try {
    const res = await getIdleEquipments()
    equipments.value = (res.data || []).map((item: any) => ({
      id: item.equipmentId,
      name: `${item.equipmentName} - ${item.model || item.location || ''}`
    }))
  } catch (error) {
    console.error('加载器材失败:', error)
    equipments.value = []
  }
}

// 加载我的申请记录
async function loadRecords() {
  try {
    const res = await getMyBorrowList({
      page: 1,
      pageSize: 10,
      applicantId: userStore.userId
    })
    records.value = (res.data?.list || []).map((item: any) => ({
      equipmentName: item.equipmentName,
      quantity: 1,
      startDate: item.borrowDate,
      endDate: item.returnDate,
      status: item.status,
      applyTime: item.createdAt
    }))
  } catch (error) {
    console.error('加载记录失败:', error)
    records.value = []
  }
}

onMounted(() => {
  loadEquipments()
  loadRecords()
})
</script>

<style scoped>
.mt-20 {
  margin-top: 20px;
}
</style>
