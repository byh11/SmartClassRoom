<template>
  <div class="video-management">
    <div class="header">
      <h2>视频管理</h2>
      <el-button type="primary" @click="showUploadDialog">
        上传视频
      </el-button>
    </div>

    <!-- 视频列表 -->
    <el-table
        v-loading="loading"
        :data="videoList"
        style="width: 100%"
    >
      <el-table-column label="视频标题" min-width="200" prop="title">
        <template #default="{ row }">
          <el-link @click="previewVideo(row)">{{ row.videoName }}</el-link>
        </template>
      </el-table-column>

      <el-table-column label="上传时间" prop="createTime" width="180">
        <template #default="{ row }">
          {{ formatTime(row.startTime) }}
        </template>
      </el-table-column>

      <el-table-column label="时长" prop="duration" width="120">
        <template #default="{ row }">
          {{ row.duration }}
        </template>
      </el-table-column>

      <el-table-column label="播放量" prop="views" width="120"/>

      <el-table-column label="点赞量" prop="likeNum" width="120"/>

      <el-table-column label="收藏量" prop="collectNum" width="120"/>

      <el-table-column label="状态" prop="status" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '已删除' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column fixed="right" label="操作" width="250">
        <template #default="{ row }">
          <el-button-group>
            <el-button
                link
                type="primary"
                @click="handleEdit(row)"
            >
              编辑
            </el-button>

            <el-button
                link
                type="primary"
                @click="handleDownload(row)"
            >
              下载
            </el-button>

            <el-button
                link
                type="danger"
                @click="handleDelete(row)"
            >
              删除
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>

    <!-- 上传视频对话框 -->
    <el-dialog
        v-model="uploadDialogVisible"
        title="上传视频"
        width="600px"
    >
      <el-form ref="uploadFormRef" :model="uploadForm" :rules="uploadRules">
        <el-form-item label="视频标题" prop="videoName">
          <el-input v-model="uploadForm.videoName"/>
        </el-form-item>

        <el-form-item label="视频描述" prop="videoText">
          <el-input
              v-model="uploadForm.videoText"
              :rows="3"
              type="textarea"
          />
        </el-form-item>

        <el-form-item label="视频分类" prop="videoType">
          <el-select v-model="uploadForm.videoType" placeholder="请选择分类">
            <el-option
                v-for="item in categories"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="视频文件" prop="file">
          <el-upload
              :auto-upload="false"
              :limit="1"
              :on-change="handleFileChange"
              accept="video/*"
              class="video-uploader"
          >
            <template #trigger>
              <el-button type="primary">选择视频</el-button>
            </template>
            <template #tip>
              <div class="el-upload__tip">
                支持mp4、mov等格式视频文件，大小不超过2GB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUploadSubmit">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑视频对话框 -->
    <el-dialog
        v-model="editDialogVisible"
        title="编辑视频"
        width="600px"
    >
      <el-form ref="editFormRef" :model="editForm" :rules="editRules">
        <el-form-item label="视频标题" prop="videoName">
          <el-input v-model="editForm.videoName"/>
        </el-form-item>

        <el-form-item label="视频描述" prop="videoText">
          <el-input
              v-model="editForm.videoText"
              :rows="3"
              type="textarea"
          />
        </el-form-item>

        <el-form-item label="视频分类" prop="videoType">
          <el-select v-model="editForm.videoType" placeholder="请选择分类">
            <el-option
                v-for="item in categories"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleEditSubmit">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 预览视频对话框 -->
    <el-dialog
        v-model="previewDialogVisible"
        custom-class="preview-dialog"
        title="视频预览"
        width="800px"
    >
      <video
          v-if="selectedVideo"
          :src="selectedVideo.url"
          controls
          style="width: 100%"
      ></video>
    </el-dialog>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import api from '@/api'
import {formatTime} from '@/utils/format'

// 数据
const loading = ref(false)
const videoList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 上传相关
const uploadDialogVisible = ref(false)
const uploadFormRef = ref(null)
const uploadProgress = ref(0)
const uploadStatus = ref('')
const uploadUrl = import.meta.env.VITE_API_BASE_URL + '/video/upload'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}
const uploadForm = ref({
  videoName: '',
  videoText: '',
  videoType: '',
  file: null,
  duration: 0
})

// 编辑相关
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editForm = ref({
  id: '',
  title: '',
  description: '',
  category: ''
})

// 预览相关
const previewDialogVisible = ref(false)
const selectedVideo = ref(null)

// 分类选项
const categories = [
  {label: '课程视频', value: '课程视频'},
  {label: '教学资料', value: '教学资料'},
  {label: '实验演示', value: '实验演示'}
]

