<template>
  <div class="video-list">
    <h1>学生端</h1>
    <h2 class="welcome-message">{{ welcomeMessage }}</h2>
    <div class="search-container">
      <video-search v-model="searchQuery" @search="searchVideos"/>
    </div>
    <el-table :data="filteredVideos" class="video-table" style="width: 100%">
      <el-table-column label="视频标题" prop="videoname"/>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button class="play-button" size="small" type="primary" @click="playVideo(scope.row.url)">观看</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <Footer/>
</template>

<script>
import {computed, onMounted, ref} from 'vue';
import VideoSearch from './VideoSearch.vue';
import {useUserStore} from '../store/userStore';
import {useRouter} from 'vue-router';
import {mockApi} from '../api';
import Footer from './MyFooter.vue';

export default {
  components: {
    VideoSearch,
    Footer
  },
  setup() {
    const userStore = useUserStore();
    const router = useRouter();

    if (userStore.user?.type !== 'student') {
      router.push('/');
    }

    const searchQuery = ref('');
    const videos = ref([]);
    const welcomeMessage = ref(`欢迎回来, ${userStore.user?.name || '同学'}!`);

    const loadVideos = async () => {
      const response = await mockApi.selectVideoAll(10, 1);
      if (response.code === 200) {
        videos.value = response.data;
      } else {
        alert(response.message);
      }
    };

    const filteredVideos = computed(() => {
      return videos.value.filter(video =>
          video.videoname.toLowerCase().includes(searchQuery.value.toLowerCase())
      );
    });

    const searchVideos = (query) => {
      searchQuery.value = query;
    };

    const playVideo = (url) => {
      const videoWindow = window.open('', '_blank');
      videoWindow.document.write(`
        <html>
          <head>
            <title>观看视频</title>
            <style>
              body { margin: 0; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f0f0f0; }
              video { width: 100%; max-width: 800px; }
            </style>
          </head>
          <body>
            <video controls autoplay>
              <source src="${url}" type="video/mp4">
              您的浏览器不支持 HTML5 视频标签。
            </video>
          </body>
        </html>
      `);
      videoWindow.document.close();
    };

    onMounted(() => {
      loadVideos();
    });

    return {filteredVideos, searchQuery, searchVideos, playVideo, welcomeMessage};
  },
};
</script>

<style scoped>
.video-list {
  padding: 40px;
  background: linear-gradient(to bottom right, #e0eafc, #cfdef3);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  max-width: 1600px;
  margin: auto;
}

.welcome-message {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.search-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.video-table {
  margin-top: 20px;
}

.video-table .el-table {
  border-radius: 10px;
  overflow: hidden;
}

.video-table .el-table th, .video-table .el-table td {
  background-color: #fff;
  border-bottom: 1px solid #eaeaea;
}

.play-button {
  background-color: #409eff;
  color: #fff;
  border-radius: 20px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.play-button:hover {
  background-color: #66b1ff;
  transform: translateY(-2px);
}

/* 新增搜索框样式 */
.video-search {
  width: 100%;
}

.video-search input {
  width: 300px;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 25px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: border-color 0.3s;
}

.video-search input:focus {
  border-color: #409eff;
  outline: none;
}
</style>
