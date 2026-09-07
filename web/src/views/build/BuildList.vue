<template>
  <div class="build-list">
    <div class="page-header">
      <div><h2>构建历史</h2><span>查看构建状态、构建产物与执行详情</span></div>
      <n-text depth="3">自动刷新：5 秒</n-text>
    </div>
    <div class="toolbar">
      <n-space>
        <n-select
          v-model:value="selectedProjectId"
          placeholder="选择项目"
          style="width: 200px"
          clearable
          :options="projectOptions"
          @update:value="loadData"
        />
        <n-button @click="loadData">
          <template #icon>
            <n-icon><RefreshSharp /></n-icon>
          </template>
          刷新
        </n-button>
      </n-space>
    </div>
    
    <n-data-table
      :columns="columns"
      :data="dataSource"
      :loading="loading"
      :pagination="false"
    />
    
    <div class="pagination-wrapper">
      <n-pagination
        v-model:page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :item-count="pagination.itemCount"
        :page-sizes="pagination.pageSizes"
        show-size-picker
        :on-update:page="handlePageChange"
        :on-update:page-size="handlePageSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage, NButton, NSpace, NTag, NPopconfirm } from 'naive-ui'
import { RefreshSharp, EyeSharp, TrashSharp } from '@vicons/ionicons5'
import { getBuildList, deleteBuild } from '@/api/build'
import { getProjectList } from '@/api/project'
import { getCicdStatusMeta } from '@/utils/cicdStatus'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const loading = ref(false)
const selectedProjectId = ref(null)

const dataSource = ref([])
const projectOptions = ref([])

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50]
})

const getEnvType = (env) => {
  const map = {
    'development': 'info',
    'test': 'warning',
    'production': 'error'
  }
  return map[env] || 'default'
}

const getEnvText = (env) => {
  const map = {
    'development': '开发环境',
    'test': '测试环境',
    'production': '生产环境'
  }
  return map[env] || env || '-'
}

const columns = [
  { title: '项目名称', key: 'projectName', width: 150 },
  { 
    title: '环境', 
    key: 'env', 
    width: 100,
    render: (row) => {
      if (!row.env) return '-'
      return h(
        NTag,
        {
          type: getEnvType(row.env),
          size: 'small'
        },
        { default: () => getEnvText(row.env) }
      )
    }
  },
  { 
    title: '状态', 
    key: 'status', 
    width: 100,
    render: (row) => {
      return h(
        NTag,
        {
          type: getStatusType(row.status),
          size: 'small'
        },
        { default: () => getStatusText(row.status) }
      )
    }
  },
  { title: '触发人', key: 'triggerByName', width: 120 },
  { title: '开始时间', key: 'startTime', width: 180 },
  { title: '结束时间', key: 'endTime', width: 180 },
  { 
    title: '耗时', 
    key: 'duration', 
    width: 100,
    render: (row) => row.duration ? `${row.duration}s` : '-'
  },
  { 
    title: '操作', 
    key: 'action', 
    width: 200,
    render: (row) => {
      return h(
        NSpace,
        {},
        {
          default: () => [
            h(
              NButton,
              {
                text: true,
                type: 'primary',
                size: 'small',
                onClick: () => viewDetail(row.id)
              },
              { 
                default: () => '查看详情',
                icon: () => h('i', { class: 'n-icon' }, [h(EyeSharp)])
              }
            ),
            h(
              NPopconfirm,
              {
                onPositiveClick: () => handleDelete(row.id)
              },
              {
                default: () => '确定要删除吗？',
                trigger: () => h(
                  NButton,
                  {
                    text: true,
                    type: 'error',
                    size: 'small'
                  },
                  { 
                    default: () => '删除',
                    icon: () => h('i', { class: 'n-icon' }, [h(TrashSharp)])
                  }
                )
              }
            )
          ]
        }
      )
    }
  }
]

const getStatusType = (status) => {
  return getCicdStatusMeta(status).type
}

const getStatusText = (status) => {
  return getCicdStatusMeta(status).text
}

const loadData = async () => {
  try {
    loading.value = true
    
    const params = {
      current: pagination.page,
      size: pagination.pageSize
    }
    
    if (selectedProjectId.value) {
      params.projectId = selectedProjectId.value
    }
    
    const data = await getBuildList(params)
    
    dataSource.value = data.records
    pagination.itemCount = data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadProjects = async () => {
  try {
    const data = await getProjectList({ current: 1, size: 100 })
    projectOptions.value = data.records.map(project => ({
      label: project.name,
      value: project.id
    }))
  } catch (error) {
    console.error(error)
  }
}

const handlePageChange = (page) => {
  pagination.page = page
  loadData()
}

const handlePageSizeChange = (pageSize) => {
  pagination.pageSize = pageSize
  pagination.page = 1
  loadData()
}

const viewDetail = (id) => {
  if (!id) {
    message.error('构建ID不存在')
    return
  }
  router.push(`/build-detail/${id}`)
}

const handleDelete = async (id) => {
  if (!id) {
    message.error('构建 ID不存在')
    return
  }
  
  try {
    await deleteBuild(id)
    message.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

let refreshTimer
let loadingRequest = false

const refreshData = async () => {
  if (loadingRequest) return
  loadingRequest = true
  try { await loadData() } finally { loadingRequest = false }
}

onMounted(() => {
  if (route.params.projectId) {
    selectedProjectId.value = parseInt(route.params.projectId)
  }
  
  loadProjects()
  refreshData()
  // 构建执行由 Runner 异步完成，页面打开期间自动轮询，避免必须手动点击刷新。
  refreshTimer = window.setInterval(refreshData, 5000)
})

onUnmounted(() => {
  if (refreshTimer) window.clearInterval(refreshTimer)
})
</script>

<style scoped>
.build-list {
  width: 100%;
}

.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 18px; }
.page-header h2 { margin: 0 0 6px; font-size: 22px; font-weight: 600; }
.page-header span { color: var(--n-text-color-3); font-size: 13px; }

.toolbar {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
