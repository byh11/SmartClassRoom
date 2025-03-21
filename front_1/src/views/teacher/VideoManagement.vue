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
          <el-link @click="previewVideo(row)">{{ row.title }}</el-link>
        </template>
      </el-table-column>

      <el-table-column label="上传时间" prop="createTime" width="180">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>

      <el-table-column label="时长" prop="duration" width="120">
        <template #default="{ row }">
          {{ formatDuration(row.duration) }}
        </template>
      </el-table-column>

      <el-table-column label="播放量" prop="views" width="120"/>

      <el-table-column label="状态" prop="status" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 'published' ? 'success' : 'info'">
            {{ row.status === 'published' ? '已发布' : '未发布' }}
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
                :type="row.status === 'published' ? 'info' : 'success'"
                link
                type="primary"
                @click="handleToggleStatus(row)"
            >
              {{ row.status === 'published' ? '取消发布' : '发布' }}
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
        <el-form-item label="视频标题" prop="title">
          <el-input v-model="uploadForm.title"/>
        </el-form-item>

        <el-form-item label="视频描述" prop="description">
          <el-input
              v-model="uploadForm.description"
              :rows="3"
              type="textarea"
          />
        </el-form-item>

        <el-form-item label="视频分类" prop="category">
          <el-select v-model="uploadForm.category" placeholder="请选择分类">
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
              :action="uploadUrl"
              :before-upload="beforeUpload"
              :headers="uploadHeaders"
              :on-error="handleUploadError"
              :on-progress="handleUploadProgress"
              :on-success="handleUploadSuccess"
              accept="video/*"
              class="video-uploader"
          >
            <template #trigger>
              <el-button type="primary">选择视频</el-button>
            </template>
            <template #tip>
              <div class="el-upload__tip">
                支持mp4、mov等格式视频文件
              </div>
            </template>
          </el-upload>
          <el-progress
              v-if="uploadProgress > 0"
              :percentage="uploadProgress"
              :status="uploadStatus"
          />
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
        <el-form-item label="视频标题" prop="title">
          <el-input v-model="editForm.title"/>
        </el-form-item>

        <el-form-item label="视频描述" prop="description">
          <el-input
              v-model="editForm.description"
              :rows="3"
              type="textarea"
          />
        </el-form-item>

        <el-form-item label="视频分类" prop="category">
          <el-select v-model="editForm.category" placeholder="请选择分类">
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
import {ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import api from '@/api'
import {formatTime, formatDuration} from '@/utils/format'

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
const uploadForm = ref({
  title: '',
  description: '',
  category: '',
  file: null
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
  {label: '课程视频', value: 'course'},
  {label: '教学资料', value: 'material'},
  {label: '实验演示', value: 'experiment'}
]

// 表单验证规则
const uploadRules = {
  title: [
    {required: true, message: '请输入视频标题', trigger: 'blur'},
    {min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur'}
  ],
  description: [
    {required: true, message: '请输入视频描述', trigger: 'blur'}
  ],
  category: [
    {required: true, message: '请选择视频分类', trigger: 'change'}
  ]
}

const editRules = {...uploadRules}

// 获取视频列表
const fetchVideoList = async () => {
  loading.value = true
  try {
    const res = await api.getTeacherVideos({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    videoList.value = res.data.list
    total.value = res.data.total
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
    title: '',
    description: '',
    category: '',
    file: null
  }
  uploadProgress.value = 0
}

const beforeUpload = (file) => {
  const isVideo = file.type.startsWith('video/')
  const isLt2G = file.size / 1024 / 1024 / 1024 < 2

  if (!isVideo) {
    ElMessage.error('请上传视频文件！')
    return false
  }
  if (!isLt2G) {
    ElMessage.error('视频大小不能超过 2GB！')
    return false
  }

  uploadForm.value.file = file
  return true
}

const handleUploadProgress = (event) => {
  uploadProgress.value = Math.round(event.percent)
  uploadStatus.value = 'processing'
}

const handleUploadSuccess = (response) => {
  uploadStatus.value = 'success'
  uploadForm.value.url = response.url
  ElMessage.success('视频上传成功')
}

const handleUploadError = () => {
  uploadStatus.value = 'error'
  ElMessage.error('视频上传失败')
}

const handleUploadSubmit = async () => {
  if (!uploadFormRef.value) return

  try {
    await uploadFormRef.value.validate()
    await api.createVideo(uploadForm.value)
    ElMessage.success('视频创建成功')
    uploadDialogVisible.value = false
    fetchVideoList()
  } catch (error) {
    ElMessage.error('视频创建失败')
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
    await api.updateVideo(editForm.value.id, editForm.value)
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
    const res = await api.getVideoDownloadUrl(row.id)
    window.open(res.data.url, '_blank')
  } catch (error) {
    ElMessage.error('获取下载链接失败')
  }
}

const handleToggleStatus = async (row) => {
  try {
    await api.updateVideoStatus(row.id, {
      status: row.status === 'published' ? 'draft' : 'published'
    })
    ElMessage.success('状态更新成功')
    fetchVideoList()
  } catch (error) {
    ElMessage.error('状态更新失败')
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