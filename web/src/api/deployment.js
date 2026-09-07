import request from './request'

export const deploy = data => request.post('/deployment/deploy', data)
export const rollback = data => request.post('/deployment/rollback', data)
export const getDeploymentList = projectId => request.get('/deployment/list', { params: { projectId } })
