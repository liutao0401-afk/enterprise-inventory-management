import request from './request'

export const categoryApi = {
  list() {
    return request.get('/category/list')
  },
  tree() {
    return request.get('/category/tree')
  },
  get(id) {
    return request.get(`/category/${id}`)
  },
  create(data) {
    return request.post('/category', data)
  },
  update(id, data) {
    return request.put(`/category/${id}`, data)
  },
  delete(id) {
    return request.delete(`/category/${id}`)
  }
}
