<template>
  <div class="class-management">
    <div class="header">
      <h2>上课管理</h2>
      <div class="header-actions">
        <el-button
            :disabled="isRecording"
            type="primary"
            @click="startClass"
        >
          开始上课
        </el-button>
        <el-button
            :disabled="!isRecording"
            type="danger"
            @click="endClass"
        >
          下课
        </el-button>
      </div>
    </div>

    <!-- 录制状态显示 -->
    <div v-if="isRecording" class="recording-status">
      <el-alert
          :closable="false"
          :description="`已录制时长: ${formatRecordingDuration(recordingDuration)}`"
          show-icon
          title="正在录制中"
          type="warning"
      />

      <div class="recording-info">
        <p>班级：{{ currentClass }}</p>
        <p>开始时间：{{ formatTimeWithTimezone(recordingStartTime) }}</p>
      </div>
    </div>

    <!-- 视频预览 -->
    <div class="video-preview">
      <video
          ref="previewVideo"
          autoplay
          muted
          playsinline
          style="width: 100%; height: 100%; object-fit: cover;"
      ></video>
      <div v-if="isRecording" class="recording-duration">
        录制时长：{{ formatRecordingDuration(recordingDuration) }}
      </div>
    </div>

    <!-- 历史记录 -->
    <div class="class-history">
      <h3>上课记录</h3>
      <el-table
          v-loading="loading"
          :data="classRecords"
          style="width: 100%"
      >
        <el-table-column label="班级" prop="className" width="150"/>
        <el-table-column label="课程名称" prop="videoName" width="200"/>
        <el-table-column label="上课时间" prop="startTime" width="180">
          <template #default="{ row }">
            {{ formatTimeWithTimezone(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="下课时间" prop="endTime" width="180">
          <template #default="{ row }">
            {{ formatTimeWithTimezone(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="课程时长" prop="duration" width="120">
          <template #default="{ row }">
            {{ row.duration }}
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '已删除' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="200">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                  :disabled="row.status !== 1"
                  link
                  type="primary"
                  @click="viewRecording(row)"
              >
                查看回放
              </el-button>
              <el-button
                  :disabled="row.status !== 1"
                  link
                  type="primary"
                  @click="downloadRecording(row)"
              >
                下载
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
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            background
            layout="sizes, prev, pager, next, total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 开始上课对话框 -->
    <el-dialog
        v-model="startClassDialogVisible"
        title="开始上课"
        width="500px"
    >
      <el-form ref="startClassFormRef" :model="startClassForm" :rules="startClassRules">
        <el-form-item label="选择班级" prop="className">
          <el-select
              v-model="startClassForm.className"
              placeholder="请选择班级"
              style="width: 100%"
          >
            <el-option
                v-for="item in classList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="startClassDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleStartClass">
            确认开始
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 结束上课对话框 -->
    <el-dialog
        v-model="endClassDialogVisible"
        title="结束上课"
        width="500px"
    >
      <el-form ref="endClassFormRef" :model="endClassForm" :rules="endClassRules">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="endClassForm.courseName" placeholder="请输入本节课程名称"/>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="endClassDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleEndClass">
            确认结束
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {nextTick, onMounted, onUnmounted, ref} from 'vue'
import {ElMessage} from 'element-plus'
import api from '@/api'
import {useRouter} from 'vue-router'

// 数据
const loading = ref(false)
const isRecording = ref(false)
const recordingDuration = ref(0)
const recordingStartTime = ref(null)
const currentClass = ref('')
const classRecords = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const uploadProgress = ref(0)
const previewVideo = ref(null)

// 录制相关
let mediaRecorder = null
let recordedChunks = []
let durationTimer = null
let videoStream = null

// 对话框控制
const startClassDialogVisible = ref(false)
const endClassDialogVisible = ref(false)
const startClassFormRef = ref(null)
const endClassFormRef = ref(null)

// 表单数据
const startClassForm = ref({
  className: ''
})

const endClassForm = ref({
  courseName: ''
})

// 班级列表
const classList = [
  {label: '计算机科学1班', value: '计算机科学1班'},
  {label: '计算机科学2班', value: '计算机科学2班'},
  {label: '软件工程1班', value: '软件工程1班'},
  {label: '软件工程2班', value: '软件工程2班'},
  {label: '录制', value: '录制'}
]

// 表单验证规则
const startClassRules = {
  className: [
    {required: true, message: '请选择班级', trigger: 'change'}
  ]
}

const endClassRules = {
  courseName: [
    {required: true, message: '请输入课程名称', trigger: 'blur'},
    {min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur'}
  ]
}

// 获取上课记录
const fetchClassRecords = async () => {
  loading.value = true
  try {
    const teacherId = localStorage.getItem('teacherid')
    const res = await api.getTeacherVideos(teacherId)
    classRecords.value = res.data.data
    total.value = res.data.data.length
  } catch (error) {
    ElMessage.error('获取上课记录失败')
  } finally {
    loading.value = false
  }
}

// 开始录制
const startRecording = async () => {
  try {
    console.log('开始初始化录制...')

    // 确保视频元素已经挂载
    await nextTick()

    if (!previewVideo.value) {
      console.error('视频预览元素未找到')
      throw new Error('视频预览元素未找到')
    }

    // 请求摄像头和麦克风权限
    const mediaStream = await navigator.mediaDevices.getUserMedia({
      video: true,
      audio: true 
    })
    console.log('获取到媒体流:', mediaStream)

    // 设置视频预览
    console.log('设置视频预览...')
    previewVideo.value.srcObject = mediaStream
    try {
      await previewVideo.value.play()
      console.log('视频预览开始播放')
    } catch (playError) {
      console.error('视频预览播放失败:', playError)
      throw playError
    }

    // 检查浏览器支持的媒体类型
    const mimeType = 'video/webm;codecs=vp8,opus'
    if (!MediaRecorder.isTypeSupported(mimeType)) {
      console.warn('浏览器不支持 VP8/Opus 编码，尝试使用默认编码')
    }

    // 初始化录制器
    console.log('初始化 MediaRecorder...')
    mediaRecorder = new MediaRecorder(mediaStream, {
      mimeType: MediaRecorder.isTypeSupported(mimeType) ? mimeType : undefined
    })
    
    recordedChunks = []

    mediaRecorder.ondataavailable = (event) => {
      console.log('收到录制数据:', event.data.size, 'bytes')
      if (event.data.size > 0) {
        recordedChunks.push(event.data)
      }
    }

    mediaRecorder.onerror = (error) => {
      console.error('录制错误:', error)
      ElMessage.error('录制出现错误')
      stopRecording()
    }

    mediaRecorder.onstop = () => {
      console.log('录制已停止')
    }

    console.log('开始录制...')
    mediaRecorder.start(1000) // 每秒记录一次数据
    isRecording.value = true

    // 使用中国时区的时间
    const now = new Date()
    now.setHours(now.getHours() + 8)
    recordingStartTime.value = now
    recordingDuration.value = 0

    // 开始计时
    if (durationTimer) clearInterval(durationTimer)
    const startTimestamp = Date.now() // 记录开始时间戳
    durationTimer = setInterval(() => {
      recordingDuration.value = Date.now() - startTimestamp // 使用时间戳计算
    }, 1000)

    videoStream = mediaStream // 保存流的引用以便后续停止使用
    console.log('录制初始化完成，开始时间:', recordingStartTime.value.toISOString())

  } catch (error) {
    console.error('录制初始化失败:', error)
    ElMessage.error(error.message || '无法访问摄像头和麦克风')
    isRecording.value = false
    recordingStartTime.value = null
    recordingDuration.value = 0

    // 清理资源
    if (videoStream) {
      videoStream.getTracks().forEach(track => track.stop())
      videoStream = null
    }
    if (previewVideo.value) {
      previewVideo.value.srcObject = null
    }
  }
}

// 停止录制
const stopRecording = () => {
  try {
    console.log('开始停止录制...')

    if (durationTimer) {
      clearInterval(durationTimer)
      durationTimer = null
    }

    if (mediaRecorder && mediaRecorder.state !== 'inactive') {
      console.log('停止 MediaRecorder...')
      mediaRecorder.stop()
    }

    if (videoStream) {
      console.log('停止媒体流...')
      videoStream.getTracks().forEach(track => {
        track.stop()
        console.log(`停止${track.kind}轨道`)
      })
      videoStream = null
    }

    if (previewVideo.value) {
      console.log('清除视频预览...')
      previewVideo.value.srcObject = null
    }

    isRecording.value = false
    recordingStartTime.value = null
    recordingDuration.value = 0

    console.log('录制停止完成')

  } catch (error) {
    console.error('停止录制失败:', error)
    ElMessage.error('停止录制失败')
  }
}

// 开始上课
const startClass = () => {
  startClassDialogVisible.value = true
}

const handleStartClass = async () => {
  try {
    // 验证表单
    await startClassFormRef.value.validate()
    
    const teacherId = localStorage.getItem('teacherid')
    if (!teacherId) {
      ElMessage.error('未找到教师信息，请重新登录')
      return router.push('/login')
    }

    if (!startClassForm.value.className) {
      ElMessage.warning('请选择班级')
      return
    }

    const res = await api.startClass(teacherId, startClassForm.value.className)
    currentClass.value = startClassForm.value.className
    startClassDialogVisible.value = false

    // 开始录制
    await startRecording()

    ElMessage.success('开始上课')
  } catch (error) {
    if (error.name === 'ValidationError') {
      ElMessage.warning('请完善表单信息')
    } else {
      console.error('开始上课失败:', error)
      ElMessage.error('开始上课失败')
    }
  }
}

// 结束上课
const endClass = () => {
  if (!isRecording.value) {
    ElMessage.warning('当前未在录制中')
    return
  }
  endClassDialogVisible.value = true
}

const handleEndClass = async () => {
  if (!endClassFormRef.value) return

  try {
    // 验证表单
    await endClassFormRef.value.validate()

    if (!endClassForm.value.courseName || endClassForm.value.courseName.trim() === '') {
      ElMessage.warning('请输入课程名称')
      return
    }

    console.log('开始处理下课...')

    // 检查是否正在录制
    if (!isRecording.value || !recordingStartTime.value) {
      throw new Error('录制状态异常，请刷新页面重试')
    }

    // 保存开始时间，因为stopRecording会将其设为null
    const startTime = recordingStartTime.value
    const duration = recordingDuration.value

    // 停止录制
    stopRecording()

    if (recordedChunks.length === 0) {
      throw new Error('没有录制到任何数据')
    }

    // 创建录制文件
    console.log('创建录制文件...')
    const recordingBlob = new Blob(recordedChunks, {
      type: mediaRecorder.mimeType || 'video/webm'
    })
    console.log('录制文件大小:', recordingBlob.size, 'bytes')

    // 上传录制文件
    const formData = new FormData()
    const teacherId = localStorage.getItem('teacherid')
    formData.append('video', recordingBlob)
    formData.append('className', currentClass.value)
    formData.append('courseName', endClassForm.value.courseName.trim())

    // 使用中国时区的时间
    const endTime = new Date()
    endTime.setHours(endTime.getHours() + 8)

    formData.append('startTime', startTime.toISOString())
    formData.append('endTime', endTime.toISOString())
    formData.append('duration', String(duration))

    console.log('开始上传录制文件...', {
      startTime: startTime.toISOString(),
      endTime: endTime.toISOString(),
      duration: duration
    })

    // 在上传时添加进度监控
    await api.endClass(teacherId, formData, {
      onUploadProgress: (progressEvent) => {
        uploadProgress.value = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
        )
      }
    })

    endClassDialogVisible.value = false
    currentClass.value = ''
    recordingDuration.value = 0
    recordedChunks = []
    endClassForm.value.courseName = '' // 清空课程名称

    ElMessage.success('下课成功')
    fetchClassRecords()
  } catch (error) {
    if (error.name === 'ValidationError') {
      ElMessage.warning('请完善表单信息')
    } else {
      console.error('下课失败:', error)
      ElMessage.error(error.message || '下课失败')
    }
  }
}

// 查看回放
const viewRecording = async (row) => {
  try {
    const res = await api.getVideoDetail(row.videoid)
    if (res.data.data && res.data.data.url) {
      window.open(res.data.data.url, '_blank')
    } else {
      ElMessage.error('获取视频URL失败')
    }
  } catch (error) {
    console.error('获取视频URL失败:', error)
    ElMessage.error('获取视频URL失败')
  }
}

// 下载录制文件
const downloadRecording = async (row) => {
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

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchClassRecords()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchClassRecords()
}

const router = useRouter()

onMounted(() => {
  fetchClassRecords()
})

onUnmounted(() => {
  if (isRecording.value) {
    stopRecording()
  }
})

// 添加时间格式化函数
const formatTimeWithTimezone = (time) => {
  if (!time) return ''
  const date = new Date(time)
  date.setHours(date.getHours() + 8)
  return date.toISOString().replace('T', ' ').slice(0, 19)
}

// 修改录制时长格式化函数
const formatRecordingDuration = (duration) => {
  if (!duration || duration < 0) return '00:00:00'

  // 确保duration是毫秒数
  const ms = parseInt(duration)
  const hours = Math.floor(ms / (1000 * 60 * 60))
  const minutes = Math.floor((ms % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((ms % (1000 * 60)) / 1000)

  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}
</script>

<style scoped>
.class-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.recording-status {
  margin-bottom: 20px;
}

.recording-info {
  margin-top: 10px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
}

.class-history {
  margin-top: 30px;
}

.video-preview {
  margin: 20px 0;
  position: relative;
  width: 100%;
  max-width: 800px;
  height: 450px; /* 16:9 比例 */
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.video-preview video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recording-duration {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 5px 10px;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 