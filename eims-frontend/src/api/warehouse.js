import request from './request'

export const warehouseApi = {
  list() {
    return request.get('/warehouse/list')
  },
  get(id) {
    return request.get(`/warehouse/${id}`)
  },
  create(data) {
    return request.post('/warehouse', data)
  },
  update(id, data) {
    return request.put(`/warehouse/${id}`, data)
  },
  delete(id) {
    return request.delete(`/warehouse/${id}`)
  }
}
