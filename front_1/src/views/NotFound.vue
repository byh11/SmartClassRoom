<template>
  <div class="not-found">
    <el-result
        icon="warning"
        sub-title="抱歉，您访问的页面不存在"
        title="404"
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/')">
          返回首页
        </el-button>
      </template>
    </el-result>
  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
}
</style> 