<template>
  <div class="danmaku-input">
    <el-input
        v-model="content"
        :maxlength="50"
        placeholder="发送弹幕..."
        @keyup.enter="handleSend"
    >
      <template #append>
        <el-select v-model="mode" style="width: 100px">
          <el-option label="滚动" value="scroll"/>
          <el-option label="顶部" value="top"/>
          <el-option label="底部" value="bottom"/>
        </el-select>
        <el-button @click="handleSend">发送</el-button>
      </template>
      <template #prefix>
        <el-color-picker v-model="color" size="small"/>
      </template>
    </el-input>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {ElMessage} from 'element-plus'

const content = ref('')
const mode = ref('scroll')
const color = ref('#ffffff')

const emit = defineEmits(['send'])

const handleSend = () => {
  if (!content.value.trim()) {
    ElMessage.warning('请输入弹幕内容')
    return
  }

  emit('send', {
    content: content.value,
    mode: mode.value,
    color: color.value
  })

  content.value = ''
}
</script>

<style scoped>
.danmaku-input {
  padding: 10px;
  background: rgba(0, 0, 0, 0.8);
}
</style> 