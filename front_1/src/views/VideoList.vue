<template>
  <div class="video-list-page container">
    <div class="page-header">
      <h2>视频列表</h2>
      <div class="search-section">
        <el-input
            v-model="searchQuery"
            clearable
            placeholder="搜索视频..."
            @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="filters.category" clearable placeholder="选择分类">
            <el-option
                v-for="item in categories"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filters.sort" placeholder="排序方式">
            <el-option label="最多播放" value="views"/>
            <el-option label="最多点赞" value="like_num"/>
            <el-option label="最多收藏" value="collect_num"/>
          </el-select>
        </el-col>
      </el-row>
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

    <!-- 加载更多 -->
    <div v-if="hasMore" class="load-more">
      <el-button
          :loading="loading"
          @click="loadMore"
      >
        加载更多
      </el-button>
    </div>

    <!-- 无数据提示 -->
    <el-empty
        v-if="!loading && (!videoList || videoList.length === 0)"
        description="暂无视频"
    />
  </div>
</template>

<script setup>
import {onMounted, reactive, ref, watch} from 'vue'
import {useRouter} from 'vue-router'
import {useStore} from 'vuex'
import {ElMessage} from 'element-plus'
import VideoCard from '@/components/VideoCard.vue'
import api from '@/api'

const router = useRouter()
const store = useStore()

const searchQuery = ref('')
const videoList = ref([])
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)
const pageSize = 12

const filters = reactive({
  category: '',
  sort: 'views',
  order: 'desc'
})

const categories = [
  {label: '全部课程', value: ''},
  {label: '计算机科学', value: '计算机科学'},
  {label: '软件工程', value: '软件工程'},
  {label: '数据科学', value: '数据科学'}
]

// 获取视频列表
const fetchVideos = async () => {
  loading.value = true
  try {
    const params = {
      pageNumber: page.value,
      pageSize: pageSize
    }

    let res
    if (searchQuery.value) {
      // 搜索视频
      res = await api.getVideoByName({
        ...params,
        name: searchQuery.value
      })
    } else if (filters.category) {
      // 按分类获取视频
      res = await api.getVideoByType({
        ...params,
        type: filters.category
      })
    } else if (filters.sort) {
      // 按字段排序获取视频
      res = await api.getVideoTopByField({
        ...params,
        field: filters.sort,
        order: filters.order
      })
    } else {
      // 获取所有视频
      res = await api.getVideoList(params)
    }

    if (res.data.code === 200) {
      // 转换数据格式以适配 VideoCard 组件
      const formattedVideos = res.data.data.map(video => ({
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

      if (page.value === 1) {
        videoList.value = formattedVideos
      } else {
        videoList.value = [...videoList.value, ...formattedVideos]
      }
      hasMore.value = formattedVideos.length === pageSize
    } else {
      ElMessage.error(res.data.message || '获取视频列表失败')
    }
  } catch (error) {
    console.error('获取视频列表失败:', error)
    ElMessage.error('获取视频列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchVideos()
}

const loadMore = () => {
  page.value++
  fetchVideos()
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

// 监听筛选条件变化
watch([() => filters.category, () => filters.sort], () => {
  page.value = 1
  fetchVideos()
})

onMounted(() => {
  fetchVideos()
})
</script>

<style scoped>
.video-list-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-section {
  width: 300px;
}

.filter-section {
  margin-bottom: 20px;
}

.el-col {
  margin-bottom: 20px;
}

.load-more {
  text-align: center;
  margin-top: 20px;
}
</style> 