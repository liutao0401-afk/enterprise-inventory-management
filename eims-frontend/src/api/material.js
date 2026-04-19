import request from './request'

export const materialApi = {
  list(params) {
    return request.get('/material/list', { params })
  },
  all() {
    return request.get('/material/all')
  },
  get(id) {
    return request.get(`/material/${id}`)
  },
  create(data) {
    return request.post('/material', data)
  },
  update(id, data) {
    return request.put(`/material/${id}`, data)
  },
  delete(id) {
    return request.delete(`/material/${id}`)
  },
  lowStock() {
    return request.get('/material/low-stock')
  }
}
