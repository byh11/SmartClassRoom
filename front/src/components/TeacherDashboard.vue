<template>
  <div class="container">
    <h2 class="title">教师端</h2>
    <div class="search-container">
      <el-input
          v-model="searchVideoName"
          class="search-input"
          placeholder="请输入视频名称查询"
      />
      <el-button class="search-button" type="primary" @click="fetchVideos">查询视频</el-button>
    </div>
    <el-upload
        :before-upload="beforeUpload"
        :show-file-list="false"
        accept="video/*"
        class="upload-button"
    >
      <el-button>上传视频</el-button>
    </el-upload>
    <el-table v-if="!isVideoPlaying" :data="filteredVideos" class="video-table">
      <el-table-column label="视频标题" prop="videoname"/>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button class="action-button" type="danger" @click="confirmDeleteVideo(row.id)">删除</el-button>
          <el-button class="action-button" type="info" @click="playVideo(row.url)">播放</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="recording-buttons">
      <el-button class="record-button" type="success" @click="startRecording">上课</el-button>
      <el-button :disabled="!isRecording" class="record-button" type="danger" @click="stopRecording">下课</el-button>
    </div>
    <video v-if="isRecording" ref="videoElement" autoplay class="video-preview"></video>
  </div>
  <Footer/>
</template>

