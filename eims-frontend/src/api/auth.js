import request from './request'

export const authApi = {
  login(data) {
    return request.post('/auth/login', data)
  },
  logout() {
    return request.post('/auth/logout')
  },
  current() {
    return request.get('/auth/current')
  }
}
