<template>
  <el-form :inline="true" class="search-bar" @submit.prevent="handleSearch">
    <slot></slot>
    <el-form-item>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button :icon="Refresh" @click="handleReset">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { Search, Refresh } from '@element-plus/icons-vue'

const emit = defineEmits<{
  search: []
  reset: []
}>()

function handleSearch() {
  emit('search')
}

function handleReset() {
  emit('reset')
}
</script>

<style scoped>
.search-bar {
  background: #fff;
  padding: 20px 20px 0;
  border-radius: 4px;
  margin-bottom: 16px;
}
</style>