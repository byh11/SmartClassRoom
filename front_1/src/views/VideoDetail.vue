<template>
  <div class="video-detail">
    <div class="video-container">
      <div class="main-content">
        <VideoPlayer
            :video="videoData"
            @danmaku-send="handleDanmakuSend"
        />
        <div class="video-info">
          <h1>{{ videoData.title }}</h1>
          <div class="video-stats">
            <span>播放量：{{ videoData.views }}</span>
            <span>点赞数：{{ videoData.likes }}</span>
            <el-button
                :icon="videoData.isLiked ? 'Thumb' : 'ThumbFilled'"
                type="primary"
                @click="handleLike"
            >
              点赞
            </el-button>
          </div>
          <p class="description">{{ videoData.description }}</p>
        </div>

        <div class="comment-section">
          <h3>评论区</h3>
          <div class="comment-input">
            <el-input
                v-model="newComment"
                :rows="2"
                placeholder="发表评论..."
                type="textarea"
            />
            <el-button type="primary" @click="handleComment">发表评论</el-button>
          </div>
          <CommentList :comments="comments"/>
        </div>
      </div>

      <div class="side-content">
        <h3>相关视频</h3>
        <VideoCard
            v-for="video in relatedVideos"
            :key="video.id"
            :video="video"
            @click="handleVideoChange(video.id)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {useRoute} from 'vue-router'
import {ElMessage} from 'element-plus'
import VideoPlayer from '@/components/VideoPlayer.vue'
import CommentList from '@/components/CommentList.vue'
import VideoCard from '@/components/VideoCard.vue'
import api from '@/api'

const route = useRoute()
const videoId = ref(route.params.id)
const videoData = ref({})
const comments = ref([])
const relatedVideos = ref([])
const newComment = ref('')

const loadVideoData = async () => {
  try {
    const [videoRes, commentsRes, relatedRes] = await Promise.all([
      api.getVideoDetail(videoId.value),
      api.getComments(videoId.value),
      api.getVideoList() // 获取相关视频
    ])

    videoData.value = videoRes.data
    comments.value = commentsRes.data
    relatedVideos.value = relatedRes.data.filter(v => v.id !== videoId.value).slice(0, 5)
  } catch (error) {
    ElMessage.error('加载视频信息失败')
  }
}

const handleLike = async () => {
  try {
    await api.likeVideo(videoId.value)
    videoData.value.likes += 1
    videoData.value.isLiked = true
    ElMessage.success('点赞成功')
  } catch (error) {
    ElMessage.error('点赞失败')
  }
}

const handleComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    const res = await api.addComment(videoId.value, {content: newComment.value})
    comments.value.unshift(res.data)
    newComment.value = ''
    ElMessage.success('评论成功')
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

const handleDanmakuSend = async (content) => {
  try {
    await api.sendDanmaku(videoId.value, {content})
  } catch (error) {
    ElMessage.error('发送弹幕失败')
  }
}

const handleVideoChange = (newVideoId) => {
  videoId.value = newVideoId
  loadVideoData()
}

onMounted(loadVideoData)
</script>

<style scoped>
.video-detail {
  padding: 20px;
}

.video-container {
  display: flex;
  gap: 20px;
}

.main-content {
  flex: 1;
}

.side-content {
  width: 300px;
}

.video-info {
  margin: 20px 0;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 8px;
}

.video-stats {
  display: flex;
  gap: 20px;
  align-items: center;
  margin: 10px 0;
}

.description {
  margin-top: 10px;
  color: #666;
}

.comment-section {
  margin-top: 20px;
}

.comment-input {
  margin: 15px 0;
  display: flex;
  gap: 10px;
}
</style> 