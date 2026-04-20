import request from './request'

export const duplicateCheckApi = {
  check(data) {
    return request.post('/duplicate-check/check', data)
  }
}

export const feishuApi = {
  getConfig() {
    return request.get('/feishu/config')
  },
  updateConfig(data) {
    return request.post('/feishu/config', data)
  },
  test() {
    return request.post('/feishu/test')
  }
}

export const aiConfigApi = {
  getConfig() {
    return request.get('/ai/config')
  },
  updateConfig(data) {
    return request.post('/ai/config', data)
  },
  test() {
    return request.get('/ai/test')
  }
}
