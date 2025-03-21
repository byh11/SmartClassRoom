<template>
  <el-menu
      :default-active="activeRoute"
      class="navbar"
      mode="horizontal"
      router
  >
    <el-menu-item index="/">
      <el-icon>
        <HomeFilled/>
      </el-icon>
      首页
    </el-menu-item>

    <el-menu-item index="/video">
      <el-icon>
        <VideoCamera/>
      </el-icon>
      视频列表
    </el-menu-item>

    <template v-if="isTeacher">
      <el-menu-item index="/teacher/videos">
        <el-icon>
          <Film/>
        </el-icon>
        视频管理
      </el-menu-item>
      <el-menu-item index="/teacher/class">
        <el-icon>
          <Calendar/>
        </el-icon>
        上课管理
      </el-menu-item>
    </template>

    <div class="flex-grow"/>

    <template v-if="isLoggedIn">
      <el-menu-item
          :index="userRole === 'teacher' ? '/profile/teacher' : '/profile/student'"
      >
        <el-icon>
          <User/>
        </el-icon>
        个人中心
      </el-menu-item>
      <el-menu-item @click="handleLogout">
        <el-icon>
          <SwitchButton/>
        </el-icon>
        退出登录
      </el-menu-item>
    </template>
    <template v-else>
      <el-menu-item index="/login">
        <el-icon>
          <Key/>
        </el-icon>
        登录
      </el-menu-item>
      <el-menu-item index="/register">
        <el-icon>
          <Edit/>
        </el-icon>
        注册
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup>
import {computed} from 'vue'
import {useStore} from 'vuex'
import {useRouter, useRoute} from 'vue-router'
import {ElMessage} from 'element-plus'

const store = useStore()
const router = useRouter()
const route = useRoute()

const isLoggedIn = computed(() => store.state.user.isLoggedIn)
const isTeacher = computed(() => store.state.user.role === 'teacher')
const activeRoute = computed(() => route.path)
const userRole = computed(() => store.state.user.role)

// 根据用户角色计算个人中心路径
const profilePath = computed(() => {
  return isTeacher.value ? '/profile/teacher' : '/profile/student'
})

const handleLogout = async () => {
  await store.dispatch('logout')
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 0 20px;
}

.flex-grow {
  flex-grow: 1;
}
</style> 