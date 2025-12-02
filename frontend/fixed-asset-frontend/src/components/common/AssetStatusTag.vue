<template>
  <el-tag :type="tagType" :effect="effect">
    {{ statusText }}
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  status: string
  effect?: 'dark' | 'light' | 'plain'
}

const props = withDefaults(defineProps<Props>(), {
  effect: 'light'
})

const statusMap: Record<string, { type: string; text: string }> = {
  // 资产状态
  normal: { type: 'success', text: '正常' },
  in_use: { type: 'primary', text: '在用' },
  idle: { type: 'info', text: '闲置' },
  maintenance: { type: 'warning', text: '维修中' },
  scrapped: { type: 'danger', text: '已报废' },
  reserved: { type: 'primary', text: '已预订' },

  // 审批状态
  pending: { type: 'warning', text: '待审核' },
  approved: { type: 'success', text: '已通过' },
  rejected: { type: 'danger', text: '已驳回' },

  // 借用状态
  returned: { type: 'success', text: '已归还' },
  overdue: { type: 'danger', text: '已逾期' },
  need_repair: { type: 'warning', text: '需维修' },

  // 住宿状态
  active: { type: 'success', text: '在住' },
  transferred: { type: 'info', text: '已调换' },
  exited: { type: 'info', text: '已退宿' },

  // 维保状态
  assigned: { type: 'primary', text: '已派单' },
  in_progress: { type: 'warning', text: '维修中' },
  completed: { type: 'success', text: '已完成' },

  // 费用状态
  paid: { type: 'success', text: '已支付' },
  refunded: { type: 'info', text: '已退款' },
  partial_refund: { type: 'warning', text: '部分退款' },

  // 盘点状态
  surplus: { type: 'success', text: '盘盈' },
  loss: { type: 'danger', text: '盘亏' },
  damaged: { type: 'warning', text: '损坏' }
}

const tagType = computed(() => {
  return (statusMap[props.status]?.type || 'info') as any
})

const statusText = computed(() => {
  return statusMap[props.status]?.text || props.status
})
</script>