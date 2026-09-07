<template>
  <div class="release-page">
    <n-space vertical size="large">
      <n-space justify="space-between" align="center">
        <div>
          <h2 class="title">持续交付（CD）</h2>
          <n-text depth="3">从构建产物创建发布单，跟踪部署、健康检查和回滚状态</n-text>
        </div>
        <n-button @click="loadData">刷新</n-button>
      </n-space>

      <n-alert type="info" :show-icon="false">
        CI 负责生成镜像；CD 负责选择构建版本并完成部署。开启项目自动部署后，成功构建会自动进入发布流程。
      </n-alert>

      <n-card title="发布历史" :bordered="false">
        <n-data-table :columns="releaseColumns" :data="releases" :loading="loading" :pagination="false" />
        <n-empty v-if="!loading && releases.length === 0" description="暂无发布记录" />
      </n-card>

      <n-card title="发布流水线" :bordered="false" v-if="selectedRelease">
        <n-space vertical>
          <n-space align="center">
            <n-text strong>发布 #{{ selectedRelease.id }}</n-text>
            <n-text depth="3">镜像：{{ selectedRelease.targetImage || '等待构建产物' }}</n-text>
            <n-tag :type="statusType(selectedRelease.status)">{{ statusText(selectedRelease.status) }}</n-tag>
            <n-button v-if="selectedRelease.targetImage" size="small" type="primary" :loading="actionLoading" @click="openDeploy">部署到服务器</n-button>
            <n-button v-if="selectedRelease.targetImage" size="small" type="warning" :loading="actionLoading" @click="openRollback">回滚版本</n-button>
          </n-space>
          <div class="pipeline">
            <div v-for="(stage, index) in stages" :key="stage.id || stage.stageKey" class="stage-wrap">
              <div class="stage" :class="`stage-${stage.status?.toLowerCase()}`">
                <div class="stage-dot">{{ index + 1 }}</div>
                <div class="stage-name">{{ stage.stageName }}</div>
                <n-tag size="small" :type="statusType(stage.status)">{{ statusText(stage.status) }}</n-tag>
                <n-text v-if="stage.failureReason" type="error" depth="3">{{ stage.failureReason }}</n-text>
              </div>
              <div v-if="index < stages.length - 1" class="connector" />
            </div>
          </div>
          <n-divider />
          <n-text depth="3">部署任务：{{ tasks.length }} 个</n-text>
          <n-data-table v-if="tasks.length" :columns="taskColumns" :data="tasks" :pagination="false" size="small" />
          <n-empty v-else description="暂无部署任务" size="small" />
        </n-space>
      </n-card>

      <n-card v-if="deploymentHistory.length" title="部署历史" :bordered="false">
        <n-data-table :columns="deploymentColumns" :data="deploymentHistory" :pagination="false" size="small" />
      </n-card>

      <n-modal v-model:show="actionVisible" preset="dialog" :title="actionType === 'rollback' ? '回滚版本' : '部署版本'" :show-icon="false">
        <n-form label-placement="left" label-width="90">
          <n-form-item label="目标服务器"><n-select v-model:value="selectedServerId" :options="serverOptions" placeholder="请选择目标服务器" /></n-form-item>
          <n-form-item label="镜像版本">
            <n-select v-if="actionType === 'rollback'" v-model:value="rollbackReleaseId" :options="rollbackOptions" placeholder="请选择历史版本" />
            <n-input v-else v-model:value="actionImage" readonly />
          </n-form-item>
        </n-form>
        <template #action><n-space><n-button @click="actionVisible = false">取消</n-button><n-button :type="actionType === 'rollback' ? 'warning' : 'primary'" :loading="actionLoading" @click="executeAction">确认{{ actionType === 'rollback' ? '回滚' : '部署' }}</n-button></n-space></template>
      </n-modal>

      <n-card v-if="actionResult" title="最近一次操作结果" :bordered="false">
        <n-alert :type="actionResult.ok ? 'success' : 'error'" :show-icon="false">
          {{ actionResult.message }}
        </n-alert>
        <pre v-if="actionResult.log" class="action-log">{{ actionResult.log }}</pre>
      </n-card>

      <n-card title="从成功构建创建发布" :bordered="false">
        <n-data-table :columns="buildColumns" :data="successfulBuilds" :loading="buildLoading" :pagination="false" />
        <n-empty v-if="!buildLoading && successfulBuilds.length === 0" description="暂无成功构建" />
      </n-card>
    </n-space>
  </div>
