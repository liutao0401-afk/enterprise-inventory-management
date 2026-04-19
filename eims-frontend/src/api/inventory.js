import request from './request'

export const inventoryApi = {
  inbound(data) {
    return request.post('/inventory/in', data)
  },
  outbound(data) {
    return request.post('/inventory/out', data)
  },
  log(params) {
    return request.get('/inventory/log', { params })
  },
  logByMaterial(materialId) {
    return request.get(`/inventory/log/${materialId}`)
  }
}
