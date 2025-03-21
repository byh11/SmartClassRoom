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
            <el-option label="最新发布" value="latest"/>
            <el-option label="最多播放" value="views"/>
            <el-option label="最多点赞" value="likes"/>
          </el-select>
        </el-col>
      </el-row>
    </div>

    <!-- 视频列表 -->
    <el-row :gutter="20">
      <el-col
          v-for="video in videoList"
          :key="video.id"
          :lg="6"
          :md="8"
          :sm="12"
          :xs="24"
      >
        <VideoCard
            :video="video"
            @click="handleVideoClick(video)"
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
        v-if="videoList.length === 0 && !loading"
        description="暂无视频"
    />
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
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
  sort: 'latest'
})

const categories = [
  {label: '全部课程', value: ''},
  {label: '计算机科学', value: 'cs'},
  {label: '软件工程', value: 'se'},
  {label: '数据科学', value: 'ds'}
]

// 获取视频列表
const fetchVideos = async () => {
  loading.value = true
  try {
    // 模拟数据
    const mockData = Array(pageSize).fill(null).map((_, index) => ({
      id: page.value * pageSize + index,
      title: `示例视频 ${page.value * pageSize + index}`,
      coverUrl: 'https://via.placeholder.com/300x200',
      duration: 300,
      views: 1000,
      likes: 100,
      comments: 50,
      teacher: {
        name: '示例教师',
        avatar: 'https://via.placeholder.com/40'
      }
    }))

    if (page.value === 1) {
      videoList.value = mockData
    } else {
      videoList.value = [...videoList.value, ...mockData]
    }

    hasMore.value = page.value < 3 // 模拟只有3页数据
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

const handleVideoClick = (video) => {
  if (!store.state.user.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push(`/video/${video.id}`)
}

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