</template>

<script setup>
import { h, onMounted, onUnmounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { NButton, NTag, useMessage } from 'naive-ui'
import { getBuildList } from '@/api/build'
import { createRelease, getReleaseList, getReleaseStages, getReleaseTasks } from '@/api/release'
import { getServerList } from '@/api/server'
import { deploy, rollback, getDeploymentList } from '@/api/deployment'
import { getCicdStatusMeta } from '@/utils/cicdStatus'

const route = useRoute()
const message = useMessage()
const loading = ref(false)
const buildLoading = ref(false)
const releases = ref([])
const successfulBuilds = ref([])
const stages = ref([])
const tasks = ref([])
const selectedRelease = ref(null)
const actionVisible = ref(false), actionLoading = ref(false), actionType = ref('deploy')
const actionImage = ref(''), selectedServerId = ref(null), serverOptions = ref([])
const rollbackReleaseId = ref(null), actionResult = ref(null)
const rollbackOptions = ref([])
const deploymentHistory = ref([])

const statusText = status => getCicdStatusMeta(status).text
const statusType = status => getCicdStatusMeta(status).type

const releaseColumns = [
  { title: '发布编号', key: 'id', width: 110 },
  { title: '环境', key: 'environment', width: 110 },
  { title: '目标镜像', key: 'targetImage', ellipsis: { tooltip: true } },
  { title: '状态', key: 'status', width: 100, render: row => h(NTag, { type: statusType(row.status), size: 'small' }, { default: () => statusText(row.status) }) },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 100, render: row => h(NButton, { text: true, type: 'primary', onClick: () => selectRelease(row) }, { default: () => '查看流水线' }) }
]
const taskColumns = [
  { title: '任务类型', key: 'taskType' },
  { title: '状态', key: 'status', render: row => h(NTag, { type: statusType(row.status), size: 'small' }, { default: () => statusText(row.status) }) },
  { title: '失败原因', key: 'failureReason' }
]
const deploymentColumns = [
  { title: '状态', key: 'status', width: 110, render: row => h(NTag, { type: statusType(row.status), size: 'small' }, { default: () => statusText(row.status) }) },
  { title: '目标服务器', key: 'serverId', width: 130 },
  { title: '镜像版本', key: 'newImage', ellipsis: { tooltip: true } },
  { title: '执行时间', key: 'deploymentTime', width: 180 },
  { title: '日志', key: 'deploymentLog', ellipsis: { tooltip: true } }
]
const buildColumns = [
  { title: '构建编号', key: 'id', width: 120 },
  { title: '镜像', key: 'image', ellipsis: { tooltip: true } },
  { title: 'Tag', key: 'imageTag', width: 180 },
  { title: '完成时间', key: 'endTime', width: 180 },
  { title: '操作', key: 'action', width: 120, render: row => h(NButton, { type: 'primary', size: 'small', onClick: () => handleCreate(row) }, { default: () => '创建发布' }) }
]

