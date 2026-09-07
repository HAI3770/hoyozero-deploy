export const CICD_STATUS_META = {
  PENDING: { text: '等待中', type: 'default', color: '#909399' },
  CREATED: { text: '待发布', type: 'default', color: '#909399' },
  RUNNING: { text: '运行中', type: 'info', color: '#2080f0' },
  DEPLOYING: { text: '部署中', type: 'info', color: '#2080f0' },
  SUCCESS: { text: '成功', type: 'success', color: '#18a058' },
  SUCCEEDED: { text: '成功', type: 'success', color: '#18a058' },
  FAILED: { text: '失败', type: 'error', color: '#d03050' },
  ROLLED_BACK: { text: '已回滚', type: 'warning', color: '#f0a020' },
  SKIPPED: { text: '已跳过', type: 'default', color: '#909399' },
  ONLINE: { text: '在线', type: 'success', color: '#18a058' },
  OFFLINE: { text: '离线', type: 'error', color: '#d03050' }
}

export const getCicdStatusMeta = status => CICD_STATUS_META[status] || {
  text: status || '未知', type: 'default', color: '#909399'
}

