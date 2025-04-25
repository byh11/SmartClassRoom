<template>
  <div class="video-progress">
    <div v-if="progress" class="progress-info">
      <span>观看进度: {{ Math.floor(progress.progress) }}%</span>
      <span>上次观看: {{ formatTime(progress.lastPosition) }}</span>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useStore} from 'vuex'
import api from '@/api'

const props = defineProps({
  videoId: {
    type: [String, Number],
    required: true
  }
})

const store = useStore()
const progress = ref(null)

const formatTime = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const fetchProgress = async () => {
  try {
    const studentId = store.state.user.studentid
    if (!studentId) return

    const res = await api.getVideoProgress(props.videoId, studentId)
    if (res.data.code === 200) {
      progress.value = res.data.data
    }
  } catch (error) {
    console.error('获取学习进度失败:', error)
  }
}

onMounted(() => {
  fetchProgress()
})
</script>

<style scoped>
.video-progress {
  margin: 10px 0;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 14px;
}
</style> 