import request from './request'

export const getReleaseList = (params) => request.get('/release/list', { params })
export const createRelease = (buildId) => request.post('/release', { buildId })
export const getRelease = (id) => request.get(`/release/${id}`)
export const getReleaseStages = (id) => request.get(`/release/${id}/stages`)
export const getReleaseTasks = (id) => request.get(`/release/${id}/tasks`)
