<template>
  <div class="value-calculation-page">
    <el-card>
      <template #header>
        <span>资产价值核算报表</span>
      </template>
      <el-table :data="tableData" border>
        <el-table-column prop="assetId" label="资产编号" width="120" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="assetType" label="类型" width="100" />
        <el-table-column prop="originalValue" label="原值(元)" width="120">
          <template #default="{ row }">
            {{ row.originalValue?.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="annualDepreciation" label="年折旧" width="100" />
        <el-table-column prop="usedYears" label="已用年限" width="100" />
        <el-table-column prop="netValue" label="净值(元)" width="120">
          <template #default="{ row }">
            <span style="color: #409eff; font-weight: bold;">
              {{ row.netValue?.toLocaleString() }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 模拟数据
const tableData = ref([
  {
    assetId: 'BD-001',
    assetName: '逸夫楼',
    assetType: 'building',
    originalValue: 28600000,
    annualDepreciation: 572000,
    usedYears: 5,
    netValue: 25740000,
    remark: ''
  },
  {
    assetId: 'EQ-001',
    assetName: '高精度光谱分析仪',
    assetType: 'equipment',
    originalValue: 500000,
    annualDepreciation: 50000,
    usedYears: 3,
    netValue: 350000,
    remark: ''
  }
])
onMounted(() => { /*可对接接口*/ })
</script>

<style scoped>
.value-calculation-page {
  height: 100%;
}
</style>