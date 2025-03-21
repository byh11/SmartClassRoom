<template>
  <div class="comment-list">
    <div v-if="comments.length === 0" class="no-comments">
      暂无评论
    </div>

    <div v-else class="comment-items">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <el-avatar :src="comment.user.avatar"/>
        <div class="comment-content">
          <div class="comment-header">
            <span class="username">{{ comment.user.name }}</span>
            <span class="time">{{ formatTime(comment.createTime) }}</span>
          </div>
          <p class="text">{{ comment.content }}</p>
          <div class="actions">
            <el-button type="text" @click="handleLike(comment)">
              <el-icon>
                <Thumb/>
              </el-icon>
              {{ comment.likes }}
            </el-button>
            <el-button type="text" @click="handleReply(comment)">
              回复
            </el-button>
          </div>

          <!-- 回复列表 -->
          <div v-if="comment.replies && comment.replies.length > 0" class="replies">
            <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
              <el-avatar :src="reply.user.avatar" size="small"/>
              <div class="reply-content">
                <div class="comment-header">
                  <span class="username">{{ reply.user.name }}</span>
                  <span class="time">{{ formatTime(reply.createTime) }}</span>
                </div>
                <p class="text">{{ reply.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {formatTime} from '@/utils/date'

const props = defineProps({
  comments: {
    type: Array,
    default: () => []
  }
})

const handleLike = async (comment) => {
  // 实现点赞逻辑
}

const handleReply = (comment) => {
  // 实现回复逻辑
}
</script>

<style scoped>
.comment-list {
  margin-top: 20px;
}

.no-comments {
  text-align: center;
  color: #999;
  padding: 20px;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.username {
  font-weight: bold;
  color: #333;
}

.time {
  color: #999;
  font-size: 12px;
}

.text {
  margin: 5px 0;
  color: #666;
}

.actions {
  display: flex;
  gap: 15px;
}

.replies {
  margin-top: 10px;
  padding-left: 20px;
  background: #f5f5f5;
  border-radius: 4px;
}

.reply-item {
  display: flex;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-content {
  flex: 1;
}
</style> 