const loadData = async () => {
  loading.value = true; buildLoading.value = true
  try {
    const projectId = route.params.projectId ? Number(route.params.projectId) : undefined
    releases.value = await getReleaseList(projectId ? { projectId } : undefined)
    rollbackOptions.value = releases.value.filter(item => item.targetImage).map(item => ({ label: `发布 #${item.id} · ${item.targetImage}`, value: item.id, image: item.targetImage }))
    const builds = await getBuildList({ projectId, current: 1, size: 20, status: 'SUCCESS' })
    successfulBuilds.value = (builds.records || builds || []).filter(item => item.status === 'SUCCESS')
  } catch (error) { message.error(error.message || '加载发布数据失败') }
  finally { loading.value = false; buildLoading.value = false }
}
const selectRelease = async release => {
  selectedRelease.value = release
  try {
    stages.value = await getReleaseStages(release.id)
    tasks.value = await getReleaseTasks(release.id)
    const projectId = release.projectId || (route.params.projectId ? Number(route.params.projectId) : null)
    deploymentHistory.value = projectId ? await getDeploymentList(projectId) : []
  }
  catch (error) { message.error(error.message || '加载流水线失败') }
}
const handleCreate = async build => {
  try { const release = await createRelease(build.id); message.success('发布单已创建'); await loadData(); await selectRelease(release) }
  catch (error) { message.error(error.message || '创建发布失败') }
}
const loadServers = async () => {
  try { const data = await getServerList({ current: 1, size: 100 }); serverOptions.value = (data.records || data || []).map(item => ({ label: `${item.name} (${item.host})`, value: item.id })) }
  catch (error) { message.error(error.message || '加载服务器列表失败') }
}
const openDeploy = () => { actionType.value = 'deploy'; actionImage.value = selectedRelease.value.targetImage; selectedServerId.value = null; actionVisible.value = true; loadServers() }
const openRollback = () => { actionType.value = 'rollback'; rollbackReleaseId.value = null; selectedServerId.value = null; actionVisible.value = true; loadServers() }
const executeAction = async () => {
  if (!selectedServerId.value) return message.warning('请选择目标服务器')
  if (actionType.value === 'rollback' && !rollbackReleaseId.value) return message.warning('请选择要回滚的历史版本')
  const projectId = selectedRelease.value.projectId || (route.params.projectId ? Number(route.params.projectId) : null)
  if (!projectId) return message.error('无法确定项目，请从项目发布入口进入')
  const selectedRollback = rollbackOptions.value.find(item => item.value === rollbackReleaseId.value)
  const image = actionType.value === 'rollback' ? selectedRollback?.image : actionImage.value
  actionLoading.value = true
  try { const result = await (actionType.value === 'rollback' ? rollback : deploy)({ projectId, serverId: selectedServerId.value, image }); actionResult.value = { ok: true, message: actionType.value === 'rollback' ? '回滚任务已完成' : '部署任务已完成', log: result?.log || '' }; message.success(actionResult.value.message); actionVisible.value = false; await loadData(); await selectRelease(selectedRelease.value) }
  catch (error) { actionResult.value = { ok: false, message: error.message || '操作失败', log: error.response?.data?.message || '' }; message.error(actionResult.value.message) }
  finally { actionLoading.value = false }
}
let detailTimer
const refreshSelectedRelease = async () => {
  if (!selectedRelease.value || actionLoading.value) return
  await selectRelease(selectedRelease.value)
}
onMounted(async () => { await loadData(); detailTimer = window.setInterval(refreshSelectedRelease, 5000) })
onUnmounted(() => { if (detailTimer) window.clearInterval(detailTimer) })
</script>

<style scoped>
.title { margin: 0 0 6px; font-size: 22px; }
.pipeline { display: flex; align-items: flex-start; overflow-x: auto; padding: 20px 4px 8px; }
.stage-wrap { display: flex; align-items: center; min-width: 180px; }
.stage { display: flex; flex-direction: column; align-items: center; gap: 8px; min-width: 150px; text-align: center; }
.stage-dot { width: 30px; height: 30px; line-height: 30px; border-radius: 50%; color: #fff; background: #999; }
.stage-success .stage-dot { background: #18a058; }
.stage-running .stage-dot, .stage-deploying .stage-dot { background: #2080f0; }
.stage-failed .stage-dot { background: #d03050; }
.stage-name { font-weight: 600; }
.connector { width: 55px; height: 2px; background: #d9d9d9; }
.action-log { max-height: 220px; overflow: auto; margin: 12px 0 0; padding: 12px; background: #181818; color: #d8dee9; border-radius: 4px; white-space: pre-wrap; font: 12px/1.6 monospace; }
</style>
