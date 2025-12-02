<template>
  <div class="fee-calculator-page">
    <el-card>
      <template #header>
        <span>费用计算工具</span>
      </template>
      <el-form label-width="110px" style="max-width: 500px;">
        <el-form-item label="选择计算类型">
          <el-select v-model="type" placeholder="请选择">
            <el-option label="器材外借押金" value="deposit" />
            <el-option label="器材借用租金" value="rent" />
            <el-option label="外借违约金" value="externalPenalty" />
            <el-option label="校内违约金" value="internalPenalty" />
            <el-option label="房产超面积费" value="excess" />
            <el-option label="教职工住宿租金" value="staffRent" />
          </el-select>
        </el-form-item>
        <template v-if="type === 'deposit'">
          <el-form-item label="器材原值(元)">
            <el-input-number v-model="form.originalValue" :min="0" />
          </el-form-item>
        </template>
        <template v-if="type === 'rent' || type === 'externalPenalty' || type === 'internalPenalty'">
          <el-form-item label="每日租金(元)">
            <el-input-number v-model="form.dailyRent" :min="0" />
          </el-form-item>
          <el-form-item label="天数">
            <el-input-number v-model="form.days" :min="1" />
          </el-form-item>
        </template>
        <template v-if="type === 'excess'">
          <el-form-item label="实际面积(㎡)">
            <el-input-number v-model="form.actualArea" :min="0" />
          </el-form-item>
          <el-form-item label="基准面积(㎡)">
            <el-input-number v-model="form.baseArea" :min="0" />
          </el-form-item>
          <el-form-item label="月数">
            <el-input-number v-model="form.months" :min="1" />
          </el-form-item>
        </template>
        <template v-if="type === 'staffRent'">
          <el-form-item label="住房面积(㎡)">
            <el-input-number v-model="form.area" :min="0" />
          </el-form-item>
        </template>
        <el-form-item>
          <el-button type="primary" @click="handleCalc">立即计算</el-button>
        </el-form-item>
        <el-form-item v-if="resultText">
          <span style="font-size: 18px; font-weight: bold; color: #409eff;">结果：{{ resultText }}</span>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  calculateEquipmentDeposit,
  calculateEquipmentRent,
  calculateExternalPenalty,
  calculateInternalPenalty,
  calculateExcessAreaFee,
  calculateStaffHousingRent
} from '@/utils/fee-calculator'

const type = ref('')
const form = ref({
  originalValue: 0,
  dailyRent: 0,
  days: 1,
  actualArea: 0,
  baseArea: 0,
  months: 1,
  area: 0
})
const resultText = ref('')

function handleCalc() {
  if (type.value === 'deposit') {
    resultText.value = `押金 = ¥${calculateEquipmentDeposit(form.value.originalValue)}`
  } else if (type.value === 'rent') {
    resultText.value = `租金 = ¥${calculateEquipmentRent(form.value.dailyRent, form.value.days)}`
  } else if (type.value === 'externalPenalty') {
    resultText.value = `外借违约金 = ¥${calculateExternalPenalty(form.value.dailyRent, form.value.days)}`
  } else if (type.value === 'internalPenalty') {
    resultText.value = `校内违约金 = ¥${calculateInternalPenalty(form.value.dailyRent, form.value.days)}`
  } else if (type.value === 'excess') {
    resultText.value = `超面积费 = ¥${calculateExcessAreaFee(form.value.actualArea, form.value.baseArea, form.value.months)}`
  } else if (type.value === 'staffRent') {
    resultText.value = `教职工住宿月租金 = ¥${calculateStaffHousingRent(form.value.area)}`
  } else {
    resultText.value = ''
  }
}
</script>

<style scoped>
.fee-calculator-page {
  height: 100%;
}
</style>