<template>
  <div class="profile">
    <router-view></router-view>
  </div>
</template>

<script setup>
import {onMounted} from 'vue'
import {useRouter, useRoute} from 'vue-router'
import {useStore} from 'vuex'

const router = useRouter()
const route = useRoute()
const store = useStore()

onMounted(() => {
  // 根据用户角色自动重定向到对应的个人中心页面
  const userRole = store.state.user.role
  if (route.path === '/profile') {
    if (userRole === 'teacher') {
      router.replace('/profile/teacher')
    } else {
      router.replace('/profile/student')
    }
  }
})
</script> 