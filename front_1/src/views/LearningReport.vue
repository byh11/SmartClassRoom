<template>
  <div class="learning-report">
    <el-card class="report-card">
      <template #header>
        <div class="card-header">
          <h3>学习报告</h3>
          <div class="header-controls">
            <el-radio-group v-model="reportType" @change="handleReportTypeChange">
              <el-radio-button :label="1">日报</el-radio-button>
              <el-radio-button :label="2">周报</el-radio-button>
              <el-radio-button :label="3">月报</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <div v-loading="loading" class="report-content">
        <div v-if="reports.length === 0" class="empty-state">
          <p>暂无学习报告</p>
          <el-button type="primary" @click="generateNewReport">
            生成报告
          </el-button>
        </div>

        <div v-else class="report-list">
          <div v-for="report in reports" :key="report.id" class="report-item">
            <div class="report-header">
              <span class="report-date">{{ formatDate(report.reportDate) }}</span>
              <el-button size="small" type="primary" @click="generateNewReport">
                重新生成
              </el-button>
            </div>

            <div class="report-stats">
              <div class="stat-item">
                <div class="stat-label">学习时长</div>
                <div class="stat-value">{{ formatTime(report.watchTime) }}</div>
              </div>

              <div class="stat-item">
                <div class="stat-label">完成视频</div>
                <div class="stat-value">{{ report.completedVideos }}</div>
              </div>
            </div>

            <div v-if="report.suggestions" class="report-suggestions">
              <h4>学习建议</h4>
              <p>{{ report.suggestions }}</p>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue'
import {useStore} from 'vuex'
import {ElMessage} from 'element-plus'
import api from '@/api'

const store = useStore()
const loading = ref(false)
const reportType = ref(1)
const reports = ref([])

// 根据报告类型计算日期范围
const dateRange = computed(() => {
  const today = new Date()
  const start = new Date()

  if (reportType.value === 1) {
    // 日报：当天
    return [today, today]
  } else if (reportType.value === 2) {
    // 周报：本周
    const day = today.getDay() || 7 // 将周日的0转为7
    start.setDate(today.getDate() - day + 1) // 设置为本周一
    return [start, today]
  } else {
    // 月报：本月
    start.setDate(1) // 设置为本月1号
    return [start, today]
  }
})

// 格式化时间（包含秒）
const formatTime = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  return `${hours}小时${minutes}分钟${secs}秒`
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const fetchReports = async () => {
  loading.value = true
  try {
    const studentId = store.state.user.studentid
    if (!studentId) {
      ElMessage.error('请先登录')
      return
    }

    const [startDate, endDate] = dateRange.value
    const formatDateForApi = (date) => {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }

    const res = await api.getLearningReports(
        studentId,
        reportType.value,
        formatDateForApi(startDate),
        formatDateForApi(endDate)
    )

    if (res.data.code === 200) {
      reports.value = res.data.data
    }
  } catch (error) {
    console.error('获取学习报告失败:', error)
    ElMessage.error('获取学习报告失败')
  } finally {
    loading.value = false
  }
}

const generateNewReport = async () => {
  try {
    const studentId = store.state.user.studentid
    if (!studentId) {
      ElMessage.error('请先登录')
      return
    }

    const res = await api.generateLearningReport(studentId, reportType.value)
    if (res.data.code === 200) {
      ElMessage.success('生成报告成功')
      fetchReports()
    }
  } catch (error) {
    console.error('生成报告失败:', error)
    ElMessage.error('生成报告失败')
  }
}

const handleReportTypeChange = () => {
  fetchReports()
}

onMounted(() => {
  fetchReports()
})
</script>

<style scoped>
.learning-report {
  padding: 20px;
}

.report-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-controls {
  display: flex;
  gap: 20px;
  align-items: center;
}

.report-content {
  padding: 20px;
}

.empty-state {
  text-align: center;
  color: #999;
  padding: 40px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.empty-state p {
  margin: 0;
  font-size: 16px;
}

.report-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.report-item {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.report-date {
  font-size: 18px;
  font-weight: bold;
}

.report-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.report-suggestions {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.report-suggestions h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.report-suggestions p {
  margin: 0;
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
}
</style> 