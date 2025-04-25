<template>
  <div class="ai-assistant">
    <div
        :style="{ left: chatPosition.x + 'px', top: chatPosition.y + 'px' }"
        class="ai-avatar"
        @click="toggleChat"
        @mousedown="startDrag"
    >
      <img
          alt="AI Assistant"
          src="https://1324618242.vod-qcloud.com/9e20a3e3vodcq1324618242/ce72993a1397757908570674844/1397757908572356484.png"/>
    </div>
    <div
        v-if="showChat"
        :style="{
        left: (chatPosition.x + 60) + 'px',
        top: chatPosition.y + 'px',
        width: chatSize.width + 'px',
        height: chatSize.height + 'px'
      }"
        class="chat-window"
        @mousedown="startWindowDrag"
    >
      <div class="chat-header">
        <span>AI 助手</span>
        <button @click="toggleChat">×</button>
      </div>
      <div ref="messagesContainer" class="chat-messages">
        <div v-for="(message, index) in messages" :key="index" :class="['message', message.type]">
          <div v-if="message.type === 'ai'" @click="handleLinkClick" v-html="message.content"></div>
          <div v-else>{{ message.content }}</div>
        </div>
      </div>
      <div class="chat-input">
        <input
            v-model="userInput"
            placeholder="输入您的问题..."
            @keyup.enter="sendMessage"
        />
        <button @click="sendMessage">发送</button>
      </div>
      <div class="resize-handle" @mousedown="startResize"></div>
    </div>
  </div>
</template>

<script setup>
import {nextTick, ref} from 'vue'
import {useStore} from 'vuex'
import {marked} from 'marked'

const position = ref({x: 50, y: 50})
const isDragging = ref(false)
const isWindowDragging = ref(false)
const showChat = ref(false)
const userInput = ref('')
const messages = ref([])
const messagesContainer = ref(null)
const store = useStore()
const chatSize = ref({width: 300, height: 400})
const isResizing = ref(false)
const chatPosition = ref({x: 50, y: 50})

let dragStartX = 0
let dragStartY = 0
let windowDragStartX = 0
let windowDragStartY = 0
let resizeStartX = 0
let resizeStartY = 0
let originalWidth = 0
let originalHeight = 0

const startDrag = (e) => {
  if (e.target.closest('.ai-avatar')) {
    e.preventDefault()
    e.stopPropagation()
    isDragging.value = true
    dragStartX = e.clientX - chatPosition.value.x
    dragStartY = e.clientY - chatPosition.value.y
    document.addEventListener('mousemove', onDrag)
    document.addEventListener('mouseup', stopDrag)
  }
}

const startWindowDrag = (e) => {
  if (e.target.classList.contains('chat-header') || e.target.tagName === 'SPAN') {
    e.stopPropagation()
    isWindowDragging.value = true
    windowDragStartX = e.clientX - chatPosition.value.x
    windowDragStartY = e.clientY - chatPosition.value.y
    document.addEventListener('mousemove', onWindowDrag)
    document.addEventListener('mouseup', stopWindowDrag)
  }
}

const onDrag = (e) => {
  if (isDragging.value) {
    e.preventDefault()
    const newX = e.clientX - dragStartX
    const newY = e.clientY - dragStartY

    // 限制在视口范围内
    chatPosition.value.x = Math.min(Math.max(0, newX), window.innerWidth - 50)
    chatPosition.value.y = Math.min(Math.max(0, newY), window.innerHeight - 50)
  }
}

const onWindowDrag = (e) => {
  if (isWindowDragging.value) {
    chatPosition.value.x = e.clientX - windowDragStartX
    chatPosition.value.y = e.clientY - windowDragStartY
  }
}

const stopDrag = (e) => {
  if (isDragging.value) {
    e.preventDefault()
    isDragging.value = false
    document.removeEventListener('mousemove', onDrag)
    document.removeEventListener('mouseup', stopDrag)
  }
}

const stopWindowDrag = (e) => {
  if (isWindowDragging.value) {
    e.preventDefault()
    isWindowDragging.value = false
    document.removeEventListener('mousemove', onWindowDrag)
    document.removeEventListener('mouseup', stopWindowDrag)
  }
}

