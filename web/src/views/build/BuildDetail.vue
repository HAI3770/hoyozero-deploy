<template>
  <div class="build-detail">
    <n-page-header @back="handleBack">
      <template #title>构建详情</template>
      
      <template #extra>
        <n-space>
          <n-tag :type="getStatusType(buildInfo.status)">
            {{ getStatusText(buildInfo.status) }}
          </n-tag>
          <n-button @click="loadBuild">
            <template #icon>
              <n-icon><RefreshSharp /></n-icon>
            </template>
            刷新
          </n-button>
        </n-space>
      </template>
      
      <n-descriptions :column="3" bordered style="margin-top: 16px">
        <n-descriptions-item label="构建ID">
          {{ buildInfo.id }}
        </n-descriptions-item>
        <n-descriptions-item label="项目名称">
          {{ buildInfo.projectName || '-' }}
        </n-descriptions-item>
        <n-descriptions-item label="触发人">
          {{ buildInfo.triggerByName || '-' }}
        </n-descriptions-item>
        <n-descriptions-item label="开始时间">
          {{ buildInfo.startTime || '-' }}
        </n-descriptions-item>
        <n-descriptions-item label="结束时间">
          {{ buildInfo.endTime || '-' }}
        </n-descriptions-item>
        <n-descriptions-item label="耗时">
          {{ buildInfo.duration ? buildInfo.duration + 's' : '-' }}
        </n-descriptions-item>
      </n-descriptions>
    </n-page-header>
    
    <n-card title="构建日志" style="margin-top: 16px" :bordered="false">
      <div class="log-console" ref="logConsoleRef">
        <pre>{{ logs }}</pre>
      </div>
    </n-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { RefreshSharp } from '@vicons/ionicons5'
import { getBuild } from '@/api/build'

const router = useRouter()
const route = useRoute()
const buildInfo = ref({})
const logs = ref('')
const logConsoleRef = ref()
let ws = null

const getStatusType = (status) => {
  const map = {
    'PENDING': 'default',
    'RUNNING': 'info',
    'SUCCESS': 'success',
    'FAILED': 'error'
  }
  return map[status] || 'default'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '等待中',
    'RUNNING': '运行中',
    'SUCCESS': '成功',
    'FAILED': '失败'
  }
  return map[status] || status
}

const handleBack = () => {
  router.back()
}

const loadBuild = async () => {
  if (!route.params.id) {
    console.error('构建ID不存在')
    return
  }
  
  try {
    const data = await getBuild(route.params.id)
    buildInfo.value = data
    
    if (data.log) {
      logs.value = data.log
    }
  } catch (error) {
    console.error(error)
  }
}

const connectWebSocket = () => {
  if (!route.params.id) {
    console.error('构建ID不存在，无法建立WebSocket连接')
    return
  }
  
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const wsUrl = `${protocol}//${window.location.host}/ws/build/${route.params.id}`
  
  ws = new WebSocket(wsUrl)
  
  ws.onopen = () => {
    console.log('WebSocket连接已建立')
  }
  
  ws.onmessage = (event) => {
    logs.value += event.data + '\n'
    
    nextTick(() => {
      if (logConsoleRef.value) {
        logConsoleRef.value.scrollTop = logConsoleRef.value.scrollHeight
      }
    })
  }
  
  ws.onerror = (error) => {
    console.error('WebSocket错误:', error)
  }
  
  ws.onclose = () => {
    console.log('WebSocket连接已关闭')
    loadBuild()
  }
}

onMounted(() => {
  loadBuild()
  connectWebSocket()
})

onUnmounted(() => {
  if (ws) {
    ws.close()
  }
})
</script>

<style scoped>
.build-detail {
  width: 100%;
}

.log-console {
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 16px;
  border-radius: 4px;
  max-height: 600px;
  overflow-y: auto;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
}

.log-console pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.log-console::-webkit-scrollbar {
  width: 8px;
}

.log-console::-webkit-scrollbar-thumb {
  background: #555;
  border-radius: 4px;
}

.log-console::-webkit-scrollbar-thumb:hover {
  background: #777;
}
</style>
