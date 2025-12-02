<template>
  <div class="maintenance-apply-page">
    <el-card>
      <template #header>
        <span>提交维修申请</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="formRules" label-width="120px" style="max-width: 700px;">
        <el-form-item label="资产类型" prop="assetType">
          <el-radio-group v-model="form.assetType" @change="handleAssetTypeChange">
            <el-radio value="building">建筑设施</el-radio>
            <el-radio value="equipment">实验器材</el-radio>
            <el-radio value="vehicle">车辆</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="选择资产" prop="assetId">
          <el-select v-model="form.assetId" placeholder="请选择需要维修的资产" style="width: 100%">
            <el-option
                v-for="item in assetOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="故障描述" prop="faultDescription">
          <el-input
              v-model="form.faultDescription"
              type="textarea"
              :rows="5"
              placeholder="请详细描述故障现象，包括：&#10;1. 什么时候发现的问题&#10;2. 具体的故障表现&#10;3. 是否影响正常使用"
          />
        </el-form-item>

        <el-form-item label="紧急程度" prop="urgency">
          <el-radio-group v-model="form.urgency">
            <el-radio value="low">一般</el-radio>
            <el-radio value="medium">较急</el-radio>
            <el-radio value="high">紧急</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" style="width: 300px;" />
        </el-form-item>

        <el-form-item label="上传图片">
          <el-upload
              action="#"
              :auto-upload="false"
              list-type="picture-card"
              :limit="5"
              :file-list="imageFileList"
              :on-change="handleImageChange"
              :on-remove="handleImageRemove"
              :on-exceed="handleImageExceed"
              accept="image/jpeg,image/png,image/gif"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">最多上传5张图片，支持jpg/png/gif格式，单张不超过5MB</div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交申请</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 我的维修申请 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <span>我的维修申请</span>
      </template>

      <el-table :data="myRecords" border>
        <el-table-column prop="maintenanceId" label="维修单号" width="130" />
        <el-table-column prop="assetName" label="资产名称" min-width="150" />
        <el-table-column prop="faultDescription" label="故障描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="reportDate" label="申请日期" width="110" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <AssetStatusTag :status="row.status" />
          </template>
        </el-table-column>
        <el-table-column prop="repairResult" label="维修结果" min-width="150">
          <template #default="{ row }">
            {{ row.repairResult || '-' }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadFile, UploadFiles } from 'element-plus'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'
import { submitMaintenanceRequest, getMaintenanceList } from '@/api/maintenance'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

interface AssetOption {
  value: string
  label: string
}

// 表单数据
const form = reactive({
  assetType: 'equipment',
  assetId: '',
  assetName: '',
  faultDescription: '',
  urgency: 'medium',
  contactPhone: ''
})

// 图片上传相关
const imageFileList = ref<UploadFile[]>([])
const imageBase64List = ref<string[]>([])

// 表单验证规则
const formRules: FormRules = {
  assetType: [{ required: true, message: '请选择资产类型', trigger: 'change' }],
  assetId: [{ required: true, message: '请选择资产', trigger: 'change' }],
  faultDescription: [{ required: true, message: '请描述故障', trigger: 'blur' }],
  urgency: [{ required: true, message: '请选择紧急程度', trigger: 'change' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const recordsLoading = ref(false)

// 资产选项
const assetOptions = ref<AssetOption[]>([])

// 我的维修记录
const myRecords = ref<any[]>([])

// 加载我的维修记录 - 使用userId进行精确筛选
async function loadMyRecords() {
  recordsLoading.value = true
  try {
    const res = await getMaintenanceList({ 
      page: 1, 
      pageSize: 50,
      // 优先使用userId进行精确筛选，确保数据隔离
      reporterId: userStore.userId || userStore.userInfo?.id,
      reporterName: userStore.realName || userStore.userInfo?.username
    })
    myRecords.value = res.data?.list || []
  } catch (error) {
    console.error('加载维修记录失败:', error)
    myRecords.value = []
  } finally {
    recordsLoading.value = false
  }
}

// 资产类型变化
function handleAssetTypeChange(type: string) {
  form.assetId = ''
  form.assetName = ''
  if (type === 'building') {
    assetOptions.value = [
      { value: 'B-001', label: '逸夫楼101教室空调' },
      { value: 'B-002', label: '主楼3号电梯' },
      { value: 'B-003', label: '图书馆照明系统' }
    ]
  } else if (type === 'equipment') {
    assetOptions.value = [
      { value: 'E-001', label: '高精度光谱分析仪' },
      { value: 'E-002', label: '电子显微镜' },
      { value: 'E-003', label: '高性能服务器' }
    ]
  } else {
    assetOptions.value = [
      { value: 'V-001', label: '京A88888 奥迪A6L' },
      { value: 'V-002', label: '京B12345 宇通客车' }
    ]
  }
}

// 资产选择变化
function handleAssetChange(assetId: string) {
  const asset = assetOptions.value.find(a => a.value === assetId)
  if (asset) {
    form.assetName = asset.label
  }
}

// 提交
async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    // 获取资产名称
    const asset = assetOptions.value.find(a => a.value === form.assetId)
    const assetName = asset ? asset.label : form.assetId

    submitLoading.value = true
    try {
      await submitMaintenanceRequest({
        assetType: form.assetType,
        assetId: form.assetId,
        assetName: assetName,
        faultDescription: form.faultDescription,
        reporterName: userStore.realName || userStore.userInfo?.username || '用户',
        reporterDept: '用户部门'
      })
      ElMessage.success('维修申请提交成功，请等待处理')
      handleReset()
      loadMyRecords()
    } catch (error: any) {
      console.error('提交失败:', error)
      ElMessage.error(error.message || '提交失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置
function handleReset() {
  formRef.value?.resetFields()
  form.assetType = 'equipment'
  form.assetName = ''
  imageFileList.value = []
  imageBase64List.value = []
  handleAssetTypeChange('equipment')
}

// 图片上传变化处理
function handleImageChange(file: UploadFile, fileList: UploadFiles) {
  // 验证文件大小（5MB限制）
  if (file.raw && file.raw.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过5MB')
    const index = fileList.indexOf(file)
    if (index > -1) {
      fileList.splice(index, 1)
    }
    return
  }
  
  // 将图片转换为Base64
  if (file.raw) {
    const reader = new FileReader()
    reader.onload = (e) => {
      const base64 = e.target?.result as string
      imageBase64List.value.push(base64)
    }
    reader.readAsDataURL(file.raw)
  }
  
  imageFileList.value = fileList
}

// 图片删除处理
function handleImageRemove(file: UploadFile, fileList: UploadFiles) {
  imageFileList.value = fileList
  // 重新生成Base64列表
  imageBase64List.value = []
  fileList.forEach((f) => {
    if (f.raw) {
      const reader = new FileReader()
      reader.onload = (e) => {
        const base64 = e.target?.result as string
        imageBase64List.value.push(base64)
      }
      reader.readAsDataURL(f.raw)
    }
  })
}

// 超出图片数量限制
function handleImageExceed() {
  ElMessage.warning('最多只能上传5张图片')
}

onMounted(() => {
  handleAssetTypeChange('equipment')
  loadMyRecords()
})
</script>

<style scoped>
.maintenance-apply-page {
  height: 100%;
}
</style>