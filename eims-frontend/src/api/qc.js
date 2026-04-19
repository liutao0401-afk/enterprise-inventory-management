import request from './request'

export const qcApi = {
  list(params) {
    return request.get('/qc/list', { params })
  },
  get(id) {
    return request.get(`/qc/${id}`)
  },
  create(data) {
    return request.post('/qc', data)
  },
  update(id, data) {
    return request.put(`/qc/${id}`, data)
  }
}
