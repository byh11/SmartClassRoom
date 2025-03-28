<template>
  <div class="video-detail">
    <div class="main-content">
      <div class="video-container">
        <!-- 视频播放器 -->
        <div class="video-player">
          <div v-if="loading" class="loading-overlay">
            <el-icon class="is-loading">
              <Loading/>
            </el-icon>
            <span>加载中...</span>
          </div>
          <div v-if="error" class="error-overlay">
            <el-icon>
              <Warning/>
            </el-icon>
            <span>{{ error }}</span>
          </div>
          <video
              ref="videoPlayer"
              :poster="videoInfo.coverUrl"
              :src="videoInfo.url"
              class="video-element"
              controls
              @canplay="handleCanPlay"
              @loadedmetadata="handleVideoLoaded"
              @pause="handlePause"
              @play="handlePlay"
          >
            您的浏览器不支持视频播放
          </video>
        </div>

        <!-- 视频信息 -->
        <div class="video-info">
          <h1 class="video-title">{{ videoInfo.videoName }}</h1>

          <div class="video-meta">
            <div class="author-info">
              <el-avatar :size="40" :src="videoInfo.teacherAvatar"/>
              <div class="author-detail">
                <span class="author-name">{{ videoInfo.teacherName }}</span>
                <span class="publish-time">{{ formatDate(videoInfo.startTime) }}</span>
              </div>
            </div>

            <div class="video-stats">
              <span class="stat-item">
                <el-icon><View/></el-icon>
                {{ formatNumber(videoInfo.views || 0) }}
              </span>
              <span class="stat-item">
                <el-icon><Star/></el-icon>
                {{ formatNumber(videoInfo.likeNum || 0) }}
              </span>
              <span class="stat-item">
                <el-icon><ChatDotRound/></el-icon>
                {{ formatNumber(videoInfo.collectNum || 0) }}
              </span>
            </div>
          </div>

          <div class="video-actions">
            <el-button-group>
              <el-button
                  :type="videoInfo.isCollected ? 'primary' : 'default'"
                  @click="handleCollect"
              >
                {{ videoInfo.isCollected ? '取消收藏' : '收藏' }}
              </el-button>
            </el-button-group>
          </div>

          <div class="video-description">
            <h3>视频简介</h3>
            <p>{{ videoInfo.videoText || '暂无简介' }}</p>
          </div>

          <div v-if="videoInfo.className" class="course-info">
            <h3>所属课程</h3>
            <el-tag>{{ videoInfo.className }}</el-tag>
          </div>
        </div>

        <!-- 评论区 -->
        <div class="comment-section">
          <h3>评论区</h3>
          <div class="comment-input">
            <el-input
                v-model="newComment"
                type="textarea"
                :maxlength="200"
                :rows="3"
                placeholder="写下你的评论..."
                show-word-limit
            />
            <el-button type="primary" @click="handleComment">发表评论</el-button>
          </div>
          <div class="comment-list">
            <div v-if="comments.length === 0" class="no-comments">
              暂无评论
            </div>
            <div v-else class="comment-items">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <el-avatar :src="defaultAvatar"/>
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="username">{{ comment.username }}</span>
                    <div class="comment-actions">
                      <span class="time">{{ formatDate(comment.createTime) }}</span>
                      <el-button
                          v-if="comment.userId === store.state.user.id"
                          class="delete-btn"
                          type="text"
                          @click="handleDeleteComment(comment)"
                      >
                        删除
                      </el-button>
                    </div>
                  </div>
                  <p class="text">{{ comment.content }}</p>
                  <div class="actions">
                    <el-button type="text" @click="handleLike(comment)">
                      <el-icon>
                        <StarFilled/>
                      </el-icon>
                      {{ comment.likes }}
                    </el-button>
                    <el-button type="text" @click="handleReply(comment)">
                      回复
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧相关视频列表 -->
    <div class="side-content">
      <h3>该教师的其他视频</h3>
      <div class="related-videos">
        <div v-if="relatedVideos.length === 0" class="no-videos">
          暂无其他视频
        </div>
        <div v-else class="video-list">
          <div v-for="video in relatedVideos" :key="video.videoid" class="video-item"
               @click="handleVideoChange(video.videoid)">
            <div class="video-cover">
              <el-image :src="defaultCover" fit="cover"/>
              <span class="duration">{{ video.duration }}</span>
            </div>
            <div class="video-info">
              <h4 class="title">{{ video.videoName }}</h4>
              <div class="meta">
                <span class="views">{{ video.likeNum }} 播放</span>
                <span class="time">{{ formatDate(video.startTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, onUnmounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useStore} from 'vuex'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ChatDotRound, Loading, Star, StarFilled, View, Warning} from '@element-plus/icons-vue'
import {formatNumber} from '@/utils/format'
import api from '@/api'

const route = useRoute()
const router = useRouter()
const store = useStore()
const loading = ref(true)
const error = ref('')
const videoPlayer = ref(null)
const isCollected = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const defaultCover = 'https://via.placeholder.com/300x200'

// 评论相关
const newComment = ref('')
const comments = ref([])

// 相关视频列表
const relatedVideos = ref([])

const videoInfo = ref({
  videoid: '',
  teacherid: '',
  url: '',
  videoName: '',
  likeNum: '0',
  collectNum: '0',
  duration: '',
  status: 1,
  startTime: '',
  endTime: '',
  className: '',
  videoType: '',
  videoText: ''
})

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

// 处理视频加载完成
const handleVideoLoaded = () => {
  console.log('视频元数据加载完成')
  console.log('视频时长:', videoPlayer.value.duration)
  console.log('视频URL:', videoInfo.value.url)
  loading.value = false
  error.value = ''
}

// 处理视频可以播放
const handleCanPlay = () => {
  console.log('视频可以播放')
  if (route.query.autoplay === 'true') {
    console.log('尝试自动播放')
    videoPlayer.value.play().catch(error => {
      console.error('自动播放失败:', error)
      ElMessage.warning('自动播放失败，请手动点击播放')
    })
  }
}

// 处理视频开始播放
const handlePlay = () => {
  console.log('视频开始播放')
}

// 处理视频暂停
const handlePause = () => {
  console.log('视频暂停')
}

// 获取视频详情
const fetchVideoDetail = async () => {
  try {
    loading.value = true
    error.value = ''
    const videoId = route.params.id
    console.log('获取视频ID:', videoId)
    const response = await api.getVideoDetail(videoId)
    console.log('视频详情响应:', response)

    if (response && response.data) {
      videoInfo.value = response.data.data
      // 确保视频URL存在
      if (!videoInfo.value.url) {
        error.value = '视频地址不存在'
        ElMessage.error('视频地址不存在')
        router.push('/')
        return
      }
      console.log('设置视频URL:', videoInfo.value.url)

      // 获取相关视频
      await fetchRelatedVideos()
      // 获取评论列表
      await fetchComments()
    } else {
      error.value = '获取视频信息失败：数据格式错误'
      ElMessage.error('获取视频信息失败：数据格式错误')
      router.push('/')
    }
  } catch (error) {
    console.error('获取视频详情失败:', error)
    error.value = '获取视频信息失败'
    ElMessage.error('获取视频信息失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

// 获取相关视频
const fetchRelatedVideos = async () => {
  try {
    const response = await api.getTeacherVideos(videoInfo.value.teacherid)
    if (response && response.data) {
      // 过滤掉当前视频
      relatedVideos.value = response.data.filter(v => v.videoid !== videoInfo.value.videoid)
    }
  } catch (error) {
    console.error('获取相关视频失败:', error)
  }
}

// 获取评论列表
const fetchComments = async () => {
  try {
    const response = await api.getComments(videoInfo.value.videoid)
    if (response.data && response.data.data) {
      comments.value = response.data.data
    }
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}

// 处理评论
const handleComment = async () => {
  if (!store.state.user.isLoggedIn) {
    router.push('/login')
    return
  }

  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    // 获取用户id
    let userid = store.state.user.studentid
    if (!userid) {
      userid = store.state.user.teacherid
    }
    const response = await api.addComment(userid, videoInfo.value.videoid, {
      content: newComment.value
    })
    if (response && response.data) {
      comments.value.unshift(response.data)
      newComment.value = ''
      ElMessage.success('评论成功')
    }
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

// 处理评论点赞
const handleLike = async (comment) => {
  if (!store.state.user.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    // 实现评论点赞逻辑
    ElMessage.success('点赞成功')
  } catch (error) {
    ElMessage.error('点赞失败')
  }
}

// 处理评论回复
const handleReply = (comment) => {
  // 实现评论回复逻辑
}

// 处理视频切换
const handleVideoChange = (videoId) => {
  router.push(`/video/${videoId}`)
}

// 处理收藏
const handleCollect = async () => {
  if (!store.state.user.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    if (isCollected.value) {
      await api.uncollectVideo(videoInfo.value.videoid)
      ElMessage.success('取消收藏成功')
    } else {
      await api.collectVideo(videoInfo.value.videoid)
      ElMessage.success('收藏成功')
    }
    isCollected.value = !isCollected.value
  } catch (error) {
    ElMessage.error(isCollected.value ? '取消收藏失败' : '收藏失败')
  }
}

// 处理删除评论
const handleDeleteComment = async (comment) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await api.deleteComment(comment.id)
    if (response && response.data) {
      // 从评论列表中移除该评论
      comments.value = comments.value.filter(c => c.id !== comment.id)
      ElMessage.success('删除成功')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除评论失败')
    }
  }
}

onMounted(async () => {
  await fetchVideoDetail()
})

onUnmounted(() => {
  if (videoPlayer.value) {
    videoPlayer.value.pause()
  }
})
</script>

<style scoped>
.video-detail {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 20px;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.side-content {
  width: 300px;
  flex-shrink: 0;
}

.video-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
}

.video-player {
  position: relative;
  width: 100%;
  background: #000;
  aspect-ratio: 16/9;
}

.video-element {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.video-info {
  padding: 20px;
}

.video-title {
  margin: 0 0 20px;
  font-size: 24px;
  font-weight: 600;
}

.video-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-detail {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-size: 16px;
  font-weight: 500;
}

.publish-time {
  font-size: 14px;
  color: #909399;
}

.video-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
}

.video-actions {
  margin-bottom: 20px;
}

.video-description {
  margin-bottom: 20px;
}

.video-description h3 {
  margin: 0 0 10px;
  font-size: 18px;
}

.video-description p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.course-info {
  margin-top: 20px;
}

.course-info h3 {
  margin: 0 0 10px;
  font-size: 18px;
}

/* 评论区样式 */
.comment-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.comment-section h3 {
  margin: 0 0 20px;
  font-size: 18px;
}

.comment-input {
  margin-bottom: 20px;
}

.comment-input .el-button {
  margin-top: 10px;
  float: right;
}

.comment-list {
  margin-top: 20px;
}

.no-comments {
  text-align: center;
  color: #909399;
  padding: 20px;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  font-weight: bold;
  color: #333;
}

.time {
  color: #909399;
  font-size: 12px;
}

.text {
  margin: 5px 0;
  color: #606266;
}

.actions {
  display: flex;
  gap: 15px;
}

.delete-btn {
  color: #f56c6c;
  padding: 0;
  font-size: 12px;
}

.delete-btn:hover {
  color: #f78989;
}

/* 相关视频列表样式 */
.related-videos {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.related-videos h3 {
  margin: 0 0 20px;
  font-size: 18px;
}

.no-videos {
  text-align: center;
  color: #909399;
  padding: 20px;
}

.video-item {
  cursor: pointer;
  margin-bottom: 15px;
  border-radius: 4px;
  overflow: hidden;
  transition: transform 0.2s;
}

.video-item:hover {
  transform: translateY(-2px);
}

.video-cover {
  position: relative;
  width: 100%;
  padding-top: 56.25%;
  background: #f5f5f5;
}

.video-cover .el-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.duration {
  position: absolute;
  bottom: 5px;
  right: 5px;
  padding: 2px 4px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  border-radius: 2px;
  font-size: 12px;
}

.video-item .video-info {
  padding: 10px 0;
}

.video-item .title {
  margin: 0 0 5px;
  font-size: 14px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.video-item .meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  z-index: 1;
}

.loading-overlay .el-icon {
  font-size: 30px;
  margin-bottom: 10px;
}

.loading-overlay span {
  margin-top: 10px;
}

.error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  z-index: 1;
}

.error-overlay span {
  margin-top: 10px;
}
</style> 