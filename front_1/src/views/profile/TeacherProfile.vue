<template>
  <div class="profile-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <div>
            <el-button v-if="!isEditing" type="primary" @click="handleEdit">编辑</el-button>
            <el-button v-if="!isEditing" @click="dialogVisible = true">修改密码</el-button>
            <template v-else>
              <el-button @click="handleCancel">取消</el-button>
              <el-button type="primary" @click="handleSave">保存</el-button>
            </template>
          </div>
        </div>
      </template>

      <el-form
          ref="formRef"
          :disabled="!isEditing"
          :model="form"
          :rules="rules"
          class="profile-form"
          label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="头像">
              <div class="avatar-container">
                <el-avatar :size="100" :src="form.avatar || defaultAvatar"/>
                <el-upload
                    v-if="isEditing"
                    :before-upload="beforeAvatarUpload"
                    :show-file-list="false"
                    class="avatar-uploader"
                >
                  <el-button size="small" type="primary">更换头像</el-button>
                </el-upload>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教师编号" prop="teacherid">
              <el-input v-model="form.teacherid" disabled/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="sex">
              <el-select v-model="form.sex" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="男"/>
                <el-option label="女" value="女"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生日" prop="birthday">
              <el-date-picker
                  v-model="form.birthday"
                  placeholder="选择日期"
                  style="width: 100%"
                  type="date"
                  value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属院系" prop="college">
              <el-input v-model="form.college"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog
        v-model="dialogVisible"
        title="修改密码"
        width="500px"
        @close="handleDialogClose"
    >
      <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
              v-model="passwordForm.oldPassword"
              show-password
              type="password"
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
              v-model="passwordForm.newPassword"
              show-password
              type="password"
          />
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
              v-model="passwordForm.confirmPassword"
              show-password
              type="password"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleChangePassword">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {useStore} from 'vuex'
import api from '@/api'

const store = useStore()
const formRef = ref(null)
const passwordFormRef = ref(null)
const isEditing = ref(false)
const dialogVisible = ref(false)

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 表单数据
const form = reactive({
  teacherid: '',
  name: '',
  sex: '',
  birthday: '',
  phone: '',
  email: '',
  college: '',
  avatarUrl: ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单校验规则
const rules = {
  name: [
    {required: true, message: '请输入姓名', trigger: 'blur'}
  ],
  sex: [
    {required: true, message: '请选择性别', trigger: 'change'}
  ],
  phone: [
    {required: true, message: '请输入手机号', trigger: 'blur'},
    {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur'}
  ],
  email: [
    {required: true, message: '请输入邮箱', trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur'}
  ]
}

// 密码校验规则
const passwordRules = {
  oldPassword: [
    {required: true, message: '请输入原密码', trigger: 'blur'}
  ],
  newPassword: [
    {required: true, message: '请输入新密码', trigger: 'blur'},
    {min: 6, message: '密码长度不能小于6位', trigger: 'blur'}
  ],
  confirmPassword: [
    {required: true, message: '请再次输入新密码', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取教师信息
const fetchTeacherInfo = async () => {
  try {
    const teacherid = store.state.user.teacherid
    console.log('当前教师ID:', teacherid) // 添加日志
    if (!teacherid) {
      ElMessage.error('未找到教师信息')
      return
    }
    const response = await api.getTeacherInfo(teacherid)
    console.log('获取到的教师信息:', response.data) // 添加日志
    if (response.data.code === 200) {
      const data = response.data.data
      Object.assign(form, data)
    } else {
      ElMessage.error(response.data.message || '获取信息失败')
    }
  } catch (error) {
    console.error('获取信息失败:', error) // 添加错误日志
    ElMessage.error('获取信息失败')
  }
}

// 编辑按钮点击
const handleEdit = () => {
  isEditing.value = true
}

// 取消编辑
const handleCancel = () => {
  isEditing.value = false
  fetchTeacherInfo()
}

// 保存信息
const handleSave = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await api.updateTeacherInfo(form.teacherid, form)
        if (response.data.code === 200) {
          ElMessage.success('保存成功')
          isEditing.value = false
        } else {
          ElMessage.error(response.data.message || '保存失败')
        }
      } catch (error) {
        ElMessage.error('保存失败')
      }
    }
  })
}

// 处理对话框关闭
const handleDialogClose = () => {
  passwordFormRef.value?.resetFields()
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await api.teacherChangePassword(form.teacherid, {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })

        if (response.data.code === 200) {
          ElMessage.success('密码修改成功')
          dialogVisible.value = false
          passwordForm.oldPassword = ''
          passwordForm.newPassword = ''
          passwordForm.confirmPassword = ''
          passwordFormRef.value.resetFields()
        } else {
          ElMessage.error(response.data.message || '密码修改失败')
        }
      } catch (error) {
        ElMessage.error('密码修改失败')
      }
    }
  })
}

// 头像上传前的验证
const beforeAvatarUpload = async (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG/GIF 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }

  try {
    const response = await api.updateTeacherAvatar(form.teacherid, file)
    if (response.data.code === 200) {
      form.avatarUrl = response.data.data
      ElMessage.success('头像更新成功')
      return false // 阻止默认上传
    }
  } catch (error) {
    console.error('上传头像失败:', error)
    ElMessage.error('上传头像失败')
  }
  return false
}

onMounted(() => {
  fetchTeacherInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.box-card {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-form {
  max-width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-form-item__content) {
  width: 100%;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.avatar-uploader {
  text-align: center;
}

:deep(.el-upload) {
  width: 100%;
}
</style> 