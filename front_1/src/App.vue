<template>
  <div id="app">
    <Navbar/>
    <div class="main-container">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import Navbar from '@/components/Navbar.vue'
</script>

<style>
@import '@/assets/styles/main.css';

#app {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.main-container {
  padding: 20px;
  margin-top: 60px; /* 为固定导航栏留出空间 */
}
</style> 