// 表单验证规则
const uploadRules = {
  videoName: [
    {required: true, message: '请输入视频标题', trigger: 'blur'},
    {min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur'}
  ],
  videoText: [
    {required: true, message: '请输入视频描述', trigger: 'blur'}
  ],
  videoType: [
    {required: true, message: '请选择视频分类', trigger: 'change'}
  ]
}

const editRules = {...uploadRules}

// 获取视频列表
const fetchVideoList = async () => {
  loading.value = true
  try {
    const teacherId = localStorage.getItem('teacherid')
    const res = await api.getTeacherVideos(teacherId)
    videoList.value = res.data.data
    total.value = res.data.data.length
  } catch (error) {
    ElMessage.error('获取视频列表失败')
  } finally {
    loading.value = false
  }
}

// 上传相关方法
const showUploadDialog = () => {
  uploadDialogVisible.value = true
  uploadForm.value = {
    videoName: '',
    videoText: '',
    videoType: '',
    file: null,
    duration: 0
  }
  uploadProgress.value = 0
  uploadStatus.value = ''
}

const handleFileChange = (file) => {
  const isVideo = file.raw.type.startsWith('video/')
  const isLt2G = file.raw.size / 1024 / 1024 / 1024 < 2

  if (!isVideo) {
    ElMessage.error('请上传视频文件！')
    return false
  }
  if (!isLt2G) {
    ElMessage.error('视频大小不能超过 2GB！')
    return false
  }

  // 1. 创建临时 URL
  const url = URL.createObjectURL(file.raw)
  console.log('创建临时URL:', url)

  // 2. 创建 video 元素
  const video = document.createElement('video')
  video.preload = 'metadata'

  // 3. 监听 loadedmetadata 事件
  video.onloadedmetadata = () => {
    console.log('视频元数据加载完成')

    // 检查视频是否有效
    if (!video.videoWidth || !video.videoHeight) {
      console.error('视频无效：无法获取视频尺寸')
      ElMessage.warning('视频文件可能已损坏或格式不正确')
      uploadForm.value.duration = 0
      uploadForm.value.file = file.raw
      URL.revokeObjectURL(url)
      video.remove()
      return
    }

    // 4. 获取 duration
    let duration = 0
    try {
      // 尝试使用 MediaMetadata API
      if (navigator.mediaSession && navigator.mediaSession.metadata) {
        const metadata = new MediaMetadata({
          title: file.raw.name,
          artist: 'Unknown',
          album: 'Unknown',
          artwork: []
        })
        navigator.mediaSession.metadata = metadata
      }

      // 尝试获取视频时长
      if (video.duration && isFinite(video.duration)) {
        duration = video.duration
      } else {
        // 如果无法获取，尝试使用文件大小估算
        const fileSize = file.raw.size
        const bitrate = 2000000 // 假设2Mbps的比特率
        duration = Math.floor(fileSize * 8 / bitrate)
      }
    } catch (error) {
      console.error('获取视频时长失败:', error)
      // 使用文件大小估算
      const fileSize = file.raw.size
      const bitrate = 2000000
      duration = Math.floor(fileSize * 8 / bitrate)
    }

    console.log('计算得到的duration:', duration)

    // 5. 格式化显示
    if (duration > 0) {
      const roundedDuration = Math.floor(duration)
      console.log('处理后的duration:', roundedDuration)
      uploadForm.value.duration = roundedDuration
      console.log('设置duration:', uploadForm.value.duration)
    } else {
      console.error('无法获取有效的视频时长')
      ElMessage.warning('无法获取视频时长，请检查视频文件')
      uploadForm.value.duration = 0
    }

    // 6. 释放资源
    URL.revokeObjectURL(url)
    video.remove()

    // 设置文件
    uploadForm.value.file = file.raw
  }

  // 错误处理
  video.onerror = (error) => {
    console.error('视频加载错误:', error)
    ElMessage.warning('无法获取视频时长，请检查视频文件')
    uploadForm.value.duration = 0
    uploadForm.value.file = file.raw

    // 释放资源
    URL.revokeObjectURL(url)
    video.remove()
  }

  // 设置视频源
  video.src = url
  console.log('设置视频源:', url)

  return true
}

const handleUploadSubmit = async () => {
  if (!uploadFormRef.value) return

  try {
    await uploadFormRef.value.validate()
    if (!uploadForm.value.file) {
      ElMessage.warning('请选择要上传的视频文件')
      return
    }

    const formData = new FormData()
    formData.append('file', uploadForm.value.file)
    formData.append('videoName', uploadForm.value.videoName)
    formData.append('videoText', uploadForm.value.videoText)
    formData.append('videoType', uploadForm.value.videoType)
    formData.append('teacherid', localStorage.getItem('teacherid'))
    formData.append('duration', uploadForm.value.duration)

    uploadProgress.value = 0
    uploadStatus.value = 'processing'

    const res = await api.uploadVideo(localStorage.getItem('teacherid'), formData, {
      onUploadProgress: (progressEvent) => {
        uploadProgress.value = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
        )
      }
    })

    if (res.data.code === 200) {
      uploadStatus.value = 'success'
      ElMessage.success('视频上传成功')
      uploadDialogVisible.value = false
      fetchVideoList()
    } else {
      uploadStatus.value = 'error'
      ElMessage.error(res.message || '视频上传失败')
    }
  } catch (error) {
    uploadStatus.value = 'error'
    ElMessage.error(error.message || '视频上传失败')
  }
}

// 编辑相关方法
const handleEdit = (row) => {
  editForm.value = {...row}
  editDialogVisible.value = true
}

const handleEditSubmit = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
    await api.updateVideo(editForm.value.videoid, editForm.value)
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    fetchVideoList()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

// 其他操作方法
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该视频吗？', '提示', {
      type: 'warning'
    })
    await api.deleteVideo(row.id)
    ElMessage.success('删除成功')
    fetchVideoList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleDownload = async (row) => {
  try {
    const res = await api.getVideoDownloadUrl(row.videoid)
    if (res.data.data) {
      window.open(res.data.data, '_blank')
    } else {
      ElMessage.error('获取下载链接失败')
    }
  } catch (error) {
    ElMessage.error('获取下载链接失败')
  }
}


const previewVideo = (row) => {
  selectedVideo.value = row
  previewDialogVisible.value = true
}

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchVideoList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchVideoList()
}

onMounted(() => {
  fetchVideoList()
})
</script>

<style scoped>
.video-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.video-uploader {
  width: 100%;
}

:deep(.preview-dialog .el-dialog__body) {
  padding: 0;
  background: #000;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}
</style> 