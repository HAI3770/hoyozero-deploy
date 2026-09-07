<template>
  <div class="dify-assistant">
    <n-button v-if="!open" circle type="primary" size="large" class="launcher" @click="open = true">
      <template #icon><n-icon><ChatbubbleEllipsesSharp /></n-icon></template>
    </n-button>
    <n-card v-else class="panel" :bordered="false">
      <template #header><div class="panel-title"><span>CI/CD 智能助手</span><n-tag size="small" :type="overview?.difyConfigured ? 'success' : 'warning'" :bordered="false">{{ overview?.difyConfigured ? '已连接' : '待配置' }}</n-tag></div></template>
      <template #header-extra><n-space :size="4"><n-button text size="small" @click="clearConversation">清空</n-button><n-button text @click="open = false">×</n-button></n-space></template>
      <div ref="messageList" class="messages">
        <div v-if="messages.length === 0" class="welcome">
          <n-icon size="32" color="#18a058"><SparklesSharp /></n-icon>
          <div class="welcome-title">我是 hoyozero 宏宇 CI/CD 助手</div>
          <div class="welcome-text">可以帮你分析构建失败、解释部署状态和梳理发布问题。</div>
          <div v-if="overview" class="overview">服务器 {{ overview.onlineServerCount }}/{{ overview.serverCount }} 在线 · 最近构建 {{ overview.latestBuildStatus || '暂无' }} · 最近部署 {{ overview.latestDeploymentStatus || '暂无' }}</div>
          <n-space vertical :size="8" class="quick-actions">
            <n-button v-for="item in suggestions" :key="item" secondary size="small" @click="send(item)">{{ item }}</n-button>
          </n-space>
        </div>
        <div v-for="item in messages" :key="item.id" :class="['message', item.role]">
          <div class="message-label">{{ item.role === 'user' ? '我' : 'AI 助手' }}</div>
          <div class="message-content" v-html="renderText(item.content)"></div>
        </div>
        <div v-if="loading" class="message assistant"><div class="message-label">AI 助手</div><div class="message-content typing">正在分析 hoyozero 当前状态…</div></div>
      </div>
      <div class="composer">
        <n-input v-model:value="draft" type="textarea" :autosize="{ minRows: 2, maxRows: 5 }" placeholder="询问构建、部署或服务器问题…" :disabled="loading" @keydown.enter.exact.prevent="send()" />
        <n-button type="primary" :loading="loading" :disabled="!draft.trim()" @click="send()">发送</n-button>
      </div>
    </n-card>
  </div>
</template>

<script setup>
import { nextTick, ref, watch } from 'vue'
import { NButton, NCard, NIcon, NInput, NSpace, NTag, useMessage } from 'naive-ui'
import { ChatbubbleEllipsesSharp, SparklesSharp } from '@vicons/ionicons5'
import { chatWithAssistant, getAssistantOverview } from '@/api/ai'

const message = useMessage()
const open = ref(false)
const draft = ref('')
const loading = ref(false)
const messages = ref([])
const messageList = ref(null)
const conversationId = ref('')
const overview = ref(null)
const suggestions = ['分析最近一次构建失败原因', '当前系统最近有哪些异常？', '部署前需要检查哪些风险？']

const scrollToBottom = async () => { await nextTick(); if (messageList.value) messageList.value.scrollTop = messageList.value.scrollHeight }
const renderText = (text) => String(text || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/\n/g, '<br>')

const send = async (preset) => {
  const query = String(preset || draft.value).trim()
  if (!query || loading.value) return
  draft.value = ''
  messages.value.push({ id: `${Date.now()}-user`, role: 'user', content: query })
  loading.value = true
  await scrollToBottom()
  try {
    const result = await chatWithAssistant({ query, conversation_id: conversationId.value || undefined })
    if (result?.conversation_id) conversationId.value = result.conversation_id
    messages.value.push({ id: `${Date.now()}-assistant`, role: 'assistant', content: result?.answer || '暂未获得有效回答。' })
  } catch (error) {
    messages.value.push({ id: `${Date.now()}-error`, role: 'assistant', content: `请求失败：${error.message || '请检查 Dify 配置和服务状态。'}` })
    message.error('智能助手请求失败')
  } finally { loading.value = false; await scrollToBottom() }
}
const clearConversation = () => { messages.value = []; conversationId.value = '' }
const openPanel = () => { open.value = true }
defineExpose({ openPanel })
watch(open, async (value) => {
  if (!value) return
  try { overview.value = await getAssistantOverview() } catch (_) { overview.value = null }
}, { immediate: true })
</script>

<style scoped>
.dify-assistant { position: fixed; right: 28px; bottom: 28px; z-index: 1000; }
.launcher { box-shadow: 0 8px 24px rgba(24,160,88,.35); }
.panel { width: 430px; height: 680px; display: flex; flex-direction: column; overflow: hidden; box-shadow: 0 12px 40px rgba(0,0,0,.18); }
.panel :deep(.n-card__content) { flex: 1; min-height: 0; display: flex; flex-direction: column; padding: 0; }
.panel-title { display: flex; align-items: center; gap: 8px; }
.messages { flex: 1; overflow-y: auto; padding: 16px; background: #f7f8fa; }
.welcome { text-align: center; padding: 28px 12px 12px; color: #666; }
.welcome-title { margin-top: 10px; color: #222; font-weight: 600; }
.welcome-text { margin: 8px 0 18px; font-size: 13px; line-height: 1.6; }
.overview { margin: -4px 0 16px; color: #777; font-size: 12px; line-height: 1.5; }
.quick-actions { align-items: stretch; }
.message { max-width: 92%; margin-bottom: 14px; }
.message.user { margin-left: auto; }
.message-label { margin-bottom: 4px; color: #888; font-size: 12px; }
.message.user .message-label { text-align: right; }
.message-content { padding: 10px 12px; border-radius: 10px; line-height: 1.6; word-break: break-word; font-size: 13px; }
.message.assistant .message-content { background: #fff; border: 1px solid #e8e8e8; }
.message.user .message-content { color: #fff; background: #18a058; }
.typing { color: #888; }
.composer { display: flex; gap: 8px; align-items: flex-end; padding: 12px; border-top: 1px solid #eee; background: #fff; }
.composer :deep(.n-input) { flex: 1; }
@media (max-width: 560px) { .dify-assistant { right: 12px; bottom: 12px; left: 12px; } .panel { width: 100%; height: min(680px, calc(100vh - 24px)); } }
@media (max-height: 760px) { .panel { height: calc(100vh - 56px); } }
</style>
