import request from './request'

// 获取登录日志列表
export const getLoginLogList = (params) => {
  return request.get('/log/login/list', { params })
}

// 删除登录日志
export const deleteLoginLog = (id) => {
  return request.delete(`/log/login/${id}`)
}

// 清空登录日志
export const clearLoginLog = () => {
  return request.delete('/log/login/clear')
}

// 获取操作日志列表
export const getOperationLogList = (params) => {
  return request.get('/log/operation/list', { params })
}

// 删除操作日志
export const deleteOperationLog = (id) => {
  return request.delete(`/log/operation/${id}`)
}

// 清空操作日志
export const clearOperationLog = () => {
  return request.delete('/log/operation/clear')
}
