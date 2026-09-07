import request from './request'

export const chatWithAssistant = (data) => request.post('/ai/chat', data)
export const getAssistantOverview = () => request.get('/ai/overview')
