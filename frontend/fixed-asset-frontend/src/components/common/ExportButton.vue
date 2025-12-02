<template>
  <el-button :loading="loading" :icon="Download" @click="handleExport">
    {{ text }}
  </el-button>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

interface Props {
  text?: string
  filename?: string
  exportFn: () => Promise<Blob | any>
}

const props = withDefaults(defineProps<Props>(), {
  text: '导出',
  filename: 'export.xlsx'
})

const loading = ref(false)

async function handleExport() {
  loading.value = true
  try {
    const res = await props.exportFn()

    // 处理 Blob 下载
    const blob = res instanceof Blob ? res : new Blob([res])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = props.filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL. revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  } finally {
    loading. value = false
  }
}
</script>