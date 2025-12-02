<template>
  <div class="asset-statistics-page">
    <el-card>
      <template #header>
        <span>资产统计</span>
      </template>
      <el-table :data="tableData" border>
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="count" label="资产数量" width="120" />
        <el-table-column prop="totalValue" label="总价值(元)" width="160">
          <template #default="{ row }">
            {{ row.totalValue?.toLocaleString() }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
const tableData = ref([
  { category: '建筑', count: 20, totalValue: 165000000 },
  { category: '器材', count: 320, totalValue: 58000000 },
  { category: '车辆', count: 12, totalValue: 1500000 }
])
onMounted(() => { /*可对接接口*/ })
</script>

<style scoped>
.asset-statistics-page { height: 100%; }
</style>