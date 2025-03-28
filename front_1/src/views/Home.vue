<template>
  <div class="home">
    <!-- 轮播图 -->
    <el-carousel class="carousel" height="400px">
      <el-carousel-item v-for="item in carouselData" :key="item.id">
        <div class="carousel-content" @click="watchVideo(item)">
          <img :alt="item.title" :src="item.image">
          <div class="carousel-info">
            <h3>{{ item.title }}</h3>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 内容区域 -->
    <div class="content-section container">
      <el-row :gutter="20">
        <el-col :span="16">
          <!-- 校园资讯 -->
          <div class="section">
            <div class="section-header">
              <h2>校园资讯</h2>
              <el-button text>查看更多</el-button>
            </div>
            <el-card v-for="news in newsData" :key="news.id" class="news-item">
              <h3>{{ news.title }}</h3>
              <p>{{ news.summary }}</p>
              <span class="news-date">{{ news.date }}</span>
            </el-card>
          </div>

          <!-- 最新技术 -->
          <div class="section">
            <div class="section-header">
              <h2>最新技术</h2>
              <el-button text>查看更多</el-button>
            </div>
            <el-card v-for="tech in techData" :key="tech.id" class="tech-item">
              <h3>{{ tech.title }}</h3>
              <p>{{ tech.description }}</p>
            </el-card>
          </div>
        </el-col>

        <el-col :span="8">
          <!-- 热门视频 -->
          <div class="section">
            <div class="section-header">
              <h2>热门视频</h2>
              <el-button text>查看更多</el-button>
            </div>
            <VideoCard
                v-for="video in popularVideos"
                :key="video.videoid"
                :video="video"
                @click="watchVideo(video)"
            />
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import {useStore} from 'vuex'
import {ElMessage} from 'element-plus'
import VideoCard from '@/components/VideoCard.vue'

const router = useRouter()
const store = useStore()

// 模拟数据
const carouselData = ref([
  {
    id: 1,
    title: '2025年度教学成果展示',
    image: 'https://tse1-mm.cn.bing.net/th/id/OIP-C.0vYuN7Pt8V6rlgjHgjrx5QHaFj?w=233&h=180&c=7&r=0&o=5&pid=1.7',
    videoId: '1'
  },
  {
    id: 2,
    title: '智慧课堂应用实践',
    image: 'https://tse3-mm.cn.bing.net/th/id/OIP-C.SzJOVTBQneYAgfarjFH9nAHaFC?w=222&h=180&c=7&r=0&o=5&pid=1.7',
    videoId: '2'
  },
  {
    id: 3,
    title: '在线教育创新发展',
    image: 'https://tse2-mm.cn.bing.net/th/id/OIP-C.vm-izF8HO7DncE6sHNUxDQAAAA?rs=1&pid=ImgDetMain',
    videoId: '3'
  }
])

const newsData = ref([
  {
    id: 1,
    title: '我校举办2023年教育信息化研讨会',
    summary: '为推进教育信息化建设，提升教学质量，我校于近日举办...',
    date: '2023-12-01'
  },
  {
    id: 2,
    title: '智慧教室建设项目正式启动',
    summary: '为创建现代化教学环境，提升教学效果，我校智慧教室建设项目正式启动...',
    date: '2023-11-28'
  }
])

const techData = ref([
  {
    id: 1,
    title: '人工智能在教育中的应用',
    description: '探讨AI技术如何改变传统教学模式，提升教学效果...'
  },
  {
    id: 2,
    title: '虚拟现实技术与教育创新',
    description: 'VR技术为教育带来新的可能，创造沉浸式学习体验...'
  }
])

const popularVideos = ref([
  {
    videoid: 1,
    videoidName: '计算机网络基础教程',
    coverUrl: 'https://th.bing.com/th?&id=OVP.wSwCT0VUYwTucEz9TBtuBgIIFF&w=326&h=183&c=7&pid=2.1&rs=1',
    duration: 3600,
    views: 1500,
    likeNum: 120,
    collectNum: 30,
    teacherName: '张教授',
    teacherAvatar: 'https://via.placeholder.com/40'
  },
  {
    videoid: 2,
    videoidName: 'Python程序设计入门',
    coverUrl: 'https://th.bing.com/th?&id=OVP.tnkFppy-gHnHoioANSnBgwIIGG&w=326&h=183&c=7&pid=2.1&rs=1',
    duration: 2700,
    views: 1200,
    likeNum: 100,
    collectNum: 25,
    teacherName: '李老师',
    teacherAvatar: 'https://via.placeholder.com/40'
  }
])

// 观看视频
const watchVideo = (item) => {
  if (!store.state.user.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push(`/video/${item.videoId || item.id}`)
}
</script>

<style scoped>
.home {
  min-height: 100vh;
}

.carousel {
  margin-bottom: 30px;
  background: #f5f5f5;
}

.carousel-content {
  height: 100%;
  cursor: pointer;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f5f5;
  overflow: hidden;
}

.carousel-content img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.carousel-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;
  z-index: 1;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.news-item, .tech-item {
  margin-bottom: 15px;
}

.news-item h3, .tech-item h3 {
  margin-bottom: 10px;
  font-size: 18px;
}

.news-date {
  color: #999;
  font-size: 14px;
}
</style> 