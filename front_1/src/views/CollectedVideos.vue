<template>
  <div class="collected-videos-page container">
    <div class="page-header">
      <h2>我收藏的视频</h2>
    </div>

    <!-- 视频列表 -->
    <el-row :gutter="20">
      <el-col
          v-for="video in videoList"
          :key="video.videoid"
          :lg="6"
          :md="8"
          :sm="12"
          :xs="24"
      >
        <VideoCard
            :video="video"
            @click="handlePlay(video)"
        />
      </el-col>
    </el-row>

    <!-- 无数据提示 -->
    <el-empty
        v-if="!loading && (!videoList || videoList.length === 0)"
        description="暂无收藏的视频"
    />
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useStore} from 'vuex'
import {ElMessage} from 'element-plus'
import VideoCard from '@/components/VideoCard.vue'
import api from '@/api'

const router = useRouter()
const store = useStore()

const videoList = ref([])
const loading = ref(false)

// 获取收藏的视频列表
const fetchCollectedVideos = async () => {
  loading.value = true
  try {
    let userid = store.state.user.studentid
    let userType = 1
    if (!userid) {
      userid = store.state.user.teacherid
      userType = 0
    }
    if (!userid) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const res = await api.getCollectedVideos(userid, userType)
    if (res.data.code === 200) {
      // 转换数据格式以适配 VideoCard 组件
      videoList.value = res.data.data.map(video => ({
        videoid: video.videoid,
        videoidName: video.videoName,
        coverUrl: video.coverUrl,
        duration: video.duration,
        views: parseInt(video.views),
        likeNum: parseInt(video.likeNum),
        collectNum: parseInt(video.collectNum),
        teacherName: video.teacherName,
        teacherAvatar: video.teacherAvatar,
        description: video.videoText,
        videoType: video.videoType,
        className: video.className,
        startTime: video.startTime,
        endTime: video.endTime
      }))
    } else {
      ElMessage.error(res.data.message || '获取收藏的视频列表失败')
    }
  } catch (error) {
    console.error('获取收藏的视频列表失败:', error)
    ElMessage.error('获取收藏的视频列表失败')
  } finally {
    loading.value = false
  }
}

const handlePlay = async (video) => {
  if (!store.state.user.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    // 添加播放记录
    await api.addPlayRecord(video.videoid).then(res => {
      if (res.data.code === 200) {
        console.log('添加播放记录成功')
      } else {
        console.error('添加播放记录失败:', res.data.message)
      }
    }).catch(error => {
      console.error('添加播放记录失败:', error)
    })
    // 跳转到视频详情页
    router.push({
      path: `/video/${video.videoid}`,
      query: {autoplay: 'true'}
    })
  } catch (error) {
    console.error('添加播放记录失败:', error)
    ElMessage.error('播放失败，请重试')
  }
}

onMounted(() => {
  fetchCollectedVideos()
})
</script>

<style scoped>
.collected-videos-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.el-col {
  margin-bottom: 20px;
}
</style> 