<template>
  <div class="container">
    <div id="particles-js" class="particles-container"></div>
    <el-form :model="form" class="login-form" @submit.prevent="handleLogin">
      <el-form-item :rules="[{ required: true }]" label="工号">
        <el-input v-model="form.id" placeholder="请输入工号"/>
      </el-form-item>
      <el-form-item :rules="[{ required: true }]" label="密码">
        <el-input v-model="form.password" placeholder="请输入密码" type="password"/>
      </el-form-item>
      <div class="button-container">
        <el-button class="login-button" type="primary" @click="handleLogin">登录</el-button>
        <el-button class="register-button" @click="goToRegister">注册</el-button>
      </div>
    </el-form>
  </div>
</template>

<script>
import {onMounted, ref} from 'vue';
import {useUserStore} from '../store/userStore';
import {useRouter} from 'vue-router';
import {mockApi} from '../api';

export default {
  setup() {
    const form = ref({id: '', password: ''});
    const userStore = useUserStore();
    const router = useRouter();

    const loadParticles = () => {
      const script = document.createElement('script');
      script.src = 'https://cdn.jsdelivr.net/particles.js/2.0.0/particles.min.js';
      script.onload = () => {
        particlesJS.load('particles-js', '/particles.json', () => {
          console.log('particles.js loaded - callback');
        });
      };
      document.head.appendChild(script);
    };

    const handleLogin = async () => {
      const isTeacher = form.value.id.startsWith('teacher');
      const userType = isTeacher ? 'teacher' : 'student';

      try {
        const response = isTeacher
            ? await mockApi.teacherLogin(form.value.id, form.value.password)
            : await mockApi.studentLogin(form.value.id, form.value.password);

        if (response.code === 200) {
          userStore.login({
            username: isTeacher ? response.data.teacherid : response.data.studentid,
            type: userType,
            teacherId: isTeacher ? response.data.teacherid : null,
          });
          router.push(isTeacher ? '/teacher' : '/student');
        } else {
          alert(response.message);
        }
      } catch (error) {
        console.error('登录失败:', error);
        alert('登录失败，请重试');
      }
    };

    const goToRegister = () => {
      router.push('/register');
    };

    onMounted(() => {
      loadParticles();
    });

    return {form, handleLogin, goToRegister};
  },
};
</script>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  position: relative;
  padding: 20px;
}

.particles-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  background-color: rgba(10, 11, 114, 0.5);
}

.login-form {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 80px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  width: 400px; /* 设定宽度以提高可读性 */
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.button-container {
  display: flex;
  justify-content: space-between;
}

.login-button {
  background-color: #409eff;
  color: #fff;
  border-radius: 20px;
  width: 48%; /* 使按钮宽度一致 */
}

.register-button {
  background-color: transparent;
  color: #409eff;
  border: 1px solid #409eff;
  border-radius: 20px;
  width: 48%; /* 使按钮宽度一致 */
}

.register-button:hover {
  background-color: #409eff;
  color: #fff;
}
</style>