<style scoped>
.container {
  padding: 40px;
  background: linear-gradient(to bottom right, #e0f7fa, #ffffff);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 2rem;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.search-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.search-input {
  width: 250px;
  margin-right: 10px;
}

.search-button {
  height: 40px;
}

.upload-button {
  margin-bottom: 20px;
}

.video-table {
  margin-top: 20px;
  border-radius: 10px;
  overflow: hidden;
}

.video-table .el-table {
  background-color: #fff;
}

.video-table .el-table th,
.video-table .el-table td {
  border-bottom: 1px solid #eaeaea;
}

.action-button {
  margin-right: 10px;
}

.recording-buttons {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.record-button {
  width: 100px;
  margin: 0 10px;
}

.video-preview {
  margin-top: 20px;
  max-width: 100%;
  border: 1px solid #ddd;
  border-radius: 8px;
}
</style>


<script>
import {computed, onMounted, ref} from 'vue';
import {useUserStore} from '../store/userStore';
import {mockApi} from '../api';
import {useRouter} from 'vue-router';
import {ElMessage, ElMessageBox} from 'element-plus';
import Footer from './MyFooter.vue';

export default {
  components: {
    Footer
  },
  setup() {
    const userStore = useUserStore();
    const router = useRouter();
    const videos = ref([]);
    const isRecording = ref(false);
    const videoElement = ref(null);
    const searchVideoName = ref('');
    const videoTitle = ref('');
    const isVideoPlaying = ref(false);
    let mediaRecorder;
    let recordedChunks = [];
    let mediaStream;

    if (userStore.user?.type !== 'teacher') {
      router.push('/');
    }

    const fetchVideos = async () => {
      const teacherId = userStore.user.teacherId;
      const response = await mockApi.selectVideoAll(10, 1);
      if (response.code === 200) {
        videos.value = response.data.filter(video => video.teacherid === teacherId);
        ElMessage.success('视频查询成功');
      } else {
        ElMessage.error('视频查询失败');
      }
    };

    onMounted(() => {
      fetchVideos();
    });

    const filteredVideos = computed(() => {
      if (!searchVideoName.value) {
        return videos.value;
      }
      return videos.value.filter(video =>
          video.videoname.toLowerCase().includes(searchVideoName.value.toLowerCase())
      );
    });

    const beforeUpload = (file) => {
      handleFileChange(file);
      return false;
    };

    const handleFileChange = async (file) => {
      if (!file) return;
      await uploadVideo(file);
    };

    const uploadVideo = async (file, title) => {
      const videoData = {
        id: Date.now(),
        videoname: title,
        url: URL.createObjectURL(file),
      };
      const response = await mockApi.uploadVideo(file, userStore.user.teacherId);
      if (response.code === 200) {
        userStore.addVideo(videoData);
        videos.value.push(videoData);
        videoTitle.value = '';
        ElMessage.success('视频上传成功');
      } else {
        ElMessage.error('视频上传失败');
      }
    };

    const deleteVideo = async (id) => {
      await mockApi.deleteVideo(id);
      videos.value = videos.value.filter(video => video.id !== id);
      ElMessage.success('视频删除成功');
    };

    const confirmDeleteVideo = (id) => {
      ElMessageBox.confirm('确认删除此视频吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
          .then(() => {
            deleteVideo(id);
          })
          .catch(() => {
            ElMessage.info('已取消删除');
          });
    };

    const startRecording = async () => {
      const {value: clazzName} = await ElMessageBox.prompt('请输入班级名称', '上课', {
        confirmButtonText: '开始',
        cancelButtonText: '取消',
      });

      if (!clazzName) return;

      const teacherId = userStore.user.teacherId;
      const response = await mockApi.teacherAttendClazz(teacherId, clazzName);
      if (response.code === 200) {
        console.log(response.message);
      }

      isRecording.value = true;
      mediaStream = await navigator.mediaDevices.getUserMedia({video: true, audio: true});
      videoElement.value.srcObject = mediaStream;

      mediaRecorder = new MediaRecorder(mediaStream);
      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          recordedChunks.push(event.data);
        }
      };
      mediaRecorder.start();
    };

    const stopRecording = async () => {
      isRecording.value = false;
      mediaRecorder.stop();
      mediaRecorder.onstop = async () => {
        const blob = new Blob(recordedChunks, {type: 'video/webm'});
        recordedChunks = [];

        const {value: title} = await ElMessageBox.prompt('请输入视频名称', '视频上传', {
          confirmButtonText: '上传',
          cancelButtonText: '取消',
          inputValue: videoTitle.value,
        });

        if (title) {
          await uploadVideo(blob, title);
        }

        if (mediaStream) {
          mediaStream.getTracks().forEach(track => track.stop());
        }

        const teacherId = userStore.user.teacherId;
        const finishResponse = await mockApi.teacherFinishClazz(teacherId);
        if (finishResponse.code === 200) {
          console.log(finishResponse.message);
        }
      };
    };

    const playVideo = (url) => {
      isVideoPlaying.value = true;

      const video = document.createElement('video');
      video.src = url;
      video.controls = true;
      video.style.width = '80%';
      video.style.zIndex = '10';

      const fullScreenContainer = document.createElement('div');
      fullScreenContainer.style.position = 'fixed';
      fullScreenContainer.style.top = '0';
      fullScreenContainer.style.left = '0';
      fullScreenContainer.style.width = '100%';
      fullScreenContainer.style.height = '100%';
      fullScreenContainer.style.backgroundColor = 'rgba(0, 0, 0, 0.9)';
      fullScreenContainer.style.display = 'flex';
      fullScreenContainer.style.alignItems = 'center';
      fullScreenContainer.style.justifyContent = 'center';
      fullScreenContainer.style.flexDirection = 'column';
      fullScreenContainer.style.zIndex = '1000';

      const exitButton = document.createElement('button');
      exitButton.textContent = '退出播放';
      exitButton.style.position = 'absolute';
      exitButton.style.top = '20px';
      exitButton.style.right = '20px';
      exitButton.style.padding = '10px 20px';
      exitButton.style.fontSize = '16px';
      exitButton.style.backgroundColor = 'red';
      exitButton.style.color = 'white';
      exitButton.style.border = 'none';
      exitButton.style.borderRadius = '5px';
      exitButton.style.cursor = 'pointer';
      exitButton.style.zIndex = '1001';

      exitButton.onclick = () => {
        document.body.removeChild(fullScreenContainer);
        isVideoPlaying.value = false;
      };

      fullScreenContainer.appendChild(video);
      fullScreenContainer.appendChild(exitButton);
      document.body.appendChild(fullScreenContainer);

      video.play();

      video.onended = () => {
        document.body.removeChild(fullScreenContainer);
        isVideoPlaying.value = false;
      };

      fullScreenContainer.onclick = (event) => {
        if (event.target === fullScreenContainer) {
          document.body.removeChild(fullScreenContainer);
          isVideoPlaying.value = false;
        }
      };
    };

    return {
      videos,
      fetchVideos,
      handleFileChange,
      deleteVideo,
      startRecording,
      stopRecording,
      isRecording,
      videoElement,
      searchVideoName,
      videoTitle,
      confirmDeleteVideo,
      filteredVideos,
      playVideo,
      isVideoPlaying,
      beforeUpload,
    };
  },
};
</script>

