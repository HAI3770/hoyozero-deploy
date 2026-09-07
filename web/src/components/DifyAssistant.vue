<template>
  <div class="dify-assistant">
    <n-button v-if="!open" circle type="primary" size="large" class="launcher" @click="open = true">
      <template #icon><n-icon><ChatbubbleEllipsesSharp /></n-icon></template>
    </n-button>
    <n-card v-else class="panel" :bordered="false">
      <template #header><span>CI/CD 智能助手</span></template>
      <template #header-extra><n-button text @click="open = false">×</n-button></template>
      <iframe v-if="agentUrl" class="dify-frame" :src="agentUrl" title="CI/CD 智能助手" frameborder="0" allow="microphone;clipboard-write" />
      <div v-else class="dify-empty">请在前端构建时配置 VITE_DIFY_AGENT_URL</div>
    </n-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { NButton, NCard, NIcon } from 'naive-ui'
import { ChatbubbleEllipsesSharp } from '@vicons/ionicons5'

const open = ref(false)
const agentUrl = import.meta.env.VITE_DIFY_AGENT_URL || ''
</script>

<style scoped>
.dify-assistant { position: fixed; right: 28px; bottom: 28px; z-index: 1000; }
.launcher { box-shadow: 0 8px 24px rgba(24,160,88,.35); }
.panel { width: 420px; height: 650px; display: flex; flex-direction: column; overflow: hidden; box-shadow: 0 12px 40px rgba(0,0,0,.18); }
.panel :deep(.n-card__content) { flex: 1; min-height: 0; display: flex; padding: 0; }
.dify-frame { width: 100%; height: 100%; min-height: 0; border: 0; background: #fff; }
.dify-empty { flex: 1; display: flex; align-items: center; justify-content: center; padding: 24px; color: #777; text-align: center; }
@media (max-height: 760px) { .panel { height: calc(100vh - 56px); } }
</style>
