<template>
  <div
      class="video-controls"
      @mouseleave="showControls = false"
      @mouseover="showControls = true"
  >
    <!-- 进度条 -->
    <div class="progress-bar">
      <el-slider
          v-model="currentTime"
          :format-tooltip="formatTime"
          :max="duration"
          :min="0"
          @change="handleSeek"
      />
    </div>

    <!-- 控制按钮 -->
    <div :class="{ 'show': showControls }" class="control-buttons">
      <div class="left-controls">
        <el-button text @click="togglePlay">
          <el-icon>
            <component :is="isPlaying ? 'Pause' : 'VideoPlay'"/>
          </el-icon>
        </el-button>

        <div class="volume-control">
          <el-button text @click="toggleMute">
            <el-icon>
              <component :is="volume === 0 ? 'Mute' : 'VideoPlay'"/>
            </el-icon>
          </el-button>
          <el-slider
              v-model="volume"
              :max="100"
              :min="0"
              style="width: 80px"
          />
        </div>

        <span class="time-display">
          {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
        </span>
      </div>

      <div class="right-controls">
        <el-button text @click="toggleDanmaku">
          <el-icon>
            <ChatDotRound/>
          </el-icon>
          {{ showDanmaku ? '关闭弹幕' : '开启弹幕' }}
        </el-button>

        <el-button text @click="toggleFullscreen">
          <el-icon>
            <component :is="isFullscreen ? 'FullscreenExit' : 'FullScreen'"/>
          </el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, watch} from 'vue'
import {formatTime} from '@/utils/format'

const props = defineProps({
  duration: {
    type: Number,
    required: true
  },
  currentTime: {
    type: Number,
    required: true
  },
  isPlaying: {
    type: Boolean,
    required: true
  },
  volume: {
    type: Number,
    required: true
  },
  showDanmaku: {
    type: Boolean,
    required: true
  },
  isFullscreen: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits([
  'seek',
  'toggle-play',
  'volume-change',
  'toggle-mute',
  'toggle-danmaku',
  'toggle-fullscreen'
])

const showControls = ref(false)

const handleSeek = (value) => {
  emit('seek', value)
}

const togglePlay = () => {
  emit('toggle-play')
}

const toggleMute = () => {
  emit('toggle-mute')
}

const toggleDanmaku = () => {
  emit('toggle-danmaku')
}

const toggleFullscreen = () => {
  emit('toggle-fullscreen')
}

watch(() => props.volume, (newValue) => {
  emit('volume-change', newValue)
})
</script>

<style scoped>
.video-controls {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  padding: 10px;
  transition: opacity 0.3s;
}

.progress-bar {
  padding: 10px 0;
}

.control-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.control-buttons.show {
  opacity: 1;
}

.left-controls,
.right-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.volume-control {
  display: flex;
  align-items: center;
  gap: 5px;
}

.time-display {
  color: #fff;
  font-size: 14px;
}

:deep(.el-button) {
  color: #fff;
}
</style> 