import request from './request'

export const orderApi = {
  list(params) {
    return request.get('/order/list', { params })
  },
  get(id) {
    return request.get(`/order/${id}`)
  },
  items(id) {
    return request.get(`/order/${id}/items`)
  },
  create(data) {
    return request.post('/order', data)
  },
  addItems(id, items) {
    return request.post(`/order/${id}/items`, items)
  },
  confirm(id) {
    return request.put(`/order/${id}/confirm`)
  },
  cancel(id) {
    return request.put(`/order/${id}/cancel`)
  }
}