const startResize = (e) => {
  isResizing.value = true
  resizeStartX = e.clientX
  resizeStartY = e.clientY
  originalWidth = chatSize.value.width
  originalHeight = chatSize.value.height
  document.addEventListener('mousemove', onResize)
  document.addEventListener('mouseup', stopResize)
}

const onResize = (e) => {
  if (isResizing.value) {
    const deltaX = e.clientX - resizeStartX
    const deltaY = e.clientY - resizeStartY
    chatSize.value.width = Math.max(300, originalWidth + deltaX)
    chatSize.value.height = Math.max(400, originalHeight + deltaY)
  }
}

const stopResize = () => {
  isResizing.value = false
  document.removeEventListener('mousemove', onResize)
  document.removeEventListener('mouseup', stopResize)
}

const toggleChat = () => {
  showChat.value = !showChat.value
}

const sendMessage = async () => {
  if (!userInput.value.trim()) return

  const userMessage = userInput.value
  messages.value.push({type: 'user', content: userMessage})
  userInput.value = ''

  await nextTick()
  scrollToBottom()

  try {
    let userid = store.state.user.studentid
    if (!userid) {
      userid = store.state.user.teacherid
    }
    if (!userid) {
      userid = 'anonymous'
    }

    // 添加 AI 的初始响应消息
    messages.value.push({type: 'ai', content: ''})
    let lastMessage = messages.value[messages.value.length - 1]
    let markdownContent = ''

    // 使用 fetch 处理流式响应
    const response = await fetch(`${import.meta.env.VITE_AI_API_BASE_URL}/chat?input=${encodeURIComponent(userMessage)}&user=${encodeURIComponent(userid)}`)

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()

    while (true) {
      const {done, value} = await reader.read()
      if (done) break

      const chunk = decoder.decode(value, {stream: true})
      // 累积 Markdown 内容
      markdownContent += chunk
      // 将累积的 Markdown 内容转换为 HTML
      lastMessage.content = marked(markdownContent)
      await nextTick()
      scrollToBottom()
    }

  } catch (error) {
    console.error('Error details:', {
      message: error.message,
      response: error.response,
      config: error.config
    })
    const lastMessage = messages.value[messages.value.length - 1]
    lastMessage.content = '抱歉，发生了错误，请稍后重试。'
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const handleLinkClick = (e) => {
  if (e.target.tagName === 'A') {
    e.preventDefault()
    window.open(e.target.href, '_blank')
  }
}
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  z-index: 1000;
}

.ai-avatar {
  position: fixed;
  width: 50px;
  height: 50px;
  cursor: move;
  user-select: none;
  z-index: 1002; /* 确保 AI 头像始终在最上层 */
}

.ai-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.chat-window {
  position: fixed;
  z-index: 1001;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 400px;
  max-height: 600px;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 10px;
  background: #f0f0f0;
  border-radius: 8px 8px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: move;
  user-select: none;
}

.chat-header button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  padding: 0 5px;
}

.chat-header button:hover {
  color: #ff0000;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.message {
  margin: 5px 0;
  padding: 8px 12px;
  border-radius: 15px;
  max-width: 80%;
}

.message.user {
  background: #007bff;
  color: white;
  margin-left: auto;
}

.message.ai {
  background: #f0f0f0;
  color: black;
}

.chat-input {
  padding: 10px;
  display: flex;
  gap: 10px;
  border-top: 1px solid #eee;
}

.chat-input input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.chat-input button {
  padding: 8px 15px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.chat-input button:hover {
  background: #0056b3;
}

.resize-handle {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 20px;
  height: 20px;
  background: #f0f0f0;
  cursor: se-resize;
  border-radius: 0 0 10px 0;
}

.resize-handle::after {
  content: '';
  position: absolute;
  right: 5px;
  bottom: 5px;
  width: 10px;
  height: 10px;
  border-right: 2px solid #999;
  border-bottom: 2px solid #999;
}

.message.ai :deep(a) {
  color: #007bff;
  text-decoration: underline;
  cursor: pointer;
}

.message.ai :deep(a:hover) {
  color: #0056b3;
}
</style> 