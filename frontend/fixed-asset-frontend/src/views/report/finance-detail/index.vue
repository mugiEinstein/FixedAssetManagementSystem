<template>
  <div class="finance-detail-page">
    <el-card>
      <template #header>
        <span>财务收支明细</span>
      </template>
      <el-table :data="tableData" border>
        <el-table-column prop="feeId" label="单号" width="120" />
        <el-table-column prop="feeType" label="类型" width="100" />
        <el-table-column prop="payerName" label="付款人" width="120" />
        <el-table-column prop="payerType" label="人员类型" width="100" />
        <el-table-column prop="amount" label="金额(元)" width="120" />
        <el-table-column prop="paymentDate" label="付款日期" width="110" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <AssetStatusTag :status="row. status" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AssetStatusTag from '@/components/common/AssetStatusTag.vue'
const tableData = ref([
  { feeId: 'F-001', feeType: '器材借用', payerName: '张三', payerType: '学生', amount: 300, paymentDate: '2024-07-11', status: 'paid' },
  { feeId: 'F-002', feeType: '房产分配', payerName: '王老师', payerType: '教职工', amount: 1200, paymentDate: '2024-08-15', status: 'paid' }
])
onMounted(() => { /*可对接接口*/ })
</script>
<style scoped>
.finance-detail-page { height: 100%; }
</style>