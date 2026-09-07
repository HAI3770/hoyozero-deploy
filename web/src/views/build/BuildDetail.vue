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
        <n-descriptions-item label="Git Commit">
          {{ buildInfo.gitCommit || '-' }}
        </n-descriptions-item>
        <n-descriptions-item label="镜像">
          {{ buildInfo.image ? `${buildInfo.image}:${buildInfo.imageTag || ''}` : '-' }}
        </n-descriptions-item>
        <n-descriptions-item label="Digest">
          {{ buildInfo.imageDigest || '-' }}
        </n-descriptions-item>
      </n-descriptions>
    </n-page-header>

    <n-card title="Pipeline Stages" style="margin-top: 16px" :bordered="false">
      <div class="pipeline" v-if="pipelineStages.length">
        <div class="pipeline-node start-node"><span class="node-dot"></span><span>Start</span></div>
        <div class="pipeline-track">
          <div v-for="stage in pipelineStages" :key="stage.stageKey" class="pipeline-stage">
            <div class="stage-line"></div>
            <div class="stage-dot" :class="stageClass(stage.status)">{{ stage.status === 'SUCCESS' ? '✓' : stage.status === 'FAILED' ? '!' : stage.status === 'RUNNING' ? '…' : '·' }}</div>
            <div class="stage-name">{{ stage.stageName }}</div>
            <div class="stage-status">{{ stageStatusText(stage.status) }}</div>
            <div v-if="stage.stageKey === 'DEPLOY' && deployTasks.length" class="stage-tasks">
              <div v-for="task in deployTasks" :key="task.id" class="task-branch">
                <span class="task-dot" :class="stageClass(task.status)">{{ task.status === 'SUCCESS' ? '✓' : task.status === 'FAILED' ? '!' : '·' }}</span>
                <span>{{ task.serverId || '服务器' }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="pipeline-node end-node"><span class="node-dot"></span><span>End</span></div>
      </div>
      <n-empty v-else description="暂无发布阶段数据" />
    </n-card>
    
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
import { getReleaseList, getReleaseStages, getReleaseTasks } from '@/api/release'

const router = useRouter()
const route = useRoute()
const buildInfo = ref({})
const logs = ref('')
const pipelineStages = ref([])
const deployTasks = ref([])
const logConsoleRef = ref()
let ws = null

const getStatusType = (status) => {
  const map = {
    'PENDING': 'default',
    'RUNNING': 'info',
    'PUSHING': 'warning',
    'SUCCESS': 'success',
    'FAILED': 'error'
  }
  return map[status] || 'default'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '等待中',
    'RUNNING': '构建中',
    'PUSHING': '推送中',
    'SUCCESS': '构建成功',
    'FAILED': '构建失败'
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
    await loadPipeline(data)
    
    if (data.log) {
      logs.value = data.log
    }
  } catch (error) {
    console.error(error)
  }
}

const loadPipeline = async (build) => {
  try {
    const releases = await getReleaseList({ projectId: build.projectId })
    const release = (releases || []).find(item => String(item.buildId) === String(build.id))
    if (!release) { pipelineStages.value = []; deployTasks.value = []; return }
    pipelineStages.value = await getReleaseStages(release.id)
    deployTasks.value = (await getReleaseTasks(release.id)).filter(task => task.taskType === 'DEPLOY')
  } catch (error) {
    console.error('加载发布阶段失败:', error)
  }
}

const stageClass = (status) => ({
  SUCCESS: 'stage-success', RUNNING: 'stage-running', FAILED: 'stage-failed', SKIPPED: 'stage-skipped'
}[status] || 'stage-pending')

const stageStatusText = (status) => ({ SUCCESS: '成功', RUNNING: '执行中', FAILED: '失败', SKIPPED: '跳过', PENDING: '等待中' }[status] || status)

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

.pipeline { display: flex; align-items: flex-start; min-height: 170px; padding: 18px 12px 4px; overflow-x: auto; }
.pipeline-node { width: 58px; flex: 0 0 58px; color: #64748b; font-size: 13px; text-align: center; }
.node-dot { display: block; width: 12px; height: 12px; margin: 0 auto 8px; border: 2px solid #9db2cf; border-radius: 50%; background: #fff; }
.pipeline-track { display: flex; flex: 1; min-width: 500px; position: relative; margin-top: 0; }
.pipeline-track::before { content: ''; position: absolute; top: 6px; left: 0; right: 0; height: 2px; background: #a9beda; }
.pipeline-stage { position: relative; flex: 1; min-width: 130px; text-align: center; }
.stage-line { height: 1px; }
.stage-dot { position: relative; z-index: 1; width: 26px; height: 26px; line-height: 26px; margin: -7px auto 8px; border-radius: 50%; border: 1px solid #94a3b8; background: #fff; font-weight: 700; }
.stage-name { color: #1e293b; font-size: 13px; white-space: nowrap; }
.stage-status { color: #94a3b8; font-size: 11px; margin-top: 3px; }
.stage-success { color: #16a34a; border-color: #86efac; background: #dcfce7; }
.stage-running { color: #2563eb; border-color: #93c5fd; background: #dbeafe; }
.stage-failed { color: #dc2626; border-color: #fca5a5; background: #fee2e2; }
.stage-skipped { color: #64748b; border-color: #cbd5e1; background: #f1f5f9; }
.stage-tasks { margin: 22px auto 0; width: 90%; border-left: 2px solid #a9beda; border-right: 2px solid #a9beda; padding: 6px 0; }
.task-branch { display: flex; align-items: center; justify-content: center; gap: 5px; font-size: 11px; color: #475569; margin: 5px 0; }
.task-dot { width: 18px; height: 18px; line-height: 18px; border-radius: 50%; border: 1px solid #94a3b8; background: #fff; }

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
