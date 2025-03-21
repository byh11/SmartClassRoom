<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>登录</span>
        </div>
      </template>

      <el-form
          ref="formRef"
          :model="loginForm"
          :rules="rules"
          label-width="80px"
      >
        <el-form-item label="用户类型">
          <el-radio-group v-model="loginForm.userType">
            <el-radio label="student">学生</el-radio>
            <el-radio label="teacher">教师</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
            :label="loginForm.userType === 'student' ? '学号' : '教师号'"
            prop="studentid"
        >
          <el-input v-model="loginForm.studentid"/>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="loginForm.password"
              show-password
              type="password"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue'
import {useRouter} from 'vue-router'
import {useStore} from 'vuex'
import {ElMessage} from 'element-plus'
import api from '@/api'

const router = useRouter()
const store = useStore()
const formRef = ref(null)

const loginForm = reactive({
  userType: 'student',
  studentid: '',
  password: ''
})

const rules = {
  studentid: [
    {required: true, message: '请输入账号', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'}
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (loginForm.userType === 'student') {
          const params = {
            username: loginForm.studentid,
            password: loginForm.password
          }
          response = await api.studentLogin(params)
        } else {
          const params = {
            username: loginForm.studentid,
            password: loginForm.password
          }
          response = await api.teacherLogin(params)
        }

        if (response.data.code === 200) {
          const userData = {
            ...response.data.data,
            role: loginForm.userType,
            isLoggedIn: true
          }
          store.commit('setUser', userData)
          localStorage.setItem('teacherid', userData.teacherid)
          localStorage.setItem('user', JSON.stringify(userData))
          router.push('/')
          ElMessage.success('登录成功')
        } else {
          ElMessage.error(response.data.message || '登录失败')
        }
      } catch (error) {
        console.error('登录错误:', error)
        ElMessage.error('登录失败')
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
  font-size: 20px;
}
</style> 