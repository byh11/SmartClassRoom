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
                <el-icon><Collection/></el-icon>
                {{ formatNumber(videoInfo.collectNum || 0) }}
              </span>
              <span class="stat-item">
                <el-icon><ChatDotRound/></el-icon>
                {{ formatNumber(comments.length || 0) }}
              </span>
            </div>
          </div>

          <div class="video-actions">
            <el-button-group>
              <el-button
                  :type="videoInfo.isLiked ? 'primary' : 'default'"
                  @click="handleLike"
              >
                {{ videoInfo.isLiked ? '取消点赞' : '点赞' }}
              </el-button>
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
            <div class="comment-submit">
              <el-button type="primary" @click="handleComment">发表评论</el-button>
            </div>
          </div>
          <div class="comment-list">
            <div v-if="comments.length === 0" class="no-comments">
              暂无评论
            </div>
            <div v-else class="comment-items">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <el-avatar :src="comment.avatar"/>
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="username">{{ comment.userName }}</span>
                    <div class="comment-actions">
                      <span class="time">{{ formatDate(comment.createTime) }}</span>
                      <el-button
                          v-if="comment.userid === (store.state.user.studentid || store.state.user.teacherid)"
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
                    <el-button type="text" @click="handleReply(comment)">
                      回复
                    </el-button>
                  </div>
                  <!-- 回复列表 -->
                  <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
                    <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                      <el-avatar :size="30" :src="defaultAvatar"/>
                      <div class="reply-content">
                        <div class="reply-header">
                          <span class="username">{{ reply.username }}</span>
                          <span class="time">{{ formatDate(reply.createTime) }}</span>
                        </div>
                        <p class="text">{{ reply.content }}</p>
                      </div>
                    </div>
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
              <el-image :src="video.coverUrl" fit="cover"/>
              <span class="duration">{{ video.duration }}</span>
            </div>
            <div class="video-info">
              <h4 class="title">{{ video.videoName }}</h4>
              <div class="meta">
                <span class="views">{{ video.views }} 播放</span>
                <span class="time">{{ formatDate(video.startTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 回复对话框 -->
    <el-dialog
        v-model="replyDialogVisible"
        title="回复评论"
        width="500px"
        @close="handleDialogClose"
    >
      <el-form>
        <el-form-item>
          <el-input
              v-model="replyContent"
              :maxlength="200"
              :rows="4"
              placeholder="写下你的回复..."
              show-word-limit
              type="textarea"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply(currentComment)">
            回复
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {onMounted, onUnmounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useStore} from 'vuex'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ChatDotRound, Collection, Loading, Star, View, Warning} from '@element-plus/icons-vue'
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
const activeReplyId = ref(null)
const replyContent = ref('')
const replyDialogVisible = ref(false)
const currentComment = ref(null)

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
const handlePlay = async () => {
  console.log('视频开始播放')
  // 发送播放请求
  try {
    let userid = store.state.user.studentid
    let userType = 1 // 学生
    if (!userid) {
      userid = store.state.user.teacherid
      userType = 0 // 教师
    }
    await api.addPlayRecord(String(videoInfo.value.videoid))
  } catch (error) {
    console.error('记录播放失败:', error)
  }
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
      // 获取点赞和收藏状态
      await fetchVideoStatus()
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
    if (response.data && response.data.data) {
      // 过滤掉当前视频
      relatedVideos.value = response.data.data.filter(v => String(v.videoid) !== String(videoInfo.value.videoid))
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
      // 处理评论数据，将回复添加到对应评论中
      const commentsMap = {}
      const rootComments = []

      response.data.data.forEach(comment => {
        if (comment.parentid) {
          // 如果是回复，添加到对应评论的 replies 数组中
          if (!commentsMap[comment.parentid]) {
            commentsMap[comment.parentid] = {replies: []}
          }
          commentsMap[comment.parentid].replies.push({
            id: comment.commentid,
            username: comment.username,
            content: comment.content,
            createTime: comment.createTime
          })
        } else {
          // 如果是根评论，添加到根评论数组
          comment.replies = []
          rootComments.push(comment)
          commentsMap[comment.commentid] = comment
        }
      })

      // 将回复关联到对应的评论
      rootComments.forEach(comment => {
        if (commentsMap[comment.commentid] && commentsMap[comment.commentid].replies) {
          comment.replies = commentsMap[comment.commentid].replies
        }
      })

      comments.value = rootComments
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
    let userType = 1 // 学生
    if (!userid) {
      userid = store.state.user.teacherid
      userType = 0 // 教师
    }
    const response = await api.addComment(userid, videoInfo.value.videoid, {
      content: newComment.value
    }, userType)
    if (response && response.data) {
      // 重新获取评论列表
      await fetchComments()
      newComment.value = ''
      ElMessage.success('评论成功')
    }
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

// 处理评论回复
const handleReply = (comment) => {
  currentComment.value = comment
  replyContent.value = ''
  replyDialogVisible.value = true
}

// 处理对话框关闭
const handleDialogClose = () => {
  replyContent.value = ''
  currentComment.value = null
}

// 提交回复
const submitReply = async (comment) => {
  if (!store.state.user.isLoggedIn) {
    router.push('/login')
    return
  }

  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  try {
    // 获取用户id
    let userid = store.state.user.studentid
    let userType = 1 // 学生
    if (!userid) {
      userid = store.state.user.teacherid
      userType = 0 // 教师
    }

    const response = await api.addReply(userid, videoInfo.value.videoid, comment.commentid, replyContent.value, userType)
    if (response.data.code === 200) {
      // 重新获取评论列表
      await fetchComments()
      ElMessage.success('回复成功')
      replyDialogVisible.value = false
      replyContent.value = ''
      currentComment.value = null
    }
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败')
  }
}

// 处理视频切换
const handleVideoChange = async (videoId) => {
  try {
    // 先停止当前视频播放
    if (videoPlayer.value) {
      videoPlayer.value.pause()
    }
    // 重置状态
    loading.value = true
    error.value = ''
    // 显示加载提示
    ElMessage({
      message: '正在加载视频...',
      type: 'success',
      duration: 2000
    })

    // 先获取新视频的详情
    const response = await api.getVideoDetail(videoId)
    if (response && response.data) {
      // 确保视频URL存在
      if (!response.data.data.url) {
        error.value = '视频地址不存在'
        ElMessage.error('视频地址不存在')
        return
      }
      // 更新视频信息
      videoInfo.value = response.data.data
      // 使用replace而不是push，避免历史记录堆积
      router.replace(`/video/${videoId}`)
      // 获取相关视频
      await fetchRelatedVideos()
      // 获取评论列表
      await fetchComments()
      // 获取点赞和收藏状态
      await fetchVideoStatus()
      // 发送播放请求
      try {
        let userid = store.state.user.studentid
        let userType = 1 // 学生
        if (!userid) {
          userid = store.state.user.teacherid
          userType = 0 // 教师
        }
        await api.addPlayRecord(String(videoId))
      } catch (error) {
        console.error('记录播放失败:', error)
      }
    } else {
      error.value = '获取视频信息失败：数据格式错误'
      ElMessage.error('获取视频信息失败：数据格式错误')
    }
  } catch (error) {
    console.error('切换视频失败:', error)
    ElMessage.error('切换视频失败')
  } finally {
    loading.value = false
  }
}

// 处理收藏
const handleCollect = async () => {
  if (!store.state.user.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    let userid = store.state.user.studentid
    let userType = 1 // 学生
    if (!userid) {
      userid = store.state.user.teacherid
      userType = 0 // 教师
    }
    let response
    if (videoInfo.value.isCollected) {
      response = await api.uncollectVideo(userid, String(videoInfo.value.videoid), userType)
      if (response.data.code === 200) {
        videoInfo.value.isCollected = false
        videoInfo.value.collectNum = (parseInt(videoInfo.value.collectNum) - 1).toString()
        ElMessage.success('取消收藏成功')
      }
    } else {
      response = await api.collectVideo(userid, String(videoInfo.value.videoid), userType)
      if (response.data.code === 200) {
        videoInfo.value.isCollected = true
        videoInfo.value.collectNum = (parseInt(videoInfo.value.collectNum) + 1).toString()
        ElMessage.success('收藏成功')
      }
    }
  } catch (error) {
    ElMessage.error(videoInfo.value.isCollected ? '取消收藏失败' : '收藏失败')
  }
}

// 处理删除评论
const handleDeleteComment = async (comment) => {
  try {
    // 检查是否是评论的发布者
    const currentUserId = store.state.user.studentid || store.state.user.teacherid
    if (comment.userid !== currentUserId) {
      ElMessage.error('您没有权限删除此评论')
      return
    }

    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await api.deleteComment(comment.commentid)
    if (response && response.data) {
      // 从评论列表中移除该评论
      comments.value = comments.value.filter(c => c.commentid !== comment.commentid)
      ElMessage.success('删除成功')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除评论失败')
    }
  }
}

// 处理点赞
const handleLike = async () => {
  if (!store.state.user.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    let userid = store.state.user.studentid
    let userType = 1 // 学生
    if (!userid) {
      userid = store.state.user.teacherid
      userType = 0 // 教师
    }
    let response
    if (videoInfo.value.isLiked) {
      response = await api.unlikeVideo(userid, String(videoInfo.value.videoid), userType)
      if (response.data.code === 200) {
        videoInfo.value.isLiked = false
        videoInfo.value.likeNum = (parseInt(videoInfo.value.likeNum) - 1).toString()
        ElMessage.success('取消点赞成功')
      }
    } else {
      response = await api.likeVideo(userid, String(videoInfo.value.videoid), userType)
      if (response.data.code === 200) {
        videoInfo.value.isLiked = true
        videoInfo.value.likeNum = (parseInt(videoInfo.value.likeNum) + 1).toString()
        ElMessage.success('点赞成功')
      }
    }
  } catch (error) {
    ElMessage.error(videoInfo.value.isLiked ? '取消点赞失败' : '点赞失败')
  }
}

// 获取视频点赞和收藏状态
const fetchVideoStatus = async () => {
  if (!store.state.user.isLoggedIn) {
    return
  }

  try {
    let userid = store.state.user.studentid
    let userType = 1 // 学生
    if (!userid) {
      userid = store.state.user.teacherid
      userType = 0 // 教师
    }

    const response = await api.getVideoStatus(userid, String(videoInfo.value.videoid), userType)
    if (response.data && response.data.code === 200) {
      videoInfo.value.isLiked = response.data.data.isLiked
      videoInfo.value.isCollected = response.data.data.isCollected
    }
  } catch (error) {
    console.error('获取视频状态失败:', error)
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

.comment-submit {
  margin-top: 10px;
  text-align: right;
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
  position: relative;
}

.comment-content {
  flex: 1;
  padding-right: 60px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
  position: relative;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  position: absolute;
  right: 0;
  top: 0;
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

.reply-list {
  margin-top: 10px;
  padding-left: 20px;
  border-left: 2px solid #eee;
}

.reply-item {
  display: flex;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.reply-content {
  flex: 1;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.reply-form {
  margin-top: 10px;
  padding-left: 20px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}
</style> 