<template>
  <div class="data-table">
    <!-- 工具栏 -->
    <div class="table-toolbar" v-if="showToolbar">
      <div class="toolbar-left">
        <slot name="toolbar-left">
          <el-button v-if="showAdd" type="primary" :icon="Plus" @click="handleAdd">
            新增
          </el-button>
        </slot>
      </div>
      <div class="toolbar-right">
        <slot name="toolbar-right">
          <el-button v-if="showExport" :icon="Download" @click="handleExport">
            导出
          </el-button>
          <el-button v-if="showImport" :icon="Upload" @click="handleImport">
            导入
          </el-button>
        </slot>
      </div>
    </div>

    <!-- 表格 -->
    <el-table
        v-loading="loading"
        :data="data"
        :border="border"
        :stripe="stripe"
        style="width: 100%"
        @selection-change="handleSelectionChange"
    >
      <el-table-column v-if="showSelection" type="selection" width="55" />
      <el-table-column v-if="showIndex" type="index" label="序号" width="60" />
      <slot></slot>
    </el-table>

    <!-- 分页 -->
    <div class="table-pagination" v-if="showPagination">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Plus, Download, Upload } from '@element-plus/icons-vue'

interface Props {
  data: any[]
  loading?: boolean
  total?: number
  page?: number
  size?: number
  border?: boolean
  stripe?: boolean
  showToolbar?: boolean
  showAdd?: boolean
  showExport?: boolean
  showImport?: boolean
  showSelection?: boolean
  showIndex?: boolean
  showPagination?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  total: 0,
  page: 1,
  size: 10,
  border: true,
  stripe: true,
  showToolbar: true,
  showAdd: true,
  showExport: true,
  showImport: false,
  showSelection: false,
  showIndex: true,
  showPagination: true
})

const emit = defineEmits<{
  add: []
  export: []
  import: []
  'selection-change': [selection: any[]]
  'page-change': [page: number, size: number]
}>()

const currentPage = ref(props.page)
const pageSize = ref(props.size)

watch(() => props.page, (val) => {
  currentPage.value = val
})

watch(() => props.size, (val) => {
  pageSize.value = val
})

function handleAdd() {
  emit('add')
}

function handleExport() {
  emit('export')
}

function handleImport() {
  emit('import')
}

function handleSelectionChange(selection: any[]) {
  emit('selection-change', selection)
}

function handleSizeChange(size: number) {
  emit('page-change', currentPage.value, size)
}

function handleCurrentChange(page: number) {
  emit('page-change', page, pageSize.value)
}
</script>

<style scoped>
.data-table {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  gap: 8px;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>