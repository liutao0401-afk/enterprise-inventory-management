import request from './request'

export const reportApi = {
  inventoryLedger(params) {
    return request.get('/report/inventory-ledger', { params })
  },
  purchaseSummary(params) {
    return request.get('/report/purchase-summary', { params })
  },
  deliveryRate(params) {
    return request.get('/report/delivery-rate', { params })
  },
  qualityRate(params) {
    return request.get('/report/quality-rate', { params })
  },
  turnoverRate(params) {
    return request.get('/report/turnover-rate', { params })
  },
  requirementFunnel() {
    return request.get('/report/requirement-funnel')
  },
  supplierPerformance() {
    return request.get('/report/supplier-performance')
  },
  inventoryFlow(params) {
    return request.get('/report/inventory-flow', { params })
  },
  pendingOrders() {
    return request.get('/report/pending-orders')
  }
}
