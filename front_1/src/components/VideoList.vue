<template>
  <div class="video-list">
    <el-row :gutter="20">
      <el-col
          v-for="video in videos"
          :key="video.id"
          :lg="6"
          :md="8"
          :sm="12"
          :xl="4"
          :xs="24"
      >
        <VideoCard
            :show-actions="showActions"
            :show-description="showDescription"
            :video="video"
            @update="handleUpdate"
        />
      </el-col>
    </el-row>

    <!-- 加载更多 -->
    <div
        v-if="showLoadMore && !loading && hasMore"
        class="load-more"
    >
      <el-button @click="$emit('load-more')">
        加载更多
      </el-button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <el-spinner/>
    </div>

    <!-- 无数据状态 -->
    <el-empty
        v-if="!loading && videos.length === 0"
        description="暂无视频"
    />
  </div>
</template>

<script setup>
import VideoCard from './VideoCard.vue'

const props = defineProps({
  videos: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  hasMore: {
    type: Boolean,
    default: false
  },
  showLoadMore: {
    type: Boolean,
    default: true
  },
  showDescription: {
    type: Boolean,
    default: false
  },
  showActions: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update', 'load-more'])

const handleUpdate = () => {
  emit('update')
}
</script>

<style scoped>
.video-list {
  margin-bottom: 20px;
}

.video-list .el-col {
  margin-bottom: 20px;
}

.load-more {
  text-align: center;
  margin-top: 20px;
}

.loading {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}
</style> 