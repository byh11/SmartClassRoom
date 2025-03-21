<template>
  <div class="app">
    <nav-bar v-if="showNavBar"/>
    <main class="main-content">
      <router-view/>
    </main>
  </div>
</template>

<script>
import NavBar from './components/NavBar.vue'

export default {
  name: 'App',
  components: {
    NavBar
  },
  computed: {
    showNavBar() {
      // 在登录后的学生和教师页面不显示导航栏
      return !['/student', '/teacher'].includes(this.$route.path)
    }
  }
}
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
}

.app {
  min-height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  width: 100%;
  height: 100%;
}

/* 登录后的全屏布局 */
.full-screen {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
</style>
