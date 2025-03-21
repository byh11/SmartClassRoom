<template>
  <div class="comment-editor">
    <el-input
        v-model="content"
        :maxlength="200"
        :rows="3"
        placeholder="写下你的评论..."
        show-word-limit
        type="textarea"
    />
    <div class="editor-footer">
      <div class="emoji-picker">
        <el-popover
            placement="top"
            trigger="click"
            width="300"
        >
          <template #reference>
            <el-button text>
              <el-icon>
                <Emoji/>
              </el-icon>
              表情
            </el-button>
          </template>
          <div class="emoji-list">
            <span
                v-for="emoji in emojiList"
                :key="emoji"
                class="emoji-item"
                @click="insertEmoji(emoji)"
            >
              {{ emoji }}
            </span>
          </div>
        </el-popover>
      </div>
      <el-button type="primary" @click="handleSubmit">
        发表评论
      </el-button>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue'
</script> 