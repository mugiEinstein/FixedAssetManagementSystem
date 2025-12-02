<template>
  <div class="course-scheduling-page">
    <el-card>
      <template #header>
        <span>排课需求汇总</span>
      </template>

      <DataTable
          :data="tableData"
          :loading="loading"
          :total="total"
          :page="queryParams.page"
          :size="queryParams.pageSize"
          @page-change="handlePageChange"
          :show-add="false"
          :show-export="false"
      >
        <el-table-column prop="courseId" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="teacher" label="授课教师" width="110" />
        <el-table-column prop="department" label="开课院系" width="110" />
        <el-table-column prop="type" label="类型" width="110" />
        <el-table-column prop="capacity" label="课容量" width="90" />
        <el-table-column prop="time" label="时间安排" min-width="140" />
        <el-table-column prop="requiredEquipment" label="设备需求" width="120" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <AssetStatusTag :status="row.status" />
          </template>
        </el-table-column>
      </DataTable>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import DataTable from '@/components/common/DataTable.vue'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'

// 查询参数
const queryParams = reactive({
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref([
  {
    courseId: '2024-001',
    courseName: '线性代数',
    teacher: '王老师',
    department: '数学系',
    type: '通识课',
    capacity: 60,
    time: '周一 上午',
    requiredEquipment: '投影仪',
    status: 'approved'
  },
  {
    courseId: '2024-002',
    courseName: '大学物理实验',
    teacher: '刘老师',
    department: '物理系',
    type: '实验课',
    capacity: 32,
    time: '周二 下午',
    requiredEquipment: '多媒体、实验台',
    status: 'approved'
  },
  {
    courseId: '2024-003',
    courseName: '嵌入式系统设计',
    teacher: '张教授',
    department: '计算机学院',
    type: '专业课',
    capacity: 45,
    time: '周三 上午',
    requiredEquipment: '开发板、计算机',
    status: 'pending'
  }
])
const total = ref(3)
const loading = ref(false)

function handlePageChange(page: number, size: number) {
  queryParams.page = page
  queryParams.pageSize = size
}

onMounted(() => {
  // 可对接后台加载需求
})
</script>

<style scoped>
.course-scheduling-page {
  height: 100%;
}
</style>