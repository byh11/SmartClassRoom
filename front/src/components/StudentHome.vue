<template>
  <div class="student-home full-screen">
    <div class="student-container">
      <!-- 左侧菜单 -->
      <div class="sidebar">
        <div class="logo">
          <h2>智慧教室</h2>
        </div>
        <el-menu
            :default-active="activeMenu"
            active-text-color="#409EFF"
            background-color="#304156"
            class="sidebar-menu"
            text-color="#bfcbd9"
        >
          <el-menu-item index="1" @click="handleMenuClick('videos')">
            <el-icon>
              <VideoCamera/>
            </el-icon>
            <span>视频查询</span>
          </el-menu-item>
          <el-menu-item index="2" @click="handleMenuClick('profile')">
            <el-icon>
              <User/>
            </el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧内容区 -->
      <div class="main-content">
        <div class="header">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentPage }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="user-info">
            <el-dropdown>
              <span class="user-dropdown">
                {{ username }}
                <el-icon><ArrowDown/></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <div class="content">
          <router-view></router-view>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {computed, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ArrowDown, User, VideoCamera} from '@element-plus/icons-vue'

export default {
  name: 'StudentHome',
  components: {
    VideoCamera,
    User,
    ArrowDown
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const username = ref('学生用户')
    const activeMenu = ref('1')

    const currentPage = computed(() => {
      const path = route.path
      if (path.includes('videos')) return '视频查询'
      if (path.includes('profile')) return '个人中心'
      return '首页'
    })

    const handleMenuClick = (path) => {
      router.push(`/student/${path}`)
    }

    const handleLogout = () => {
      // TODO: 实现登出逻辑
      router.push('/login')
    }

    return {
      username,
      activeMenu,
      currentPage,
      handleMenuClick,
      handleLogout
    }
  }
}
</script>

<style scoped>
.student-home {
  background-color: #f0f2f5;
}

.student-container {
  display: flex;
  height: 100%;
}

.sidebar {
  width: 210px;
  height: 100%;
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  background-color: #2b2f3a;
}

.logo h2 {
  margin: 0;
  color: #fff;
  font-size: 20px;
}

.sidebar-menu {
  border-right: none;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, .08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.breadcrumb {
  font-size: 14px;
}

.user-info {
  cursor: pointer;
}

.user-dropdown {
  display: flex;
  align-items: center;
  color: #606266;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}
</style>
