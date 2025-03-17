<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-links">
        <el-button type="text" @click="navigateTo('about')">关于我们</el-button>
        <el-button type="text" @click="navigateTo('contact')">联系我们</el-button>
        <el-button type="text" @click="navigateTo('privacy')">隐私政策</el-button>
        <el-button type="text" @click="navigateTo('terms')">服务条款</el-button>
      </div>
      <div class="footer-info">
        <p>&copy; {{ currentYear }} 教育管理系统. 保留所有权利.</p>
        <p>地址: 123 教育大道, 教育城, 教育省</p>
        <p>电话: (123) 456-7890 | 邮箱: support@example.com</p>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  setup() {
    const currentYear = new Date().getFullYear();

    const navigateTo = (page) => {
      // 根据实际路由配置跳转
      console.log(`Navigate to ${page}`);
    };

    return {currentYear, navigateTo};
  },
};
</script>

<style scoped>
.footer {
  background-color: #f0f0f0;
  padding: 20px;
  text-align: center;
  border-top: 1px solid #e0e0e0;
}

.footer-content {
  max-width: 800px;
  margin: auto;
}

.footer-links {
  margin-bottom: 10px;
}

.footer-info {
  font-size: 14px;
  color: #666;
}
</style>
