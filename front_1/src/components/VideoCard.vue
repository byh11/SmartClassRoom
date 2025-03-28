<template>
  <div v-if="isValid" class="video-card" @click="handleClick">
    <!-- 视频封面 -->
    <div class="video-cover">
      <el-image
          v-if="video.coverUrl"
          :src="video.coverUrl"
          fit="cover"
          @error="handleImageError"
      />
      <el-image
          v-else
          :src="defaultCover"
          fit="cover"
      />
      <div class="play-button">
        <el-icon>
          <VideoPlay/>
        </el-icon>
      </div>
      <span class="duration">{{ video.duration }}</span>
    </div>

    <!-- 视频信息 -->
    <div class="video-info">
      <h3 class="title">{{ video.videoidName }}</h3>
      <div class="meta">
        <div class="teacher-info">
          <el-avatar :size="24" :src="video.teacherAvatar || defaultAvatar"/>
          <span class="teacher-name">{{ video.teacherName }}</span>
        </div>
        <div class="stats">
          <span class="views">
            <el-icon><View/></el-icon>
            {{ formatNumber(video.views) }}
          </span>
          <span class="likes">
            <el-icon><Star/></el-icon>
            {{ formatNumber(video.likeNum) }}
          </span>
        </div>
      </div>
      <p class="description">{{ video.description }}</p>
      <div class="tags">
        <el-tag v-if="video.videoType" size="small">{{ video.videoType }}</el-tag>
        <el-tag v-if="video.className" size="small" type="success">{{ video.className }}</el-tag>
      </div>
    </div>
  </div>
</template>

<script setup>
import {computed} from 'vue'
import {Star, VideoPlay, View} from '@element-plus/icons-vue'
import {formatNumber} from '@/utils/format'

const props = defineProps({
  video: {
    type: Object,
    required: true,
    default: () => ({
      videoid: '',
      videoidName: '',
      coverUrl: '',
      duration: '',
      views: 0,
      likeNum: 0,
      collectNum: 0,
      teacherName: '',
      teacherAvatar: '',
      description: '',
      videoType: '',
      className: '',
      startTime: '',
      endTime: ''
    })
  }
})

const defaultCover = 'https://via.placeholder.com/300x200'
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const isValid = computed(() => {
  return props.video && props.video.videoid
})

const handleClick = () => {
  emit('click', props.video)
}

const handleImageError = () => {
  // 图片加载失败时使用默认图片
  props.video.coverUrl = defaultCover
}

const emit = defineEmits(['click'])
</script>

<style scoped>
.video-card {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  cursor: pointer;
}

.video-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.video-cover {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 比例 */
}

.video-cover .el-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.image-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  color: #909399;
}

.video-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  padding: 2px 6px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
}

.video-type {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 6px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
}

.video-stats {
  position: absolute;
  bottom: 8px;
  left: 8px;
  display: flex;
  gap: 10px;
}

.views {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 2px 6px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
}

.video-info {
  padding: 12px;
}

.video-title {
  margin: 0;
  font-size: 16px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.video-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.author {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.stats {
  display: flex;
  gap: 12px;
  font-size: 14px;
  color: #909399;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.video-desc {
  margin: 8px 0 0;
  font-size: 14px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-info {
  margin-top: 8px;
}

.video-actions {
  padding: 8px 12px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.play-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style> 