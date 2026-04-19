import request from './request'

export const supplierApi = {
  list(params) {
    return request.get('/supplier/list', { params })
  },
  all() {
    return request.get('/supplier/all')
  },
  get(id) {
    return request.get(`/supplier/${id}`)
  },
  create(data) {
    return request.post('/supplier', data)
  },
  update(id, data) {
    return request.put(`/supplier/${id}`, data)
  },
  delete(id) {
    return request.delete(`/supplier/${id}`)
  }
}
