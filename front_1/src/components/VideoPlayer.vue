<template>
  <div class="video-player">
    <div ref="playerContainer" class="player-container">
      <video
          ref="videoRef"
          :src="video.url"
          controls
          @pause="handlePause"
          @play="handlePlay"
          @timeupdate="handleTimeUpdate"
      ></video>

      <div v-show="isPlaying" class="danmaku-container">
        <div
            v-for="danmaku in activeDanmaku"
            :key="danmaku.id"
            :style="danmaku.style"
            class="danmaku-item"
        >
          {{ danmaku.content }}
        </div>
      </div>
    </div>

    <div class="danmaku-control">
      <el-input
          v-model="danmakuText"
          placeholder="发送弹幕..."
          @keyup.enter="sendDanmaku"
      >
        <template #append>
          <el-button @click="sendDanmaku">发送</el-button>
        </template>
      </el-input>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, onUnmounted} from 'vue'
import {ElMessage} from 'element-plus'

const props = defineProps({
  video: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['danmaku-send'])

const videoRef = ref(null)
const playerContainer = ref(null)
const danmakuText = ref('')
const isPlaying = ref(false)
const activeDanmaku = ref([])

let danmakuId = 0

const handleTimeUpdate = () => {
  // 处理弹幕时间轴同步
  const currentTime = videoRef.value.currentTime
  // 这里可以添加根据时间显示对应弹幕的逻辑
}

const handlePlay = () => {
  isPlaying.value = true
}

const handlePause = () => {
  isPlaying.value = false
}

const createDanmaku = (content) => {
  const containerHeight = playerContainer.value.clientHeight - 40
  const randomTop = Math.floor(Math.random() * containerHeight)

  const danmaku = {
    id: danmakuId++,
    content,
    style: {
      top: `${randomTop}px`,
      transform: 'translateX(100%)',
      animation: 'danmaku 8s linear'
    }
  }

  activeDanmaku.value.push(danmaku)
  setTimeout(() => {
    activeDanmaku.value = activeDanmaku.value.filter(d => d.id !== danmaku.id)
  }, 8000)
}

const sendDanmaku = () => {
  if (!danmakuText.value.trim()) {
    ElMessage.warning('请输入弹幕内容')
    return
  }

  createDanmaku(danmakuText.value)
  emit('danmaku-send', danmakuText.value)
  danmakuText.value = ''
}

onMounted(() => {
  // 可以添加初始化逻辑
})

onUnmounted(() => {
  // 清理工作
})
</script>

<style scoped>
.video-player {
  width: 100%;
}

.player-container {
  position: relative;
  width: 100%;
  background: #000;
  overflow: hidden;
}

.player-container video {
  width: 100%;
  height: auto;
}

.danmaku-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.danmaku-item {
  position: absolute;
  white-space: nowrap;
  color: #fff;
  text-shadow: 1px 1px 2px #000;
  font-size: 20px;
}

.danmaku-control {
  margin-top: 10px;
}

@keyframes danmaku {
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(-100%);
  }
}
</style> 