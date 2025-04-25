<template>
  <div class="learning-stats">
    <el-card class="stats-card">
      <template #header>
        <div class="card-header">
          <h3>学习统计</h3>
          <div class="header-controls">
            <el-date-picker
                v-model="dateRange"
                :shortcuts="dateShortcuts"
                end-placeholder="结束日期"
                range-separator="至"
                start-placeholder="开始日期"
                type="daterange"
                @change="handleDateRangeChange"
            />
          </div>
        </div>
      </template>

      <div v-loading="loading" class="stats-content">
        <div class="stats-item">
          <div class="stats-label">总学习时长</div>
          <div class="stats-value">{{ formatTime(stats.totalWatchTime) }}</div>
        </div>

        <div class="stats-item">
          <div class="stats-label">完成视频数</div>
          <div class="stats-value">{{ stats.completedVideos }}/{{ stats.totalVideos }}</div>
        </div>

        <div class="stats-item">
          <div class="stats-label">最后学习日期</div>
          <div class="stats-value">{{ formatDateTime(stats.lastStudyDate) }}</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useStore} from 'vuex'
import {ElMessage} from 'element-plus'
import api from '@/api'

const store = useStore()
const loading = ref(false)
const dateRange = ref([])
const stats = ref({
  totalWatchTime: 0,
  totalVideos: 0,
  averageWatchTime: 0,
  watchRecords: []
})

// 日期快捷选项
const dateShortcuts = [
  {
    text: '今天',
    value: () => {
      const end = new Date()
      const start = new Date()
      return [start, end]
    }
  },
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '最近一月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  },
  {
    text: '最近三月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    }
  }
]

const formatTime = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  return `${hours}小时${minutes}分钟${secs}秒`
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr || dateTimeStr === '-') return '-'
  const date = new Date(dateTimeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 格式化日期为 YYYY-MM-DD
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const fetchStats = async () => {
  loading.value = true
  try {
    const studentId = store.state.user.studentid
    if (!studentId) {
      ElMessage.error('请先登录')
      return
    }

    let startDate = '', endDate = ''
    if (dateRange.value && dateRange.value.length === 2) {
      startDate = formatDate(dateRange.value[0])
      endDate = formatDate(dateRange.value[1])
    }

    const res = await api.getLearningStats(studentId, startDate, endDate)
    if (res.data.code === 200) {
      stats.value = res.data.data
    }
  } catch (error) {
    console.error('获取学习统计失败:', error)
    ElMessage.error('获取学习统计失败')
  } finally {
    loading.value = false
  }
}

const handleDateRangeChange = () => {
  fetchStats()
}

onMounted(() => {
  // 默认选择最近一周
  dateRange.value = dateShortcuts[0].value()
  fetchStats()
})
</script>

<style scoped>
.learning-stats {
  padding: 20px;
}

.stats-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  padding: 20px;
}

.stats-item {
  text-align: center;
}

.stats-label {
  color: #666;
  margin-bottom: 10px;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}
</style> 