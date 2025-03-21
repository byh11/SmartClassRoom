<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <h2>注册</h2>
      </template>

      <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="rules"
          label-width="100px"
          @submit.prevent
      >
        <el-form-item label="用户类型" prop="userType">
          <el-radio-group v-model="registerForm.userType">
            <el-radio label="student">学生</el-radio>
            <el-radio label="teacher">教师</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item :label="registerForm.userType === 'student' ? '学号' : '教师编号'" prop="userId">
          <el-input v-model="registerForm.userId"/>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" show-password type="password"/>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" show-password type="password"/>
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input v-model="registerForm.name"/>
        </el-form-item>

        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="registerForm.sex">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="registerForm.phone"/>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email"/>
        </el-form-item>

        <el-form-item label="出生日期" prop="birthday">
          <el-date-picker
              v-model="registerForm.birthday"
              format="YYYY-MM-DD"
              placeholder="选择日期"
              style="width: 100%"
              type="date"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <!-- 学生特有字段 -->
        <template v-if="registerForm.userType === 'student'">
          <el-form-item label="班级" prop="clazz">
            <el-input v-model="registerForm.clazz"/>
          </el-form-item>

          <el-form-item label="专业" prop="major">
            <el-input v-model="registerForm.major"/>
          </el-form-item>
        </template>

        <!-- 教师特有字段 -->
        <template v-if="registerForm.userType === 'teacher'">
          <el-form-item label="学院" prop="college">
            <el-input v-model="registerForm.college"/>
          </el-form-item>
        </template>

        <el-form-item>
          <el-button :loading="loading" type="primary" @click="handleRegister">
            注册
          </el-button>
          <el-button @click="$router.push('/login')">
            返回登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import api from '@/api'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  userType: 'student',
  userId: '',
  password: '',
  confirmPassword: '',
  name: '',
  sex: '男',
  phone: '',
  email: '',
  birthday: '',
  clazz: '',
  major: '',
  college: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.confirmPassword !== '') {
      registerFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = reactive({
  userType: [
    {required: true, message: '请选择用户类型', trigger: 'change'}
  ],
  userId: [
    {required: true, message: '请输入账号', trigger: 'blur'},
    {min: 5, max: 20, message: '长度在 5 到 20 个字符', trigger: 'blur'}
  ],
  password: [
    {required: true, validator: validatePass, trigger: 'blur'},
    {min: 6, message: '密码长度不能小于6位', trigger: 'blur'}
  ],
  confirmPassword: [
    {required: true, validator: validatePass2, trigger: 'blur'}
  ],
  name: [
    {required: true, message: '请输入姓名', trigger: 'blur'}
  ],
  sex: [
    {required: true, message: '请选择性别', trigger: 'change'}
  ],
  phone: [
    {required: true, message: '请输入手机号码', trigger: 'blur'},
    {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur'}
  ],
  email: [
    {required: true, message: '请输入邮箱', trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur'}
  ],
  birthday: [
    {required: true, message: '请选择出生日期', trigger: 'change'}
  ],
  clazz: [
    {required: true, message: '请输入班级', trigger: 'blur'}
  ],
  major: [
    {required: true, message: '请输入专业', trigger: 'blur'}
  ],
  college: [
    {required: true, message: '请输入学院', trigger: 'blur'}
  ]
})

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()
    loading.value = true

    // 根据用户类型调用不同的注册接口
    const registerApi = registerForm.userType === 'student'
        ? api.studentRegister
        : api.teacherRegister

    await registerApi(registerForm)

    ElMessage.success('注册成功！')
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error('注册失败，请检查输入信息')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px 0;
  background-color: #f5f7fa;
}

.register-card {
  width: 600px;
}

.el-form-item {
  margin-bottom: 20px;
}
</style> 