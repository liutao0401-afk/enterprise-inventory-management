import request from './request'

export const alertApi = {
  getStats() {
    return request.get('/alert/stats')
  },
  check() {
    return request.post('/alert/check')
  },
  send(data) {
    return request.post('/alert/send', data)
  }
}
