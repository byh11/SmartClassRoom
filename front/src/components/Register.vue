<template>
  <div class="register-container">
    <h2 class="title">用户注册</h2>
    <div class="user-type-selection">
      <el-button :class="{ active: userType === 'student' }" type="primary" @click="selectUserType('student')">
        学生注册
      </el-button>
      <el-button :class="{ active: userType === 'teacher' }" type="primary" @click="selectUserType('teacher')">
        教师注册
      </el-button>
    </div>

    <el-form :model="form" class="register-form" @submit.prevent="handleRegister">
      <el-form-item v-if="userType === 'student'" :rules="[{ required: true }]" label="学号">
        <el-input v-model="form.studentid"/>
      </el-form-item>
      <el-form-item v-if="userType === 'student'" :rules="[{ required: true }]" label="密码">
        <el-input v-model="form.password" type="password"/>
      </el-form-item>
      <el-form-item v-if="userType === 'student'" :rules="[{ required: true }]" label="姓名">
        <el-input v-model="form.name"/>
      </el-form-item>
      <el-form-item v-if="userType === 'student'" :rules="[{ required: true }]" label="电话">
        <el-input v-model="form.phone"/>
      </el-form-item>
      <el-form-item v-if="userType === 'student'" :rules="[{ required: true }]" label="邮箱">
        <el-input v-model="form.email"/>
      </el-form-item>
      <el-form-item v-if="userType === 'student'" :rules="[{ required: true }]" label="生日">
        <el-date-picker v-model="form.birthday" type="date"/>
      </el-form-item>
      <el-form-item v-if="userType === 'student'" :rules="[{ required: true }]" label="性别">
        <el-select v-model="form.sex">
          <el-option label="男" value="男"/>
          <el-option label="女" value="女"/>
        </el-select>
      </el-form-item>
      <el-form-item v-if="userType === 'student'" :rules="[{ required: true }]" label="班级">
        <el-input v-model="form.clazz"/>
      </el-form-item>
      <el-form-item v-if="userType === 'student'" :rules="[{ required: true }]" label="专业">
        <el-input v-model="form.major"/>
      </el-form-item>

      <el-form-item v-if="userType === 'teacher'" :rules="[{ required: true, message: '工号不能为空' }]" label="工号">
        <el-input v-model="form.teacherid"/>
        <span v-if="form.teacherid && !form.teacherid.startsWith('teacher')" class="error-message">工号必须以 "teacher" 开头</span>
      </el-form-item>
      <el-form-item v-if="userType === 'teacher'" :rules="[{ required: true }]" label="密码">
        <el-input v-model="form.password" type="password"/>
      </el-form-item>
      <el-form-item v-if="userType === 'teacher'" :rules="[{ required: true }]" label="姓名">
        <el-input v-model="form.name"/>
      </el-form-item>
      <el-form-item v-if="userType === 'teacher'" :rules="[{ required: true }]" label="电话">
        <el-input v-model="form.phone"/>
      </el-form-item>
      <el-form-item v-if="userType === 'teacher'" :rules="[{ required: true }]" label="邮箱">
        <el-input v-model="form.email"/>
      </el-form-item>
      <el-form-item v-if="userType === 'teacher'" :rules="[{ required: true }]" label="生日">
        <el-date-picker v-model="form.birthday" type="date"/>
      </el-form-item>
      <el-form-item v-if="userType === 'teacher'" :rules="[{ required: true }]" label="性别">
        <el-select v-model="form.sex">
          <el-option label="男" value="男"/>
          <el-option label="女" value="女"/>
        </el-select>
      </el-form-item>
      <el-form-item v-if="userType === 'teacher'" :rules="[{ required: true }]" label="学院">
        <el-input v-model="form.college"/>
      </el-form-item>

      <el-button class="register-button" type="primary" @click="handleRegister">注册</el-button>
    </el-form>
  </div>
  <Footer/>

</template>

<script>
import {ref} from 'vue';
import {useRouter} from 'vue-router';
import {mockApi} from '../api'; // 引入模拟 API
import Footer from './MyFooter.vue';

export default {
  components: {
    Footer
  },
  setup() {

    const userType = ref('student'); // 默认选择学生注册
    const form = ref({
      studentid: '',
      password: '',
      name: '',
      phone: '',
      email: '',
      birthday: '',
      sex: '',
      clazz: '',
      major: '',
      teacherid: '',
      college: ''
    });
    const router = useRouter();

    const selectUserType = (type) => {
      userType.value = type;
      form.value = {
        studentid: '',
        password: '',
        name: '',
        phone: '',
        email: '',
        birthday: '',
        sex: '',
        clazz: '',
        major: '',
        teacherid: '',
        college: ''
      };
    };

    const handleRegister = async () => {
      if (userType.value === 'teacher' && !form.value.teacherid.startsWith('teacher')) {
        alert('工号必须以 "teacher" 开头');
        return;
      }

      try {
        let response;
        if (userType.value === 'student') {
          response = await mockApi.studentRegister(form.value);
        } else {
          response = await mockApi.teacherRegister(form.value);
        }

        if (response.code === 200) {
          alert(response.message);
          router.push('/'); // 跳转到登录页面
        } else {
          alert(response.message);
        }
      } catch (error) {
        console.error('注册失败:', error);
        alert('注册失败，请重试');
      }
    };

    return {userType, form, selectUserType, handleRegister};
  },
};
</script>

<style scoped>
.register-container {
  padding: 30px;
  background-color: #f7f9fc;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  max-width: 600px;
  margin: 100px auto;
}

.title {
  text-align: center;
  font-size: 2rem;
  color: #333;
  margin-bottom: 20px;
}

.user-type-selection {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.register-form {
  background-color: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.1);
}

.error-message {
  color: red;
  font-size: 12px;
}

.register-button {
  margin-top: 20px;
  width: 100%;
  background-color: #409eff;
  color: white;
}

.active {
  background-color: #66b1ff; /* 活动按钮颜色 */
  color: white;
}
</style>
