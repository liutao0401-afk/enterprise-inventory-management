import request from './request'

export const requirementApi = {
  list(params) {
    return request.get('/requirement/list', { params })
  },
  get(id) {
    return request.get(`/requirement/${id}`)
  },
  create(data) {
    return request.post('/requirement', data)
  },
  approve(id, comment) {
    return request.put(`/requirement/${id}/approve`, null, { params: { comment } })
  },
  reject(id, comment) {
    return request.put(`/requirement/${id}/reject`, null, { params: { comment } })
  },
  pending() {
    return request.get('/requirement/pending')
  },
  checkDuplicate(data) {
    return request.post('/requirement/check-duplicate', data)
  }
}
