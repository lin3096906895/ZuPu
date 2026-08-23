<template>
  <div class="chat-page">
    <div class="chat-header">
      <el-select v-model="familyId" placeholder="选择家族" style="width: 180px">
        <el-option v-for="f in families" :key="f.id" :label="f.name" :value="f.id" />
      </el-select>
      <span class="hint">试试问：安文博的子女是谁？安启明的爷爷是谁？</span>
    </div>
    <div class="chat-messages" ref="messagesRef">
      <div v-for="(m, i) in messages" :key="i" :class="['msg', m.role]">
        <div class="bubble">{{ m.content }}</div>
      </div>
      <div v-if="streaming" class="msg assistant">
        <div class="bubble">{{ streamingText || '思考中...' }}</div>
      </div>
    </div>
    <div class="chat-input">
      <el-input v-model="input" placeholder="输入问题，回车发送" :disabled="streaming" @keyup.enter="send" />
      <el-button type="primary" :disabled="streaming || !input.trim()" @click="send">发送</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { listFamilies } from '@/api'

const families = ref([])
const familyId = ref(null)
const messages = ref([])
const input = ref('')
const streaming = ref(false)
const streamingText = ref('')
const messagesRef = ref(null)

async function scrollToBottom() {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

async function send() {
  const q = input.value.trim()
  if (!q || streaming.value) return
  input.value = ''
  messages.value.push({ role: 'user', content: q })
  streaming.value = true
  streamingText.value = ''
  await scrollToBottom()

  const token = localStorage.getItem('accessToken')
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), 120000)
  try {
    const resp = await fetch('/api/ai/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({ question: q, familyId: familyId.value }),
      signal: controller.signal
    })
    if (!resp.ok) {
      const err = await resp.json().catch(() => ({}))
      throw new Error(err.message || `请求失败(${resp.status})`)
    }
    const reader = resp.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    let finished = false
    while (!finished) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      let idx
      while ((idx = buffer.indexOf('\n\n')) >= 0) {
        const frame = buffer.slice(0, idx)
        buffer = buffer.slice(idx + 2)
        const line = frame.split('\n').find((l) => l.startsWith('data:'))
        if (!line) continue
        let text = line.slice(5).trim()
        if (text === '[DONE]') {
          finished = true
          break
        }
        if (text.startsWith('"') && text.endsWith('"')) {
          text = JSON.parse(text)
        }
        streamingText.value += text
        scrollToBottom()
      }
    }
    messages.value.push({ role: 'assistant', content: streamingText.value })
  } catch (e) {
    if (e.name !== 'AbortError') {
      messages.value.push({ role: 'assistant', content: '出错了：' + e.message })
    } else {
      messages.value.push({ role: 'assistant', content: streamingText.value || '（响应超时）' })
    }
  } finally {
    clearTimeout(timer)
    streaming.value = false
    streamingText.value = ''
    await scrollToBottom()
  }
}

onMounted(async () => {
  families.value = await listFamilies()
  if (families.value.length) {
    familyId.value = families.value[0].id
  }
})
</script>

<style scoped>
.chat-page {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.chat-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}
.hint {
  color: #999;
  font-size: 13px;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 16px;
  background: #fff;
  min-height: 400px;
}
.msg {
  display: flex;
  margin-bottom: 12px;
}
.msg.user {
  justify-content: flex-end;
}
.msg.assistant {
  justify-content: flex-start;
}
.bubble {
  max-width: 70%;
  padding: 10px 14px;
  border-radius: 8px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}
.msg.user .bubble {
  background: #409eff;
  color: #fff;
}
.msg.assistant .bubble {
  background: #f4f4f5;
  color: #333;
}
.chat-input {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}
</style>
