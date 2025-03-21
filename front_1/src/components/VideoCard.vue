<template>
  <div class="video-card" @click="handleClick">
    <!-- 视频封面 -->
    <div class="video-cover">
      <el-image
          :preview-src-list="[video.coverUrl]"
          :src="video.coverUrl"
          fit="cover"
          loading="lazy"
      >
        <template #error>
          <div class="image-placeholder">
            <el-icon>
              <Picture/>
            </el-icon>
          </div>
        </template>
      </el-image>

      <!-- 视频时长 -->
      <span class="video-duration">{{ formatDuration(video.duration) }}</span>

      <!-- 播放量 -->
      <div class="video-stats">
        <span class="views">
          <el-icon><View/></el-icon>
          {{ formatNumber(video.views) }}
        </span>
      </div>
    </div>

    <!-- 视频信息 -->
    <div class="video-info">
      <h3 :title="video.title" class="video-title">{{ video.title }}</h3>

      <div class="video-meta">
        <div class="author">
          <el-avatar
              :size="24"
              :src="video.teacher.avatar"
          />
          <span>{{ video.teacher.name }}</span>
        </div>

        <div class="stats">
          <span class="likes">
            <el-icon><Star/></el-icon>
            {{ formatNumber(video.likes) }}
          </span>
          <span class="comments">
            <el-icon><ChatDotRound/></el-icon>
            {{ formatNumber(video.comments) }}
          </span>
        </div>
      </div>

      <p v-if="showDescription" class="video-desc">{{ video.description }}</p>
    </div>

    <!-- 操作按钮 -->
    <div v-if="showActions" class="video-actions">
      <el-button-group>
        <el-button
            link
            type="primary"
            @click.stop="handlePlay"
        >
          播放
        </el-button>
        <el-button
            link
            type="primary"
            @click.stop="handleCollect"
        >
          {{ video.isCollected ? '取消收藏' : '收藏' }}
        </el-button>
      </el-button-group>
    </div>
  </div>
</template>

<script setup>
import {computed} from 'vue'
import {useRouter} from 'vue-router'
import {useStore} from 'vuex'
import {ElMessage} from 'element-plus'
import {formatDuration, formatNumber} from '@/utils/format'
import api from '@/api'

const props = defineProps({
  video: {
    type: Object,
    required: true,
    default: () => ({
      title: '',
      coverUrl: '',
      duration: 0,
      views: 0,
      likes: 0,
      comments: 0,
      teacher: {
        name: '',
        avatar: ''
      }
    })
  },
  showDescription: {
    type: Boolean,
    default: false
  },
  showActions: {
    type: Boolean,
    default: true
  }
})

const router = useRouter()
const store = useStore()

// 处理视频点击
const handleClick = () => {
  if (!store.state.user.isLoggedIn) {
    router.push('/login')
    return
  }
  router.push(`/video/${props.video.id}`)
}

// 处理播放按钮点击
const handlePlay = () => {
  if (!store.state.user.isLoggedIn) {
    router.push('/login')
    return
  }
  router.push(`/video/${props.video.id}`)
}

// 处理收藏按钮点击
const handleCollect = async () => {
  if (!store.state.user.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    if (props.video.isCollected) {
      await api.uncollectVideo(props.video.id)
      ElMessage.success('取消收藏成功')
    } else {
      await api.collectVideo(props.video.id)
      ElMessage.success('收藏成功')
    }
    // 触发父组件更新
    emit('update')
  } catch (error) {
    ElMessage.error(props.video.isCollected ? '取消收藏失败' : '收藏失败')
  }
}

// 发出的事件
const emit = defineEmits(['update'])
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

.video-actions {
  padding: 8px 12px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
}
</style> 