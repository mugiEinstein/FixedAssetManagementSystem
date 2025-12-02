<template>
  <div class="my-schedule-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的课表</span>
          <div class="header-actions">
            <el-select v-model="currentWeek" placeholder="选择周次" style="width: 140px; margin-right: 12px;">
              <el-option v-for="w in 20" :key="w" :label="`第${w}周`" :value="w" />
            </el-select>
            <el-button-group>
              <el-button @click="currentWeek = Math.max(1, currentWeek - 1)">上一周</el-button>
              <el-button @click="currentWeek = Math.min(20, currentWeek + 1)">下一周</el-button>
            </el-button-group>
          </div>
        </div>
      </template>

      <!-- 课表表格 -->
      <div class="schedule-container">
        <table class="schedule-table">
          <thead>
            <tr>
              <th class="time-header">时间</th>
              <th v-for="day in weekDays" :key="day" :class="{ 'today': isToday(day) }">{{ day }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(section, idx) in sections" :key="idx">
              <td class="time-cell">
                <div class="section-name">{{ section.name }}</div>
                <div class="section-time">{{ section.time }}</div>
              </td>
              <td v-for="day in weekDays" :key="day" class="course-cell" :class="{ 'today': isToday(day) }">
                <div 
                  v-if="getCourse(day, idx)" 
                  class="course-item"
                  :style="{ backgroundColor: getCourseColor(getCourse(day, idx)?.type) }"
                  @click="showCourseDetail(getCourse(day, idx))"
                >
                  <div class="course-name">{{ getCourse(day, idx)?.courseName }}</div>
                  <div class="course-location">{{ getCourse(day, idx)?.location }}</div>
                  <div class="course-teacher">{{ getCourse(day, idx)?.teacher }}</div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 图例 -->
      <div class="legend">
        <span class="legend-title">课程类型：</span>
        <span class="legend-item"><span class="legend-color" style="background: #409eff;"></span>通识课</span>
        <span class="legend-item"><span class="legend-color" style="background: #67c23a;"></span>专业课</span>
        <span class="legend-item"><span class="legend-color" style="background: #e6a23c;"></span>实验课</span>
        <span class="legend-item"><span class="legend-color" style="background: #909399;"></span>选修课</span>
      </div>
    </el-card>

    <!-- 课程详情弹窗 -->
    <el-dialog v-model="detailVisible" title="课程详情" width="400px">
      <el-descriptions :column="1" border v-if="currentCourse">
        <el-descriptions-item label="课程名称">{{ currentCourse.courseName }}</el-descriptions-item>
        <el-descriptions-item label="课程类型">{{ currentCourse.type }}</el-descriptions-item>
        <el-descriptions-item label="授课教师">{{ currentCourse.teacher }}</el-descriptions-item>
        <el-descriptions-item label="上课地点">{{ currentCourse.location }}</el-descriptions-item>
        <el-descriptions-item label="上课周次">{{ currentCourse.weeks }}</el-descriptions-item>
        <el-descriptions-item label="学分">{{ currentCourse.credit }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

interface Course {
  courseName: string
  type: string
  teacher: string
  location: string
  weeks: string
  credit: number
  day: string
  sectionIdx: number
}

const currentWeek = ref(12)
const detailVisible = ref(false)
const currentCourse = ref<Course | null>(null)

const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

const sections = [
  { name: '第1-2节', time: '08:00-09:40' },
  { name: '第3-4节', time: '10:00-11:40' },
  { name: '第5-6节', time: '14:00-15:40' },
  { name: '第7-8节', time: '16:00-17:40' },
  { name: '第9-10节', time: '19:00-20:40' }
]

// 课表数据
const courses = ref<Course[]>([
  { courseName: '线性代数', type: '通识课', teacher: '王老师', location: '逸夫楼101', weeks: '1-16周', credit: 3, day: '周一', sectionIdx: 0 },
  { courseName: '大学物理实验', type: '实验课', teacher: '刘老师', location: '主楼201', weeks: '1-16周', credit: 2, day: '周一', sectionIdx: 2 },
  { courseName: '嵌入式系统设计', type: '专业课', teacher: '张教授', location: '逸夫楼301', weeks: '1-16周', credit: 4, day: '周一', sectionIdx: 4 },
  { courseName: '程序设计基础', type: '专业课', teacher: '李老师', location: '逸夫楼102', weeks: '1-16周', credit: 4, day: '周二', sectionIdx: 1 },
  { courseName: '嵌入式系统设计', type: '专业课', teacher: '张教授', location: '逸夫楼301', weeks: '1-16周', credit: 4, day: '周三', sectionIdx: 0 },
  { courseName: '大学英语', type: '通识课', teacher: '陈老师', location: '主楼201', weeks: '1-16周', credit: 2, day: '周三', sectionIdx: 3 },
  { courseName: '数据结构', type: '专业课', teacher: '郑老师', location: '逸夫楼201', weeks: '1-16周', credit: 4, day: '周四', sectionIdx: 0 },
  { courseName: '体育', type: '选修课', teacher: '体育老师', location: '操场', weeks: '1-16周', credit: 1, day: '周五', sectionIdx: 2 }
])

function getCourse(day: string, sectionIdx: number): Course | undefined {
  return courses.value.find(c => c.day === day && c.sectionIdx === sectionIdx)
}

function getCourseColor(type: string | undefined): string {
  const colorMap: Record<string, string> = {
    '通识课': '#409eff',
    '专业课': '#67c23a',
    '实验课': '#e6a23c',
    '选修课': '#909399'
  }
  return colorMap[type || ''] || '#409eff'
}

function isToday(day: string): boolean {
  const today = new Date().getDay()
  const dayIndex = weekDays.indexOf(day)
  return today === (dayIndex + 1) % 7 || (today === 0 && dayIndex === 6)
}

function showCourseDetail(course: Course | undefined) {
  if (course) {
    currentCourse.value = course
    detailVisible.value = true
  }
}

onMounted(() => {
  // 可对接后台API加载课表
})
</script>

<style scoped>
.my-schedule-page {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.schedule-container {
  overflow-x: auto;
}

.schedule-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 800px;
}

.schedule-table th,
.schedule-table td {
  border: 1px solid #ebeef5;
  padding: 8px;
  text-align: center;
  vertical-align: top;
}

.schedule-table th {
  background: #f5f7fa;
  font-weight: 500;
  color: #606266;
}

.schedule-table th.today {
  background: #ecf5ff;
  color: #409eff;
}

.time-header {
  width: 100px;
}

.time-cell {
  background: #f5f7fa;
  width: 100px;
}

.section-name {
  font-weight: 500;
  color: #303133;
}

.section-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.course-cell {
  height: 80px;
  min-width: 100px;
}

.course-cell.today {
  background: #f0f9ff;
}

.course-item {
  padding: 8px;
  border-radius: 4px;
  color: #fff;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  transition: transform 0.2s, box-shadow 0.2s;
}

.course-item:hover {
  transform: scale(1.02);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.course-name {
  font-weight: 500;
  font-size: 13px;
  margin-bottom: 4px;
}

.course-location,
.course-teacher {
  font-size: 11px;
  opacity: 0.9;
}

.legend {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #606266;
}

.legend-title {
  font-weight: 500;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.legend-color {
  width: 14px;
  height: 14px;
  border-radius: 2px;
}
</style>