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
          :description="`已录制时长: ${formatDuration(recordingDuration)}`"
          show-icon
          title="正在录制中"
          type="warning"
      />

      <div class="recording-info">
        <p>班级：{{ currentClass }}</p>
        <p>开始时间：{{ formatTime(recordingStartTime) }}</p>
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
        <el-table-column label="课程名称" prop="courseName" width="200"/>
        <el-table-column label="上课时间" prop="startTime" width="180">
          <template #default="{ row }">
            {{ formatTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="下课时间" prop="endTime" width="180">
          <template #default="{ row }">
            {{ formatTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="课程时长" prop="duration" width="120">
          <template #default="{ row }">
            {{ formatDuration(row.duration) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'processed' ? 'success' : 'warning'">
              {{ row.status === 'processed' ? '已处理' : '处理中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="200">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                  :disabled="row.status !== 'processed'"
                  link
                  type="primary"
                  @click="viewRecording(row)"
              >
                查看回放
              </el-button>
              <el-button
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
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next"
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
import {ref, onMounted, onUnmounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {formatTime, formatDuration} from '@/utils/format'
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

// 录制相关
let mediaRecorder = null
let recordedChunks = []
let durationTimer = null

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
  {label: '计算机科学1班', value: 'CS-1'},
  {label: '计算机科学2班', value: 'CS-2'},
  {label: '软件工程1班', value: 'SE-1'},
  {label: '软件工程2班', value: 'SE-2'}
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
    const res = await api.getClassRecords(teacherId, currentPage.value)
    classRecords.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取上课记录失败')
  } finally {
    loading.value = false
  }
}

// 开始录制
const startRecording = async () => {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({
      video: true,
      audio: true
    })

    mediaRecorder = new MediaRecorder(stream)
    recordedChunks = []

    mediaRecorder.ondataavailable = (event) => {
      if (event.data.size > 0) {
        recordedChunks.push(event.data)
      }
    }

    mediaRecorder.start()
    isRecording.value = true
    recordingStartTime.value = new Date()

    // 开始计时
    durationTimer = setInterval(() => {
      recordingDuration.value = Date.now() - recordingStartTime.value
    }, 1000)

  } catch (error) {
    ElMessage.error('无法访问摄像头和麦克风')
    console.error('录制失败:', error)
  }
}

// 停止录制
const stopRecording = () => {
  if (mediaRecorder && mediaRecorder.state !== 'inactive') {
    mediaRecorder.stop()
    mediaRecorder.stream.getTracks().forEach(track => track.stop())
  }

  clearInterval(durationTimer)
  isRecording.value = false
}

// 开始上课
const startClass = () => {
  startClassDialogVisible.value = true
}

const handleStartClass = async () => {
  try {
    const teacherId = localStorage.getItem('teacherid')
    if (!teacherId) {
      ElMessage.error('未找到教师信息，请重新登录')
      return router.push('/login')
    }

    const res = await api.startClass(teacherId, startClassForm.value.className)
    currentClass.value = startClassForm.value.className
    startClassDialogVisible.value = false

    // 开始录制
    await startRecording()

    ElMessage.success('开始上课')
  } catch (error) {
    ElMessage.error('开始上课失败')
  }
}

// 结束上课
const endClass = () => {
  endClassDialogVisible.value = true
}

const handleEndClass = async () => {
  if (!endClassFormRef.value) return

  try {
    // 停止录制
    stopRecording()

    // 创建录制文件
    const recordingBlob = new Blob(recordedChunks, {
      type: 'video/webm'
    })

    // 上传录制文件
    const formData = new FormData()
    const teacherId = localStorage.getItem('teacherid')
    formData.append('video', recordingBlob)
    formData.append('className', currentClass.value)
    formData.append('courseName', endClassForm.value.courseName)
    formData.append('startTime', recordingStartTime.value.toISOString())
    formData.append('endTime', new Date().toISOString())
    await api.endClass(teacherId, formData)

    endClassDialogVisible.value = false
    currentClass.value = ''
    recordingDuration.value = 0
    recordingStartTime.value = null

    ElMessage.success('下课成功')
    fetchClassRecords()
  } catch (error) {
    ElMessage.error('下课失败')
  }
}

// 查看回放
const viewRecording = (row) => {
  window.open(row.recordingUrl, '_blank')
}

// 下载录制文件
const downloadRecording = async (row) => {
  try {
    const res = await api.getRecordingDownloadUrl(row.id)
    window.open(res.data.url, '_blank')
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 