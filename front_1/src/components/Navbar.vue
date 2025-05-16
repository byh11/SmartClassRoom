<template>
  <el-menu
      :default-active="activeRoute"
      class="navbar"
      mode="horizontal"
      router
  >
    <template v-for="item in menuItems" :key="item.path">
      <el-menu-item :index="item.path">
        <el-icon>
          <component :is="item.icon"/>
        </el-icon>
        {{ item.text }}
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
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {Edit, Key, SwitchButton, User} from '@element-plus/icons-vue'

const store = useStore()
const router = useRouter()
const route = useRoute()

const isLoggedIn = computed(() => store.state.user.isLoggedIn)
const isTeacher = computed(() => store.state.user.role === 'teacher')
const activeRoute = computed(() => route.path)
const userRole = computed(() => store.state.user.role)

const handleLogout = async () => {
  await store.dispatch('logout')
  ElMessage.success('退出成功')
  router.push('/login')
}

const menuItems = computed(() => {
  const commonItems = [
    {icon: 'HomeFilled', text: '首页', path: '/'},
    {icon: 'VideoCamera', text: '视频', path: '/video'},
    {icon: 'Collection', text: '收藏', path: '/collected-videos'},
    {icon: 'Star', text: '喜欢', path: '/liked-videos'},
  ]

  const studentItems = [
    ...commonItems,
    {icon: 'DataLine', text: '学习统计', path: '/learning/stats'},
    {icon: 'Document', text: '学习报告', path: '/learning/report'}
  ]

  const teacherItems = [
    ...commonItems,
    {icon: 'VideoCamera', text: '视频管理', path: '/teacher/videos'},
    {icon: 'Calendar', text: '上课管理', path: '/teacher/class'}
  ]

  return isTeacher.value ? teacherItems : studentItems
})
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